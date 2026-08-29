package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import service.ApiService;
import service.JsonHelper;

public class PaymentPanel extends JPanel {

    private JLabel vehicleNumberValue;
    private JLabel slotValue;
    private JLabel feeValue;
    private JComboBox<String> paymentMethodCombo;
    private JButton payBtn;
    private JLabel statusLabel;
    private ApiService apiService;
    private int currentRecordId = -1;
    private double currentFee = 0;

    public PaymentPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Payment");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel);
        add(topBar, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Vehicle Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Vehicle Number:"), gbc);
        gbc.gridx = 1;
        vehicleNumberValue = new JLabel("-");
        vehicleNumberValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(vehicleNumberValue, gbc);

        // Slot
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Slot:"), gbc);
        gbc.gridx = 1;
        slotValue = new JLabel("-");
        slotValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(slotValue, gbc);

        // Fee
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Parking Fee:"), gbc);
        gbc.gridx = 1;
        feeValue = new JLabel("-");
        feeValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        feeValue.setForeground(new Color(39, 174, 96));
        formPanel.add(feeValue, gbc);

        // Payment Method
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "UPI", "Card"});
        paymentMethodCombo.setPreferredSize(new Dimension(200, 30));
        formPanel.add(paymentMethodCombo, gbc);

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
        payBtn = new JButton("PAY");
        payBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        payBtn.setBackground(new Color(46, 204, 113));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        payBtn.setPreferredSize(new Dimension(150, 40));
        formPanel.add(payBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        payBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
    }

    public void setPaymentInfo(int recordId, String vehicleNumber, String slot, double fee) {
        this.currentRecordId = recordId;
        this.currentFee = fee;
        vehicleNumberValue.setText(vehicleNumber);
        slotValue.setText(slot);
        feeValue.setText("Rs. " + String.format("%.0f", fee));
    }

    private void processPayment() {
        String vehicle = vehicleNumberValue.getText();
        String fee = feeValue.getText();
        String method = (String) paymentMethodCombo.getSelectedItem();

        if (vehicle.equals("-")) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("No payment information available.");
            return;
        }

        if (currentRecordId == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("No parking record found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Payment\n\nVehicle: " + vehicle + "\nAmount: " + fee + "\nMethod: " + method,
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            payBtn.setEnabled(false);
            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText("Processing payment...");

            String paymentMethod = method.toUpperCase();
            new Thread(() -> {
                String response = apiService.createPayment(currentRecordId, currentFee, paymentMethod);
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (response.contains("\"success\":true")) {
                            statusLabel.setForeground(new Color(46, 204, 113));
                            statusLabel.setText("Payment successful. Vehicle exit completed.");
                            JOptionPane.showMessageDialog(this,
                                    "Payment Successful\nVehicle Exit Completed\nSlot is now Available",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String message = JsonHelper.extractMessage(response);
                            statusLabel.setForeground(Color.RED);
                            statusLabel.setText(message.isEmpty() ? "Payment failed" : message);
                        }
                    } catch (Exception e) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Error processing payment.");
                    } finally {
                        payBtn.setEnabled(true);
                    }
                });
            }).start();
        }
    }
}
