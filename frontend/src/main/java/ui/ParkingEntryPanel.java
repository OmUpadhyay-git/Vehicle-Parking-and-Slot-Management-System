package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import service.ApiService;
import service.JsonHelper;

public class ParkingEntryPanel extends JPanel {

    private JTextField vehicleNumberField;
    private JButton searchBtn;
    private JLabel vehicleTypeLabel;
    private JComboBox<String> slotCombo;
    private JLabel entryTimeLabel;
    private JButton parkBtn;
    private JLabel statusLabel;
    private ApiService apiService;
    private ArrayList<String[]> availableSlots = new ArrayList<>();
    private int selectedSlotId = -1;

    public ParkingEntryPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Vehicle Entry");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel);
        add(topBar, BorderLayout.NORTH);

        // Main form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Vehicle Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Vehicle Number:"), gbc);
        gbc.gridx = 1;
        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        numPanel.setBackground(Color.WHITE);
        vehicleNumberField = new JTextField(15);
        vehicleNumberField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        numPanel.add(vehicleNumberField);
        searchBtn = new JButton("SEARCH");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        numPanel.add(searchBtn);
        formPanel.add(numPanel, gbc);

        // Vehicle Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        vehicleTypeLabel = new JLabel("-");
        vehicleTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(vehicleTypeLabel, gbc);

        // Available Slot
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Available Slot:"), gbc);
        gbc.gridx = 1;
        slotCombo = new JComboBox<>();
        slotCombo.setPreferredSize(new Dimension(200, 28));
        formPanel.add(slotCombo, gbc);

