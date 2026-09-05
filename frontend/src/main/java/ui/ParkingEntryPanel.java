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
    private boolean vehicleFound = false;

    // New vehicle registration fields
    private JPanel newVehiclePanel;
    private JComboBox<String> vehicleTypeCombo;
    private JTextField ownerNameField;
    private JTextField ownerPhoneField;

    public ParkingEntryPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Vehicle Entry");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.setContentAreaFilled(true);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> resetForm());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

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
        searchBtn.setOpaque(true);
        searchBtn.setContentAreaFilled(true);
        searchBtn.setBorderPainted(false);
        numPanel.add(searchBtn);
        formPanel.add(numPanel, gbc);

        // Vehicle Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        vehicleTypeLabel = new JLabel("-");
        vehicleTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(vehicleTypeLabel, gbc);

        // New Vehicle Registration Panel (hidden by default)
        newVehiclePanel = new JPanel(new GridBagLayout());
        newVehiclePanel.setBackground(new Color(255, 249, 230));
        newVehiclePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 200, 0)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        GridBagConstraints ngbc = new GridBagConstraints();
        ngbc.insets = new Insets(4, 4, 4, 4);
        ngbc.anchor = GridBagConstraints.WEST;
        ngbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel newVehicleTitle = new JLabel("New Vehicle - Enter Details:");
        newVehicleTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        newVehicleTitle.setForeground(new Color(180, 100, 0));
        ngbc.gridx = 0; ngbc.gridy = 0; ngbc.gridwidth = 2;
        newVehiclePanel.add(newVehicleTitle, ngbc);
        ngbc.gridwidth = 1;

        ngbc.gridx = 0; ngbc.gridy = 1;
        newVehiclePanel.add(new JLabel("Type:"), ngbc);
        ngbc.gridx = 1;
        vehicleTypeCombo = new JComboBox<>(new String[]{"CAR", "BIKE"});
        vehicleTypeCombo.setPreferredSize(new Dimension(150, 28));
        newVehiclePanel.add(vehicleTypeCombo, ngbc);

        ngbc.gridx = 0; ngbc.gridy = 2;
        newVehiclePanel.add(new JLabel("Owner Name:"), ngbc);
        ngbc.gridx = 1;
        ownerNameField = new JTextField(15);
        ownerNameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        newVehiclePanel.add(ownerNameField, ngbc);

        ngbc.gridx = 0; ngbc.gridy = 3;
        newVehiclePanel.add(new JLabel("Owner Phone:"), ngbc);
        ngbc.gridx = 1;
        ownerPhoneField = new JTextField(15);
        ownerPhoneField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        newVehiclePanel.add(ownerPhoneField, ngbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(newVehiclePanel, gbc);
        gbc.gridwidth = 1;
        newVehiclePanel.setVisible(false);

        // Available Slot
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Available Slot:"), gbc);
        gbc.gridx = 1;
        slotCombo = new JComboBox<>();
        slotCombo.setPreferredSize(new Dimension(200, 28));
        formPanel.add(slotCombo, gbc);

        // Entry Time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Entry Time:"), gbc);
        gbc.gridx = 1;
        entryTimeLabel = new JLabel("Automatically Generated");
        entryTimeLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        entryTimeLabel.setForeground(Color.GRAY);
        formPanel.add(entryTimeLabel, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 5;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        parkBtn = new JButton("PARK VEHICLE");
        parkBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        parkBtn.setBackground(new Color(46, 204, 113));
        parkBtn.setForeground(Color.WHITE);
        parkBtn.setFocusPainted(false);
        parkBtn.setOpaque(true);
        parkBtn.setContentAreaFilled(true);
        parkBtn.setBorderPainted(false);
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
                                vehicleTypeLabel.setForeground(new Color(46, 204, 113));
                                vehicleFound = true;
                                newVehiclePanel.setVisible(false);
                                statusLabel.setForeground(new Color(46, 204, 113));
                                statusLabel.setText("Vehicle found.");
                                loadAvailableSlotsForType(vType);
                                return;
                            }
                        }
                    }
                    // Vehicle not found - show registration fields
                    vehicleTypeLabel.setText("Not registered");
                    vehicleTypeLabel.setForeground(new Color(192, 57, 43));
                    vehicleFound = false;
                    newVehiclePanel.setVisible(true);
                    statusLabel.setForeground(new Color(180, 100, 0));
                    statusLabel.setText("Vehicle not found. Enter details to register and park.");
                    loadAllAvailableSlots();
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

    private void loadAllAvailableSlots() {
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

                        availableSlots.add(new String[]{slotNum, slotType, String.valueOf(slotId)});
                        slotCombo.addItem(slotNum + " - " + slotType);
                        i = objEnd + 1;
                    }
                } catch (Exception e) {
                    // Keep empty
                }
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

        if (selectedIndex == -1 || selectedIndex >= availableSlots.size()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("No available slot selected.");
            return;
        }

        String slotDisplay = (String) slotCombo.getSelectedItem();
        String slotNumber = slotDisplay.split(" - ")[0];
        int slotId = Integer.parseInt(availableSlots.get(selectedIndex)[2]);
        String slotType = availableSlots.get(selectedIndex)[1];

        String vehicleType = null;
        String ownerName = null;
        String ownerPhone = null;

        if (!vehicleFound) {
            vehicleType = (String) vehicleTypeCombo.getSelectedItem();
            ownerName = ownerNameField.getText().trim();
            ownerPhone = ownerPhoneField.getText().trim();

            if (ownerName.isEmpty() || ownerPhone.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please fill in owner name and phone.");
                return;
            }

            if (!vehicleType.equalsIgnoreCase(slotType)) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Vehicle type (" + vehicleType + ") does not match slot type (" + slotType + ").");
                return;
            }
        }

        String confirmMsg = "Confirm Vehicle Parking\n\nVehicle: " + number + "\nSlot: " + slotNumber;
        if (!vehicleFound) {
            confirmMsg += "\nType: " + vehicleType + "\nOwner: " + ownerName;
        }

        int confirm = JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm Parking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            parkBtn.setEnabled(false);
            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText("Processing...");

            String finalVehicleType = vehicleType;
            String finalOwnerName = ownerName;
            String finalOwnerPhone = ownerPhone;

            new Thread(() -> {
                String response = apiService.parkVehicleDirect(number, slotId, finalVehicleType, finalOwnerName, finalOwnerPhone);
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (response.contains("\"success\":true")) {
                            statusLabel.setForeground(new Color(46, 204, 113));
                            statusLabel.setText("Vehicle parked successfully. Slot " + slotNumber + " assigned.");
                            resetForm();
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

    private void resetForm() {
        vehicleNumberField.setText("");
        vehicleTypeLabel.setText("-");
        vehicleTypeLabel.setForeground(Color.BLACK);
        vehicleFound = false;
        newVehiclePanel.setVisible(false);
        ownerNameField.setText("");
        ownerPhoneField.setText("");
        statusLabel.setText(" ");
        slotCombo.removeAllItems();
        loadAvailableSlots();
    }
}
