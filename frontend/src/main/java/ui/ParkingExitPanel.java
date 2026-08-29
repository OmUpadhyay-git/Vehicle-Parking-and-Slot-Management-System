package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import service.ApiService;
import service.JsonHelper;

public class ParkingExitPanel extends JPanel {

    private JTextField vehicleNumberField;
    private JButton searchBtn;
    private JLabel vehicleTypeValue;
    private JLabel slotValue;
    private JLabel entryTimeValue;
    private JLabel exitTimeValue;
    private JLabel durationValue;
    private JLabel feeValue;
    private JComboBox<String> paymentMethodCombo;
    private JButton exitBtn;
    private JLabel statusLabel;
    private ApiService apiService;
    private int currentRecordId = -1;
    private double currentFee = 0;

    public ParkingExitPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Vehicle Exit");
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
        vehicleTypeValue = new JLabel("-");
        vehicleTypeValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(vehicleTypeValue, gbc);

        // Parking Slot
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Parking Slot:"), gbc);
        gbc.gridx = 1;
        slotValue = new JLabel("-");
        slotValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(slotValue, gbc);

        // Entry Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Entry Time:"), gbc);
        gbc.gridx = 1;
        entryTimeValue = new JLabel("-");
        entryTimeValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(entryTimeValue, gbc);

        // Exit Time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Exit Time:"), gbc);
        gbc.gridx = 1;
        exitTimeValue = new JLabel("Automatically Generated");
        exitTimeValue.setFont(new Font("SansSerif", Font.ITALIC, 12));
        exitTimeValue.setForeground(Color.GRAY);
        formPanel.add(exitTimeValue, gbc);

        // Duration
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        durationValue = new JLabel("-");
        durationValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(durationValue, gbc);

        // Fee
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Parking Fee:"), gbc);
        gbc.gridx = 1;
        feeValue = new JLabel("-");
        feeValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        feeValue.setForeground(new Color(39, 174, 96));
        formPanel.add(feeValue, gbc);

