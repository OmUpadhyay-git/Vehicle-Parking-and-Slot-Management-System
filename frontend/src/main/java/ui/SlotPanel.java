package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.ApiService;
import service.JsonHelper;

public class SlotPanel extends JPanel {

    private JTable slotTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JPanel gridPanel;
    private ApiService apiService;
    private ArrayList<String[]> allSlots = new ArrayList<>();

    public SlotPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Parking Slot Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadSlots());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        JButton addSlotBtn = new JButton("+ ADD SLOT");
        addSlotBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        addSlotBtn.setBackground(new Color(46, 204, 113));
        addSlotBtn.setForeground(Color.WHITE);
        addSlotBtn.setFocusPainted(false);
        addSlotBtn.setOpaque(true);
        addSlotBtn.setContentAreaFilled(true);
        addSlotBtn.setBorderPainted(false);
        toolbar.add(addSlotBtn);

        toolbar.add(new JLabel("    Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All", "Available", "Occupied", "Car", "Bike"});
        filterCombo.setPreferredSize(new Dimension(120, 28));
        toolbar.add(filterCombo);

        add(toolbar, BorderLayout.CENTER);

        // Split pane: table + grid visualization
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);

        // Table
        String[] columns = {"Slot", "Vehicle Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        slotTable = new JTable(tableModel);
        slotTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        slotTable.setRowHeight(28);
        slotTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        slotTable.getTableHeader().setBackground(new Color(236, 240, 241));

        // Status cell coloring
        slotTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = String.valueOf(table.getValueAt(row, 2));
                    if ("AVAILABLE".equals(status)) {
                        c.setBackground(new Color(234, 250, 241));
                    } else {
                        c.setBackground(new Color(250, 234, 234));
                    }
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(slotTable);
        splitPane.setLeftComponent(tableScroll);

        // Grid visualization
        gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        splitPane.setRightComponent(gridScroll);

        add(splitPane, BorderLayout.SOUTH);

        loadSlots();

        addSlotBtn.addActionListener(e -> showAddSlotDialog());

        filterCombo.addActionListener(e -> applyFilter());
    }

    private void loadSlots() {
        new Thread(() -> {
            String response = apiService.getSlots();
            SwingUtilities.invokeLater(() -> {
                parseAndDisplaySlots(response);
            });
        }).start();
    }

    private void parseAndDisplaySlots(String response) {
        allSlots.clear();
        tableModel.setRowCount(0);
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
                String vType = JsonHelper.extractField(obj, "vehicle_type");
                String status = JsonHelper.extractField(obj, "status");

                allSlots.add(new String[]{slotNum, vType, status});
                tableModel.addRow(new Object[]{slotNum, vType, status});
                i = objEnd + 1;
            }
            buildGrid();
        } catch (Exception e) {
            // Keep empty table
        }
    }

    private void buildGrid() {
        gridPanel.removeAll();

        JLabel carTitle = new JLabel("CAR PARKING");
        carTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        carTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPanel.add(carTitle);
        gridPanel.add(Box.createVerticalStrut(5));

        JPanel carGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        carGrid.setBackground(Color.WHITE);
        carGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bikeGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bikeGrid.setBackground(Color.WHITE);
        bikeGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String slotNum = String.valueOf(tableModel.getValueAt(i, 0));
            String type = String.valueOf(tableModel.getValueAt(i, 1));
            String status = String.valueOf(tableModel.getValueAt(i, 2));

            JPanel slotBox = new JPanel(new BorderLayout());
            slotBox.setPreferredSize(new Dimension(60, 50));
            slotBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel slotLabel = new JLabel(slotNum, SwingConstants.CENTER);
            slotLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            slotBox.add(slotLabel, BorderLayout.NORTH);

            JLabel statusLabel = new JLabel("AVAILABLE".equals(status) ? "AV" : "OC", SwingConstants.CENTER);
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            if ("AVAILABLE".equals(status)) {
                slotBox.setBackground(new Color(46, 204, 113));
                statusLabel.setForeground(Color.WHITE);
            } else {
                slotBox.setBackground(new Color(231, 76, 60));
                statusLabel.setForeground(Color.WHITE);
            }
            slotBox.add(statusLabel, BorderLayout.CENTER);

            if ("CAR".equals(type)) {
                carGrid.add(slotBox);
            } else {
                bikeGrid.add(slotBox);
            }
        }

        carGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        gridPanel.add(carGrid);
        gridPanel.add(Box.createVerticalStrut(15));

        JLabel bikeTitle = new JLabel("BIKE PARKING");
        bikeTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        bikeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPanel.add(bikeTitle);
        gridPanel.add(Box.createVerticalStrut(5));

        bikeGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        gridPanel.add(bikeGrid);

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);

        for (String[] slot : allSlots) {
            boolean show = false;
            switch (filter) {
                case "All":
                    show = true;
                    break;
                case "Available":
                    show = "AVAILABLE".equals(slot[2]);
                    break;
                case "Occupied":
                    show = "OCCUPIED".equals(slot[2]);
                    break;
                case "Car":
                    show = "CAR".equals(slot[1]);
                    break;
                case "Bike":
                    show = "BIKE".equals(slot[1]);
                    break;
            }
            if (show) {
                tableModel.addRow(new Object[]{slot[0], slot[1], slot[2]});
            }
        }
        buildGrid();
    }

    private void showAddSlotDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Slot", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Slot Number:"), gbc);
        gbc.gridx = 1;
        JTextField slotField = new JTextField(10);
        panel.add(slotField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CAR", "BIKE"});
        panel.add(typeCombo, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(errorLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setBackground(new Color(189, 195, 199));
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setBorderPainted(false);
        JButton saveBtn = new JButton("SAVE");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setContentAreaFilled(true);
        saveBtn.setBorderPainted(false);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String slotNum = slotField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            if (slotNum.isEmpty()) {
                errorLabel.setText("Slot number is required.");
                return;
            }

            saveBtn.setEnabled(false);
            saveBtn.setText("Saving...");

            new Thread(() -> {
                String response = apiService.createSlot(slotNum, type);
                SwingUtilities.invokeLater(() -> {
                    if (response.contains("\"success\":true")) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, "Slot added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadSlots();
                    } else {
                        String message = JsonHelper.extractMessage(response);
                        errorLabel.setText(message.isEmpty() ? "Failed to add slot" : message);
                        saveBtn.setEnabled(true);
                        saveBtn.setText("SAVE");
                    }
                });
            }).start();
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