        // Entry Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Entry Time:"), gbc);
        gbc.gridx = 1;
        entryTimeLabel = new JLabel("Automatically Generated");
        entryTimeLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        entryTimeLabel.setForeground(Color.GRAY);
        formPanel.add(entryTimeLabel, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 4;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        parkBtn = new JButton("PARK VEHICLE");
        parkBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        parkBtn.setBackground(new Color(46, 204, 113));
        parkBtn.setForeground(Color.WHITE);
        parkBtn.setFocusPainted(false);
        parkBtn.setPreferredSize(new Dimension(180, 35));
        formPanel.add(parkBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Search action
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchVehicle();
            }
        });

        vehicleNumberField.addActionListener(e -> searchVehicle());

        // Park action
        parkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parkVehicle();
            }
        });

        loadAvailableSlots();
    }

    private void searchVehicle() {
        String number = vehicleNumberField.getText().trim();
        if (number.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a vehicle number.");
            return;
        }

        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText("Searching...");

        new Thread(() -> {
            String response = apiService.searchVehicle(number);
            SwingUtilities.invokeLater(() -> {
                try {
                    if (response.contains("\"success\":true")) {
                        int dataIndex = response.indexOf("\"data\":[");
                        if (dataIndex != -1) {
                            int arrStart = response.indexOf("[", dataIndex);
                            int arrEnd = response.indexOf("]", arrStart);
                            String arrContent = response.substring(arrStart + 1, arrEnd).trim();
                            if (!arrContent.isEmpty()) {
                                int objStart = arrContent.indexOf("{");
                                int objEnd = arrContent.indexOf("}", objStart);
                                String obj = arrContent.substring(objStart, objEnd + 1);
                                String vType = JsonHelper.extractField(obj, "vehicle_type");
                                vehicleTypeLabel.setText(vType);
                                statusLabel.setForeground(new Color(46, 204, 113));
                                statusLabel.setText("Vehicle found.");
                                loadAvailableSlotsForType(vType);
                                return;
                            }
                        }
                    }
                    vehicleTypeLabel.setText("-");
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Vehicle not found. Please add the vehicle first.");
                    slotCombo.removeAllItems();
                } catch (Exception e) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Error searching for vehicle.");
                }
            });
        }).start();
    }

    private void loadAvailableSlots() {
        new Thread(() -> {
            String response = apiService.getAvailableSlots();
            SwingUtilities.invokeLater(() -> {
                parseAvailableSlots(response);
            });
        }).start();
    }

    private void loadAvailableSlotsForType(String vehicleType) {
        new Thread(() -> {
            String response = apiService.getAvailableSlots();
            SwingUtilities.invokeLater(() -> {
                availableSlots.clear();
                slotCombo.removeAllItems();
                try {
                    if (!response.contains("\"success\":true")) return;

                    int dataIndex = response.indexOf("\"data\":[");
                    if (dataIndex == -1) return;
                    int arrStart = response.indexOf("[", dataIndex);
                    int arrEnd = response.lastIndexOf("]");
                    String arrContent = response.substring(arrStart + 1, arrEnd).trim();
                    if (arrContent.isEmpty()) return;

                    int i = 0;
                    while (i < arrContent.length()) {
                        int objStart = arrContent.indexOf("{", i);
                        if (objStart == -1) break;
                        int objEnd = arrContent.indexOf("}", objStart);
                        if (objEnd == -1) break;

                        String obj = arrContent.substring(objStart, objEnd + 1);
                        String slotNum = JsonHelper.extractField(obj, "slot_number");
                        String slotType = JsonHelper.extractField(obj, "vehicle_type");
                        int slotId = JsonHelper.extractJsonInt(obj, "slot_id");

                        if (slotType.equalsIgnoreCase(vehicleType)) {
                            availableSlots.add(new String[]{slotNum, slotType, String.valueOf(slotId)});
                            slotCombo.addItem(slotNum + " - " + slotType);
                        }
                        i = objEnd + 1;
                    }
                } catch (Exception e) {
                    // Keep empty
                }
            });
        }).start();
    }

    private void parseAvailableSlots(String response) {
        availableSlots.clear();
        slotCombo.removeAllItems();
        try {
            if (!response.contains("\"success\":true")) return;

            int dataIndex = response.indexOf("\"data\":[");
            if (dataIndex == -1) return;
            int arrStart = response.indexOf("[", dataIndex);
            int arrEnd = response.lastIndexOf("]");
            String arrContent = response.substring(arrStart + 1, arrEnd).trim();
            if (arrContent.isEmpty()) return;

            int i = 0;
            while (i < arrContent.length()) {
                int objStart = arrContent.indexOf("{", i);
                if (objStart == -1) break;
                int objEnd = arrContent.indexOf("}", objStart);
                if (objEnd == -1) break;

                String obj = arrContent.substring(objStart, objEnd + 1);
                String slotNum = JsonHelper.extractField(obj, "slot_number");
                String slotType = JsonHelper.extractField(obj, "vehicle_type");
                int slotId = JsonHelper.extractJsonInt(obj, "slot_id");

                availableSlots.add(new String[]{slotNum, slotType, String.valueOf(slotId)});
                slotCombo.addItem(slotNum + " - " + slotType);
                i = objEnd + 1;
            }
        } catch (Exception e) {
            // Keep empty
        }
    }

    private void parkVehicle() {
        String number = vehicleNumberField.getText().trim();
        int selectedIndex = slotCombo.getSelectedIndex();

        if (number.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a vehicle number.");
            return;
        }

        if (vehicleTypeLabel.getText().equals("-")) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please search for a vehicle first.");
            return;
        }

        if (selectedIndex == -1 || selectedIndex >= availableSlots.size()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("No available slot selected.");
            return;
        }

        String slotDisplay = (String) slotCombo.getSelectedItem();
        String slotNumber = slotDisplay.split(" - ")[0];
        int slotId = Integer.parseInt(availableSlots.get(selectedIndex)[2]);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Vehicle Parking\n\nVehicle: " + number + "\nSlot: " + slotNumber,
                "Confirm Parking",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            parkBtn.setEnabled(false);
            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText("Processing...");

            new Thread(() -> {
                String response = apiService.parkVehicle(number, slotId);
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (response.contains("\"success\":true")) {
                            statusLabel.setForeground(new Color(46, 204, 113));
                            statusLabel.setText("Vehicle parked successfully. Slot " + slotNumber + " assigned.");
                            vehicleNumberField.setText("");
                            vehicleTypeLabel.setText("-");
                            loadAvailableSlots();
                        } else {
                            String message = JsonHelper.extractMessage(response);
                            statusLabel.setForeground(Color.RED);
                            statusLabel.setText(message.isEmpty() ? "Failed to park vehicle" : message);
                        }
                    } catch (Exception e) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Error parking vehicle.");
                    } finally {
                        parkBtn.setEnabled(true);
                    }
                });
            }).start();
        }
    }
}