        // Payment Method
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        paymentMethodCombo = new JComboBox<>(new String[]{"CASH", "UPI", "CARD"});
        paymentMethodCombo.setPreferredSize(new Dimension(200, 28));
        formPanel.add(paymentMethodCombo, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 8;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        exitBtn = new JButton("COMPLETE EXIT");
        exitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitBtn.setBackground(new Color(231, 76, 60));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setPreferredSize(new Dimension(180, 35));
        exitBtn.setEnabled(false);
        formPanel.add(exitBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Search action
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchActiveParking();
            }
        });

        vehicleNumberField.addActionListener(e -> searchActiveParking());

        // Exit action
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeExit();
            }
        });
    }

    private void searchActiveParking() {
        String number = vehicleNumberField.getText().trim();
        if (number.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a vehicle number.");
            return;
        }

        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText("Searching...");

        new Thread(() -> {
            String response = apiService.getActiveParking();
            SwingUtilities.invokeLater(() -> {
                try {
                    if (!response.contains("\"success\":true")) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("No active parking records found.");
                        return;
                    }

                    int dataIndex = response.indexOf("\"data\":[");
                    if (dataIndex == -1) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("No active parking records found.");
                        return;
                    }

                    int arrStart = response.indexOf("[", dataIndex);
                    int arrEnd = response.lastIndexOf("]");
                    String arrContent = response.substring(arrStart + 1, arrEnd).trim();
                    if (arrContent.isEmpty()) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("No active parking records found.");
                        return;
                    }

                    boolean found = false;
                    int i = 0;
                    while (i < arrContent.length()) {
                        int objStart = arrContent.indexOf("{", i);
                        if (objStart == -1) break;
                        int objEnd = arrContent.indexOf("}", objStart);
                        if (objEnd == -1) break;

                        String obj = arrContent.substring(objStart, objEnd + 1);
                        String vNum = JsonHelper.extractField(obj, "vehicle_number");

                        if (vNum.equalsIgnoreCase(number)) {
                            int recordId = JsonHelper.extractJsonInt(obj, "record_id");
                            String vType = JsonHelper.extractField(obj, "vehicle_type");
                            String slotNum = JsonHelper.extractField(obj, "slot_number");
                            String entryTime = JsonHelper.extractField(obj, "entry_time");

                            currentRecordId = recordId;

                            vehicleTypeValue.setText(vType);
                            slotValue.setText(slotNum);
                            entryTimeValue.setText(JsonHelper.formatDateTime(entryTime));
                            exitTimeValue.setText("Now");
                            durationValue.setText("Calculating...");
                            feeValue.setText("Calculating...");

                            statusLabel.setForeground(new Color(46, 204, 113));
                            statusLabel.setText("Active parking record found.");
                            exitBtn.setEnabled(true);
                            found = true;

                            // Calculate estimated fee
                            calculateEstimatedFee(entryTime, vType);
                            break;
                        }
                        i = objEnd + 1;
                    }

                    if (!found) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("No active parking found for this vehicle.");
                        resetForm();
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Error searching for parking record.");
                }
            });
        }).start();
    }

    private void calculateEstimatedFee(String entryTimeStr, String vehicleType) {
        try {
            String timePart = entryTimeStr.substring(11, 19);
            String[] parts = timePart.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            java.time.LocalDateTime entryTime = java.time.LocalDateTime.of(
                    java.time.LocalDate.now(),
                    java.time.LocalTime.of(hours, minutes)
            );
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            long durationMinutes = java.time.Duration.between(entryTime, now).toMinutes();

            if (durationMinutes < 1) durationMinutes = 1;

            int chargedHours = (int) Math.ceil(durationMinutes / 60.0);
            double fee;
            if (vehicleType.equalsIgnoreCase("CAR")) {
                fee = chargedHours <= 1 ? 40 : 40 + (chargedHours - 1) * 20;
            } else {
                fee = chargedHours <= 1 ? 20 : 20 + (chargedHours - 1) * 10;
            }

            long hrs = durationMinutes / 60;
            long mins = durationMinutes % 60;
            durationValue.setText(hrs + "h " + mins + "m (charged: " + chargedHours + "h)");
            feeValue.setText("Rs. " + String.format("%.0f", fee));
            currentFee = fee;
        } catch (Exception e) {
            durationValue.setText("Unable to calculate");
            feeValue.setText("-");
        }
    }

    private void resetForm() {
        vehicleTypeValue.setText("-");
        slotValue.setText("-");
        entryTimeValue.setText("-");
        exitTimeValue.setText("Automatically Generated");
        durationValue.setText("-");
        feeValue.setText("-");
        exitBtn.setEnabled(false);
        currentRecordId = -1;
        currentFee = 0;
    }

    private void completeExit() {
        if (currentRecordId == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("No active parking record found.");
            return;
        }

        String number = vehicleNumberField.getText().trim();
        String method = (String) paymentMethodCombo.getSelectedItem();
        String fee = feeValue.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Vehicle Exit\n\nVehicle: " + number + "\nTotal Fee: " + fee + "\nPayment Method: " + method,
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            exitBtn.setEnabled(false);
            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText("Processing exit...");

            new Thread(() -> {
                String response = apiService.exitVehicle(currentRecordId, method);
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (response.contains("\"success\":true")) {
                            statusLabel.setForeground(new Color(46, 204, 113));
                            statusLabel.setText("Vehicle exit completed. Payment recorded.");
                            resetForm();
                            vehicleNumberField.setText("");
                        } else {
                            String message = JsonHelper.extractMessage(response);
                            statusLabel.setForeground(Color.RED);
                            statusLabel.setText(message.isEmpty() ? "Failed to process exit" : message);
                            exitBtn.setEnabled(true);
                        }
                    } catch (Exception e) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Error processing exit.");
                        exitBtn.setEnabled(true);
                    }
                });
            }).start();
        }
    }
}
