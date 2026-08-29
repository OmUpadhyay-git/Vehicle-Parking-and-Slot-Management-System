package ui;

import java.awt.*;
import javax.swing.*;

import service.ApiService;

public class DashboardPanel extends JPanel {

    private JLabel totalSlotsValue;
    private JLabel availableSlotsValue;
    private JLabel occupiedSlotsValue;
    private JLabel activeVehiclesValue;
    private JLabel revenueValue;
    private ApiService apiService;

    public DashboardPanel() {
        apiService = new ApiService();
        initUI();
        loadDashboardData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel);
        add(topBar, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        cardsPanel.add(createStatCard("Total Slots", "0", new Color(52, 152, 219)), gbc);

        gbc.gridx = 1;
        cardsPanel.add(createStatCard("Available Slots", "0", new Color(46, 204, 113)), gbc);

        gbc.gridx = 2; gbc.weightx = 0.5;
        cardsPanel.add(createStatCard("Occupied Slots", "0", new Color(231, 76, 60)), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        cardsPanel.add(createStatCard("Active Vehicles", "0", new Color(155, 89, 182)), gbc);

        gbc.gridx = 1;
        cardsPanel.add(createStatCard("Today's Revenue", "Rs. 0", new Color(243, 156, 18)), gbc);

        add(cardsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(10));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(valueLabel);

        if (title.equals("Total Slots")) totalSlotsValue = valueLabel;
        else if (title.equals("Available Slots")) availableSlotsValue = valueLabel;
        else if (title.equals("Occupied Slots")) occupiedSlotsValue = valueLabel;
        else if (title.equals("Active Vehicles")) activeVehiclesValue = valueLabel;
        else if (title.contains("Revenue")) revenueValue = valueLabel;

        return card;
    }

    private void loadDashboardData() {
        new Thread(() -> {
            String response = apiService.getDashboard();
            SwingUtilities.invokeLater(() -> {
                try {
                    if (response.contains("\"success\":true")) {
                        int totalSlots = extractJsonInt(response, "total_slots");
                        int availableSlots = extractJsonInt(response, "available_slots");
                        int occupiedSlots = extractJsonInt(response, "occupied_slots");
                        int activeVehicles = extractJsonInt(response, "active_vehicles");
                        double todayRevenue = extractJsonDouble(response, "today_revenue");

                        totalSlotsValue.setText(String.valueOf(totalSlots));
                        availableSlotsValue.setText(String.valueOf(availableSlots));
                        occupiedSlotsValue.setText(String.valueOf(occupiedSlots));
                        activeVehiclesValue.setText(String.valueOf(activeVehicles));
                        revenueValue.setText("Rs. " + String.format("%.0f", todayRevenue));
                    }
                } catch (Exception e) {
                    // Keep default "0" values
                }
            });
        }).start();
    }

    private int extractJsonInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return 0;
        startIndex += searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        try {
            return Integer.parseInt(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double extractJsonDouble(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return 0.0;
        startIndex += searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.' || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        try {
            return Double.parseDouble(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void refreshData() {
        loadDashboardData();
    }
}
