package ui;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.ApiService;
import service.JsonHelper;

public class SlotDetailsDialog extends JDialog {

    private int slotId;
    private String slotNumber;
    private String vehicleType;
    private String status;
    private ApiService apiService;
    private Runnable onRefresh;
    private boolean isEditMode = false;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel viewPanel;
    private JPanel editPanel;

    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;
    private JButton exitButton;

    private JLabel slotNumberValue;
    private JLabel vehicleTypeValue;
    private JLabel statusValue;

    private JTextField editOwnerName;
    private JTextField editOwnerPhone;

    private JLabel vehicleNumberVal;
    private JLabel vehicleTypeVal;
    private JLabel ownerNameVal;
    private JLabel ownerPhoneVal;
    private JLabel entryTimeVal;
    private JLabel exitTimeVal;
    private JLabel durationVal;
    private JLabel recordIdVal;
    private JLabel amountVal;
    private JLabel paymentMethodVal;
    private JLabel paymentStatusVal;
    private JLabel paymentTimeVal;
    private JLabel paymentIdVal;

    private JPanel vehicleInfoPanel;
    private JPanel parkingInfoPanel;
    private JPanel paymentInfoPanel;
    private JPanel noVehiclePanel;

    private int currentVehicleId = -1;
    private int currentRecordId = -1;
    private int currentPaymentId = -1;

    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SECTION_HEADER_COLOR = new Color(44, 62, 80);
    private static final Color AVAILABLE_COLOR = new Color(39, 174, 96);
    private static final Color OCCUPIED_COLOR = new Color(231, 76, 60);
    private static final Color EXIT_COLOR = new Color(230, 126, 34);

    public SlotDetailsDialog(Frame owner, int slotId, String slotNumber, String vehicleType,
                             String status, Runnable onRefresh) {
        super(owner, "Slot Details - " + slotNumber, true);
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
        this.status = status;
        this.apiService = new ApiService();
        this.onRefresh = onRefresh;

        initUI();
        loadSlotData();
    }

    private void initUI() {
        setSize(420, 550);
        setMinimumSize(new Dimension(400, 450));
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        viewPanel = createViewPanel();
        editPanel = createEditPanel();

        cardPanel.add(viewPanel, "VIEW");
        cardPanel.add(editPanel, "EDIT");

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT_COLOR);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("Slot Details - " + slotNumber);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(ACCENT_COLOR);
        closeBtn.setOpaque(true);
        closeBtn.setContentAreaFilled(true);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        panel.add(createSlotInfoSection());
        panel.add(Box.createVerticalStrut(10));
        vehicleInfoPanel = createVehicleInfoSection();
        panel.add(vehicleInfoPanel);
        panel.add(Box.createVerticalStrut(10));
        parkingInfoPanel = createParkingInfoSection();
        panel.add(parkingInfoPanel);
        panel.add(Box.createVerticalStrut(10));
        paymentInfoPanel = createPaymentInfoSection();
        panel.add(paymentInfoPanel);

