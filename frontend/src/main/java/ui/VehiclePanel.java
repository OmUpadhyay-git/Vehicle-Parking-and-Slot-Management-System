package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.ApiService;

public class VehiclePanel extends JPanel {

    private JTextField searchField;
    private JButton searchBtn;
    private JButton addBtn;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private ApiService apiService;

    public VehiclePanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Vehicle Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel);
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
        toolbar.add(searchBtn);

        addBtn = new JButton("+ ADD VEHICLE");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        toolbar.add(addBtn);

        add(toolbar, BorderLayout.CENTER);

        // Table
        String[] columns = {"Vehicle No.", "Type", "Owner Name", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehicleTable = new JTable(tableModel);
        vehicleTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        vehicleTable.setRowHeight(28);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        vehicleTable.getTableHeader().setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.SOUTH);

        loadVehicles();

        // Button actions
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddVehicleDialog();
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    loadVehicles();
                } else {
                    searchVehicles(query);
                }
            }
        });

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                loadVehicles();
            } else {
                searchVehicles(query);
            }
        });
    }

    private void loadVehicles() {
        new Thread(() -> {
            String response = apiService.getVehicles();
            SwingUtilities.invokeLater(() -> {
                parseAndDisplayVehicles(response);
            });
        }).start();
    }

    private void searchVehicles(String query) {
        new Thread(() -> {
            String response = apiService.searchVehicle(query);
            SwingUtilities.invokeLater(() -> {
                parseAndDisplayVehicles(response);
            });
        }).start();
    }

    private void parseAndDisplayVehicles(String response) {
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
                String vNum = extractField(obj, "vehicle_number");
                String vType = extractField(obj, "vehicle_type");
                String owner = extractField(obj, "owner_name");
                String phone = extractField(obj, "owner_phone");

                tableModel.addRow(new Object[]{vNum, vType, owner, phone});
                i = objEnd + 1;
            }
        } catch (Exception e) {
            // Keep empty table
        }
    }

    private String extractField(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }

    private void showAddVehicleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Vehicle", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Vehicle Number:"), gbc);
        gbc.gridx = 1;
        JTextField numberField = new JTextField(15);
        panel.add(numberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CAR", "BIKE"});
        panel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Owner Name:"), gbc);
        gbc.gridx = 1;
        JTextField ownerField = new JTextField(15);
        panel.add(ownerField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Owner Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(errorLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setBackground(new Color(189, 195, 199));
        cancelBtn.setFocusPainted(false);

        JButton saveBtn = new JButton("SAVE");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String number = numberField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String owner = ownerField.getText().trim();
            String phone = phoneField.getText().trim();

            if (number.isEmpty() || owner.isEmpty() || phone.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            saveBtn.setEnabled(false);
            saveBtn.setText("Saving...");

            new Thread(() -> {
                String response = apiService.createVehicle(number, type, owner, phone);
                SwingUtilities.invokeLater(() -> {
                    if (response.contains("\"success\":true")) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, "Vehicle added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadVehicles();
                    } else {
                        String message = extractMessage(response);
                        errorLabel.setText(message.isEmpty() ? "Failed to add vehicle" : message);
                        saveBtn.setEnabled(true);
                        saveBtn.setText("SAVE");
                    }
                });
            }).start();
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private String extractMessage(String json) {
        String searchKey = "\"message\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }
}
