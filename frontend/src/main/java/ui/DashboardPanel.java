package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import service.ApiService;
import service.JsonHelper;

public class DashboardPanel extends JPanel {

    private JLabel totalSlotsValue;
    private JLabel availableSlotsValue;
    private JLabel occupiedSlotsValue;
    private JLabel activeVehiclesValue;
    private JLabel revenueValue;
    private ApiService apiService;

    private JPanel carSlotsPanel;
    private JPanel bikeSlotsPanel;

    private List<SlotCard> allSlotCards = new ArrayList<>();

    private static final Color AVAILABLE_COLOR = new Color(39, 174, 96);
    private static final Color OCCUPIED_COLOR = new Color(231, 76, 60);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SECTION_HEADER_COLOR = new Color(44, 62, 80);
    private static final Color CARD_BORDER = new Color(230, 230, 230);
    private static final Color LIGHT_BG = new Color(250, 250, 250);

    public DashboardPanel() {
        apiService = new ApiService();
        initUI();
        loadDashboardData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel("  Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(SECTION_HEADER_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(ACCENT_COLOR);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        refreshBtn.addActionListener(e -> refreshData());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(Color.WHITE);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));

        scrollContent.add(createStatisticsSection());
        scrollContent.add(Box.createVerticalStrut(20));
        scrollContent.add(createParkingSlotLayoutSection());

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatisticsSection() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel section = new JPanel(new GridLayout(1, 5, 12, 0));
        section.setBackground(Color.WHITE);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(createStatCard("Total Slots", "0", new Color(52, 152, 219), "\u25A3"));
        section.add(createStatCard("Available Slots", "0", new Color(46, 204, 113), "\u2714"));
        section.add(createStatCard("Occupied Slots", "0", new Color(231, 76, 60), "\u25CF"));
        section.add(createStatCard("Active Vehicles", "0", new Color(155, 89, 182), "\u2637"));
        section.add(createStatCard("Today's Revenue", "Rs. 0", new Color(243, 156, 18), "\u20B9"));

        wrapper.add(section, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 2, 5, getHeight() - 4, 3, 3);
            }
        };
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        iconLabel.setForeground(color);
        iconLabel.setPreferredSize(new Dimension(35, 35));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(140, 140, 140));
        textPanel.add(titleLabel);

        textPanel.add(Box.createVerticalStrut(3));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(new Color(52, 52, 52));
        textPanel.add(valueLabel);

        textPanel.add(Box.createVerticalStrut(2));

        JLabel subtitle = new JLabel(getSubtitle(title));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(new Color(180, 180, 180));
        textPanel.add(subtitle);

        card.add(textPanel, BorderLayout.CENTER);

        if (title.equals("Total Slots")) totalSlotsValue = valueLabel;
        else if (title.equals("Available Slots")) availableSlotsValue = valueLabel;
        else if (title.equals("Occupied Slots")) occupiedSlotsValue = valueLabel;
        else if (title.equals("Active Vehicles")) activeVehiclesValue = valueLabel;
        else if (title.contains("Revenue")) revenueValue = valueLabel;

        return card;
    }

    private String getSubtitle(String title) {
        switch (title) {
            case "Total Slots": return "All parking slots";
            case "Available Slots": return "Slots available";
            case "Occupied Slots": return "Slots occupied";
            case "Active Vehicles": return "Vehicles parked";
            case "Today's Revenue": return "From parking fees";
            default: return "";
        }
    }

    private JPanel createParkingSlotLayoutSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setBackground(Color.WHITE);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel layoutTitle = new JLabel("Parking Slot Layout");
        layoutTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        layoutTitle.setForeground(SECTION_HEADER_COLOR);
        headerRow.add(layoutTitle, BorderLayout.WEST);

        JPanel legend = createLegend();
        headerRow.add(legend, BorderLayout.EAST);

        section.add(headerRow);
        section.add(Box.createVerticalStrut(8));

        carSlotsPanel = createSlotSection("CAR PARKING");
        section.add(carSlotsPanel);
        section.add(Box.createVerticalStrut(10));

        bikeSlotsPanel = createSlotSection("BIKE PARKING");
        section.add(bikeSlotsPanel);

        return section;
    }

    private JPanel createLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legend.setBackground(Color.WHITE);

        legend.add(createLegendItem("Available", AVAILABLE_COLOR));
        legend.add(createLegendItem("Occupied", OCCUPIED_COLOR));

        return legend;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(2, 2, 10, 10);
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(14, 14));

        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.PLAIN, 11));
        text.setForeground(new Color(100, 100, 100));

        item.add(dot);
        item.add(text);
        return item;
    }

    private JPanel createSlotSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        sectionTitle.setForeground(ACCENT_COLOR);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);

        JPanel gridPanel = new JPanel();
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(gridPanel);
        return section;
    }

    private JPanel createSlotGrid(List<String> slots) {
        int cols = 10;
        int rows = (int) Math.ceil((double) slots.size() / cols);
        if (rows < 1) rows = 1;
        JPanel grid = new JPanel(new GridLayout(rows, cols, 8, 5));
        grid.setBackground(Color.WHITE);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String slot : slots) {
            int slotId = JsonHelper.extractJsonInt(slot, "slot_id");
            String slotNumber = JsonHelper.extractField(slot, "slot_number");
            String status = JsonHelper.extractField(slot, "status");
            String vehicleType = JsonHelper.extractField(slot, "vehicle_type");

            SlotCard card = new SlotCard(slotId, slotNumber, status, vehicleType, () -> {
                openSlotDetails(slotId, slotNumber, vehicleType, status);
            });
            allSlotCards.add(card);
            grid.add(card);
        }

        // Fill remaining cells with empty panels if needed
        int totalCells = rows * cols;
        for (int i = slots.size(); i < totalCells; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(Color.WHITE);
            empty.setPreferredSize(new Dimension(100, 80));
            grid.add(empty);
        }

        return grid;
    }

    private void loadDashboardData() {
        new Thread(() -> {
            String response = apiService.getDashboard();
            SwingUtilities.invokeLater(() -> {
                try {
                    if (response.contains("\"success\":true")) {
                        int totalSlots = JsonHelper.extractJsonInt(response, "total_slots");
                        int availableSlots = JsonHelper.extractJsonInt(response, "available_slots");
                        int occupiedSlots = JsonHelper.extractJsonInt(response, "occupied_slots");
                        int activeVehicles = JsonHelper.extractJsonInt(response, "active_vehicles");
                        double todayRevenue = JsonHelper.extractJsonDouble(response, "today_revenue");

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

        loadSlotsData();
    }

    private void loadSlotsData() {
        new Thread(() -> {
            try {
                String slotsResponse = apiService.getSlots();
                if (slotsResponse == null || !slotsResponse.contains("\"success\":true")) return;

                String[] slots = parseJsonArray(slotsResponse, "data");

                String activeResponse = apiService.getActiveParking();
                java.util.Map<Integer, String> activeSlotVehicleMap = new java.util.HashMap<>();
                if (activeResponse != null && activeResponse.contains("\"success\":true")) {
                    String[] activeRecords = parseJsonArray(activeResponse, "data");
                    for (String record : activeRecords) {
                        int slotId = JsonHelper.extractJsonInt(record, "slot_id");
                        String vehicleNumber = JsonHelper.extractField(record, "vehicle_number");
                        activeSlotVehicleMap.put(slotId, vehicleNumber);
                    }
                }

                List<String> carSlots = new ArrayList<>();
                List<String> bikeSlots = new ArrayList<>();

                for (String slot : slots) {
                    String vehicleType = JsonHelper.extractField(slot, "vehicle_type");
                    if ("CAR".equalsIgnoreCase(vehicleType)) {
                        carSlots.add(slot);
                    } else if ("BIKE".equalsIgnoreCase(vehicleType)) {
                        bikeSlots.add(slot);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    while (carSlotsPanel.getComponentCount() > 1) {
                        carSlotsPanel.remove(1);
                    }
                    while (bikeSlotsPanel.getComponentCount() > 1) {
                        bikeSlotsPanel.remove(1);
                    }

                    carSlotsPanel.add(createSlotGrid(carSlots));
                    bikeSlotsPanel.add(createSlotGrid(bikeSlots));

                    revalidate();
                    repaint();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openSlotDetails(int slotId, String slotNumber, String vehicleType, String status) {
        SwingUtilities.invokeLater(() -> {
            SlotDetailsDialog dialog = new SlotDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    slotId, slotNumber, vehicleType, status,
                    () -> refreshData());
            dialog.setVisible(true);
        });
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
        List<String> objects = new ArrayList<>();
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

    public void refreshData() {
        loadDashboardData();
        allSlotCards.clear();
    }
}
