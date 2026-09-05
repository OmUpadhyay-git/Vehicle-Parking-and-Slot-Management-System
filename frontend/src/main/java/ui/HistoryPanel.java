package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.ApiService;
import service.JsonHelper;

public class HistoryPanel extends JPanel {

    private JTextField searchField;
    private JButton searchBtn;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private ApiService apiService;

    public HistoryPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Parking History");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadHistory();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 30));
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);

        searchBtn = new JButton("SEARCH");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setContentAreaFilled(true);
        searchBtn.setBorderPainted(false);
        toolbar.add(searchBtn);

        add(toolbar, BorderLayout.CENTER);

        // Table
        String[] columns = {"Vehicle", "Slot", "Entry", "Exit", "Duration", "Fee", "Payment"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historyTable.setRowHeight(28);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(new Color(236, 240, 241));

        // Payment status coloring
        historyTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                    if (column == 6) {
                        String status = String.valueOf(value);
                        if ("PAID".equals(status)) {
                            setForeground(new Color(39, 174, 96));
                        } else {
                            setForeground(new Color(231, 76, 60));
                        }
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.SOUTH);

        loadHistory();

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    loadHistory();
                } else {
                    loadHistory();
                }
            }
        });

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                loadHistory();
            } else {
                loadHistory();
            }
        });
    }

    private void loadHistory() {
        new Thread(() -> {
            String response = apiService.getParkingHistory();
            SwingUtilities.invokeLater(() -> {
                parseAndDisplayHistory(response);
            });
        }).start();
    }

    private void parseAndDisplayHistory(String response) {
        tableModel.setRowCount(0);
        try {
            if (!response.contains("\"success\":true")) return;

            int dataIndex = response.indexOf("\"data\":[");
            if (dataIndex == -1) return;
            int arrStart = response.indexOf("[", dataIndex);
            int arrEnd = response.lastIndexOf("]");
            String arrContent = response.substring(arrStart + 1, arrEnd).trim();
            if (arrContent.isEmpty()) return;

            String searchQuery = searchField.getText().trim().toUpperCase();

            int i = 0;
            while (i < arrContent.length()) {
                int objStart = arrContent.indexOf("{", i);
                if (objStart == -1) break;
                int objEnd = arrContent.indexOf("}", objStart);
                if (objEnd == -1) break;

                String obj = arrContent.substring(objStart, objEnd + 1);
                String vNum = JsonHelper.extractField(obj, "vehicle_number");
                String slotNum = JsonHelper.extractField(obj, "slot_number");
                String entryTime = JsonHelper.extractField(obj, "entry_time");
                String exitTime = JsonHelper.extractField(obj, "exit_time");
                int durationMin = JsonHelper.extractJsonInt(obj, "duration_minutes");
                double fee = JsonHelper.extractJsonDouble(obj, "fee");
                String paymentStatus = JsonHelper.extractField(obj, "payment_status");
                String paymentMethod = JsonHelper.extractField(obj, "payment_method");

                // Filter by search query
                if (!searchQuery.isEmpty() && !vNum.toUpperCase().contains(searchQuery)) {
                    i = objEnd + 1;
                    continue;
                }

                String entry = JsonHelper.formatDateTime(entryTime);
                String exit = JsonHelper.formatDateTime(exitTime);
                String duration = JsonHelper.formatDuration(durationMin);
                String feeStr = "Rs. " + String.format("%.0f", fee);
                String payment = paymentStatus.isEmpty() ? "PENDING" : paymentStatus;

                tableModel.addRow(new Object[]{vNum, slotNum, entry, exit, duration, feeStr, payment});
                i = objEnd + 1;
            }
        } catch (Exception e) {
            // Keep empty table
        }
    }
}