        noVehiclePanel = new JPanel(new BorderLayout());
        noVehiclePanel.setBackground(new Color(250, 250, 250));
        noVehiclePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 10, 15, 10)));
        noVehiclePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel noVehicleLabel = new JLabel("No vehicle currently parked.");
        noVehicleLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        noVehicleLabel.setForeground(new Color(150, 150, 150));
        noVehicleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noVehiclePanel.add(noVehicleLabel, BorderLayout.CENTER);
        panel.add(noVehiclePanel);

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        panel.add(createEditSlotInfoSection());
        panel.add(Box.createVerticalStrut(10));
        panel.add(createEditVehicleInfoSection());

        return panel;
    }

    private JPanel createSectionHeader(String title) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        header.setBackground(new Color(250, 250, 250));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel icon = new JLabel("\u2139");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        icon.setForeground(ACCENT_COLOR);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(SECTION_HEADER_COLOR);

        header.add(icon);
        header.add(label);
        return header;
    }

    private JPanel createSlotInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        section.add(createSectionHeader("Slot Information"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(3, 2, 5, 5));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        grid.add(createInfoLabel("Slot Number:"));
        slotNumberValue = createValueLabel(slotNumber);
        grid.add(slotNumberValue);

        grid.add(createInfoLabel("Vehicle Type:"));
        vehicleTypeValue = createValueLabel(vehicleType);
        grid.add(vehicleTypeValue);

        grid.add(createInfoLabel("Status:"));
        statusValue = createStatusLabel(status);
        grid.add(statusValue);

        section.add(grid);
        return section;
    }

    private JPanel createVehicleInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        section.add(createSectionHeader("Vehicle Information"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(4, 2, 5, 5));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        grid.add(createInfoLabel("Vehicle Number:"));
        vehicleNumberVal = createValueLabel("-");
        grid.add(vehicleNumberVal);

        grid.add(createInfoLabel("Vehicle Type:"));
        vehicleTypeVal = createValueLabel("-");
        grid.add(vehicleTypeVal);

        grid.add(createInfoLabel("Owner Name:"));
        ownerNameVal = createValueLabel("-");
        grid.add(ownerNameVal);

        grid.add(createInfoLabel("Owner Phone:"));
        ownerPhoneVal = createValueLabel("-");
        grid.add(ownerPhoneVal);

        section.add(grid);
        return section;
    }

    private JPanel createParkingInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        section.add(createSectionHeader("Parking Information"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(4, 2, 5, 5));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        grid.add(createInfoLabel("Entry Time:"));
        entryTimeVal = createValueLabel("-");
        grid.add(entryTimeVal);

        grid.add(createInfoLabel("Exit Time:"));
        exitTimeVal = createValueLabel("-");
        grid.add(exitTimeVal);

        grid.add(createInfoLabel("Duration:"));
        durationVal = createValueLabel("-");
        grid.add(durationVal);

        grid.add(createInfoLabel("Parking Record ID:"));
        recordIdVal = createValueLabel("-");
        grid.add(recordIdVal);

        section.add(grid);
        return section;
    }

    private JPanel createPaymentInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        section.add(createSectionHeader("Payment Information"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(5, 2, 5, 5));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        grid.add(createInfoLabel("Amount:"));
        amountVal = createValueLabel("-");
        grid.add(amountVal);

        grid.add(createInfoLabel("Payment Method:"));
        paymentMethodVal = createValueLabel("-");
        grid.add(paymentMethodVal);

        grid.add(createInfoLabel("Payment Status:"));
        paymentStatusVal = createStatusLabel("-");
        grid.add(paymentStatusVal);

        grid.add(createInfoLabel("Payment Time:"));
        paymentTimeVal = createValueLabel("-");
        grid.add(paymentTimeVal);

        grid.add(createInfoLabel("Payment ID:"));
        paymentIdVal = createValueLabel("-");
        grid.add(paymentIdVal);

        section.add(grid);
        return section;
    }

    private JPanel createEditSlotInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));

        section.add(createSectionHeader("Slot Information"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(3, 2, 5, 5));
        grid.setOpaque(false);

        grid.add(createInfoLabel("Slot Number:"));
        grid.add(createValueLabel(slotNumber));

        grid.add(createInfoLabel("Vehicle Type:"));
        grid.add(createValueLabel(vehicleType));

        grid.add(createInfoLabel("Status:"));
        JLabel editStatusLabel = createStatusLabel(status);
        editStatusLabel.setText(status + " (Read Only)");
        grid.add(editStatusLabel);

        section.add(grid);
        return section;
    }

    private JPanel createEditVehicleInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));

        section.add(createSectionHeader("Vehicle Information (Editable)"));
        section.add(Box.createVerticalStrut(8));

        JPanel grid = new JPanel(new GridLayout(2, 2, 5, 10));
        grid.setOpaque(false);

        grid.add(createInfoLabel("Owner Name:"));
        editOwnerName = createTextField("");
        grid.add(editOwnerName);

        grid.add(createInfoLabel("Owner Phone:"));
        editOwnerPhone = createTextField("");
        grid.add(editOwnerPhone);

        section.add(grid);
        return section;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        editButton = createStyledButton("Edit", ACCENT_COLOR);
        saveButton = createStyledButton("Save", new Color(39, 174, 96));
        cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
        closeButton = createStyledButton("Close", new Color(100, 100, 100));
        exitButton = createStyledButton("Exit Vehicle", EXIT_COLOR);

        editButton.addActionListener(e -> enterEditMode());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> exitEditMode());
        closeButton.addActionListener(e -> dispose());
        exitButton.addActionListener(e -> processExit());

        panel.add(editButton);
        panel.add(exitButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(closeButton);

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        exitButton.setVisible(false);

        return panel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(100, 100, 100));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(52, 52, 52));
        return label;
    }

    private JLabel createStatusLabel(String status) {
        JLabel label = new JLabel(status);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            label.setForeground(OCCUPIED_COLOR);
        } else {
            label.setForeground(AVAILABLE_COLOR);
        }
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                new EmptyBorder(5, 8, 5, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 32));
        return button;
    }

    private void loadSlotData() {
        new Thread(() -> {
            try {
                if ("OCCUPIED".equalsIgnoreCase(status)) {
                    loadOccupiedData();
                } else {
                    SwingUtilities.invokeLater(() -> {
                        vehicleInfoPanel.setVisible(false);
                        parkingInfoPanel.setVisible(false);
                        paymentInfoPanel.setVisible(false);
                        noVehiclePanel.setVisible(true);
                        editButton.setVisible(false);
                        exitButton.setVisible(false);
                        revalidate();
                        repaint();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadOccupiedData() {
        String activeResponse = apiService.getActiveParking();
        if (activeResponse == null || !activeResponse.contains("\"success\":true")) return;

        String[] records = parseJsonArray(activeResponse, "data");
        for (String record : records) {
            int recSlotId = JsonHelper.extractJsonInt(record, "slot_id");
            if (recSlotId == slotId) {
                currentRecordId = JsonHelper.extractJsonInt(record, "record_id");
                currentVehicleId = JsonHelper.extractJsonInt(record, "vehicle_id");
                String vehicleNumber = JsonHelper.extractField(record, "vehicle_number");
                String entryTime = JsonHelper.extractField(record, "entry_time");

                String vehicleResponse = apiService.getVehicle(currentVehicleId);
                String ownerName = "";
                String ownerPhone = "";
                if (vehicleResponse != null && vehicleResponse.contains("\"success\":true")) {
                    String vehicleData = extractFirstObject(vehicleResponse, "data");
                    if (vehicleData != null) {
                        ownerName = JsonHelper.extractField(vehicleData, "owner_name");
                        ownerPhone = JsonHelper.extractField(vehicleData, "owner_phone");
                    }
                }

                String paymentAmount = "-";
                String paymentMethod = "-";
                String paymentStatus = "-";
                String paymentTime = "-";
                String paymentIdStr = "-";

                String paymentsResponse = apiService.getPayments();
                if (paymentsResponse != null && paymentsResponse.contains("\"success\":true")) {
                    String[] payments = parseJsonArray(paymentsResponse, "data");
                    for (String payment : payments) {
                        int payRecordId = JsonHelper.extractJsonInt(payment, "record_id");
                        if (payRecordId == currentRecordId) {
                            currentPaymentId = JsonHelper.extractJsonInt(payment, "payment_id");
                            paymentAmount = "Rs. " + String.format("%.2f", JsonHelper.extractJsonDouble(payment, "amount"));
                            paymentMethod = JsonHelper.extractField(payment, "payment_method");
                            paymentStatus = JsonHelper.extractField(payment, "status");
                            paymentTime = JsonHelper.formatDateTime(JsonHelper.extractField(payment, "payment_time"));
                            paymentIdStr = String.valueOf(currentPaymentId);
                            break;
                        }
                    }
                }

                String finalOwnerName = ownerName;
                String finalOwnerPhone = ownerPhone;
                String finalVehicleNumber = vehicleNumber;
                String finalEntryTime = entryTime;
                String finalPaymentAmount = paymentAmount;
                String finalPaymentMethod = paymentMethod;
                String finalPaymentStatus = paymentStatus;
                String finalPaymentTime = paymentTime;
                String finalPaymentIdStr = paymentIdStr;

                SwingUtilities.invokeLater(() -> {
                    vehicleNumberVal.setText(finalVehicleNumber);
                    vehicleTypeVal.setText(vehicleType);
                    ownerNameVal.setText(finalOwnerName.isEmpty() ? "-" : finalOwnerName);
                    ownerPhoneVal.setText(finalOwnerPhone.isEmpty() ? "-" : finalOwnerPhone);

                    entryTimeVal.setText(JsonHelper.formatDateTime(finalEntryTime));
                    exitTimeVal.setText("-");
                    durationVal.setText("In Progress");
                    recordIdVal.setText(String.valueOf(currentRecordId));

                    amountVal.setText(finalPaymentAmount);
                    paymentMethodVal.setText(finalPaymentMethod);
                    paymentStatusVal.setText(finalPaymentStatus);
                    if ("PAID".equalsIgnoreCase(finalPaymentStatus)) {
                        paymentStatusVal.setForeground(AVAILABLE_COLOR);
                    } else {
                        paymentStatusVal.setForeground(OCCUPIED_COLOR);
                    }
                    paymentTimeVal.setText(finalPaymentTime);
                    paymentIdVal.setText(finalPaymentIdStr);

                    editOwnerName.setText(finalOwnerName);
                    editOwnerPhone.setText(finalOwnerPhone);

                    vehicleInfoPanel.setVisible(true);
                    parkingInfoPanel.setVisible(true);
                    paymentInfoPanel.setVisible(true);
                    noVehiclePanel.setVisible(false);
                    editButton.setVisible(true);
                    exitButton.setVisible(true);
                    revalidate();
                    repaint();
                });
                break;
            }
        }
    }

    private String[] parseJsonArray(String json, String key) {
        String searchKey = "\"" + key + "\":[";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return new String[0];
        startIndex += searchKey.length();

        int bracketCount = 1;
        int endIndex = startIndex;
        while (endIndex < json.length() && bracketCount > 0) {
            char c = json.charAt(endIndex);
            if (c == '[') bracketCount++;
            else if (c == ']') bracketCount--;
            if (bracketCount > 0) endIndex++;
        }

        if (bracketCount != 0) return new String[0];
        String arrayContent = json.substring(startIndex, endIndex).trim();
        if (arrayContent.isEmpty()) return new String[0];

        return splitJsonObjects(arrayContent);
    }

    private String[] splitJsonObjects(String arrayContent) {
        java.util.List<String> objects = new java.util.ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inString = false;
        boolean escape = false;

        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) continue;

            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(arrayContent.substring(start, i + 1));
                }
            }
        }
        return objects.toArray(new String[0]);
    }

    private String extractFirstObject(String json, String key) {
        String searchKey = "\"" + key + "\":{";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        startIndex += searchKey.length() - 1;

        int depth = 0;
        int endIndex = startIndex;
        boolean inString = false;
        boolean escape = false;

        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) continue;

            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    endIndex = i + 1;
                    break;
                }
            }
        }
        return json.substring(startIndex, endIndex);
    }

    private void enterEditMode() {
        isEditMode = true;
        cardLayout.show(cardPanel, "EDIT");
        editButton.setVisible(false);
        exitButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        closeButton.setVisible(false);
        revalidate();
        repaint();
    }

    private void exitEditMode() {
        isEditMode = false;
        cardLayout.show(cardPanel, "VIEW");
        editButton.setVisible(true);
        exitButton.setVisible("OCCUPIED".equalsIgnoreCase(status));
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        closeButton.setVisible(true);

        editOwnerName.setText(ownerNameVal.getText().equals("-") ? "" : ownerNameVal.getText());
        editOwnerPhone.setText(ownerPhoneVal.getText().equals("-") ? "" : ownerPhoneVal.getText());

        revalidate();
        repaint();
    }

    private void saveChanges() {
        String ownerName = editOwnerName.getText().trim();
        String ownerPhone = editOwnerPhone.getText().trim();

        if (ownerName.isEmpty() && ownerPhone.isEmpty()) {
            exitEditMode();
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Saving...");

        new Thread(() -> {
            try {
                String vehicleJson = "{\"owner_name\":\"" + escapeJson(ownerName)
                        + "\",\"owner_phone\":\"" + escapeJson(ownerPhone) + "\"}";

                SwingUtilities.invokeLater(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                    exitEditMode();
                    if (onRefresh != null) onRefresh.run();
                    JOptionPane.showMessageDialog(this,
                            "Vehicle information updated successfully.",
                            "Update Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                    JOptionPane.showMessageDialog(this,
                            "Failed to update. Please try again.",
                            "Update Failed",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void processExit() {
        if (currentRecordId == -1) {
            JOptionPane.showMessageDialog(this,
                    "No active parking record found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] methods = {"CASH", "UPI", "CARD"};
        String paymentMethod = (String) JOptionPane.showInputDialog(this,
                "Select payment method for parking exit:",
                "Process Vehicle Exit",
                JOptionPane.QUESTION_MESSAGE,
                null,
                methods,
                "CASH");

        if (paymentMethod == null) return;

        exitButton.setEnabled(false);
        exitButton.setText("Processing...");

        new Thread(() -> {
            String response = apiService.exitVehicle(currentRecordId, paymentMethod);
            SwingUtilities.invokeLater(() -> {
                exitButton.setEnabled(true);
                exitButton.setText("Exit Vehicle");

                if (response != null && response.contains("\"success\":true")) {
                    status = "AVAILABLE";
                    JOptionPane.showMessageDialog(this,
                            "Vehicle exit processed successfully.",
                            "Exit Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    if (onRefresh != null) onRefresh.run();
                    dispose();
                } else {
                    String message = JsonHelper.extractMessage(response);
                    JOptionPane.showMessageDialog(this,
                            message.isEmpty() ? "Failed to process exit." : message,
                            "Exit Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
