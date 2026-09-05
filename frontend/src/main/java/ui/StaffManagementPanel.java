package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import service.ApiService;
import service.JsonHelper;

public class StaffManagementPanel extends JPanel {

    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchBtn;
    private JButton refreshBtn;
    private JButton addStaffBtn;
    private ApiService apiService;
    private ArrayList<String[]> allUsers = new ArrayList<>();

    public StaffManagementPanel() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("  Staff Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.setContentAreaFilled(true);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        topBar.add(rightPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        addStaffBtn = new JButton("+ ADD STAFF");
        addStaffBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        addStaffBtn.setBackground(new Color(46, 204, 113));
        addStaffBtn.setForeground(Color.WHITE);
        addStaffBtn.setFocusPainted(false);
        addStaffBtn.setOpaque(true);
        addStaffBtn.setContentAreaFilled(true);
        addStaffBtn.setBorderPainted(false);
        toolbar.add(addStaffBtn);

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 30));
        toolbar.add(new JLabel("    Search Staff:"));
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
        String[] columns = {"ID", "Name", "Username", "Role", "Status", "Last Login", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        staffTable.setRowHeight(36);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        staffTable.getTableHeader().setBackground(new Color(236, 240, 241));
        staffTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        staffTable.getColumnModel().getColumn(0).setMaxWidth(60);
        staffTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        staffTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        staffTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        staffTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        staffTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        staffTable.getColumnModel().getColumn(6).setPreferredWidth(180);
        staffTable.getColumnModel().getColumn(6).setMinWidth(170);

        // Center align ID, Role, Status columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        staffTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        staffTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        staffTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Role coloring
        staffTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String role = String.valueOf(value);
                    if ("ADMIN".equals(role)) {
                        setBackground(new Color(236, 240, 241));
                        setForeground(new Color(44, 62, 80));
                    } else {
                        setBackground(Color.WHITE);
                        setForeground(new Color(52, 73, 94));
                    }
                }
                return c;
            }
        });

        // Status coloring
        staffTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String status = String.valueOf(value);
                    if ("Active".equals(status)) {
                        setBackground(new Color(234, 250, 241));
                        setForeground(new Color(39, 174, 96));
                    } else {
                        setBackground(new Color(250, 234, 234));
                        setForeground(new Color(192, 57, 43));
                    }
                }
                return c;
            }
        });

        // Action column
        staffTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        staffTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(staffTable);
        add(scrollPane, BorderLayout.SOUTH);

        loadUsers();

        // Button actions
        refreshBtn.addActionListener(e -> loadUsers());
        addStaffBtn.addActionListener(e -> showAddStaffDialog());

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                loadUsers();
            } else {
                searchUsers(query);
            }
        });

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                loadUsers();
            } else {
                searchUsers(query);
            }
        });
    }

    private void loadUsers() {
        new Thread(() -> {
            String response = apiService.getUsers();
            SwingUtilities.invokeLater(() -> {
                parseAndDisplayUsers(response);
            });
        }).start();
    }

    private void parseAndDisplayUsers(String response) {
        allUsers.clear();
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
                String userId = String.valueOf(JsonHelper.extractJsonInt(obj, "user_id"));
                String name = JsonHelper.extractField(obj, "name");
                String username = JsonHelper.extractField(obj, "username");
                String role = JsonHelper.extractField(obj, "role");
                String lastLogin = JsonHelper.extractField(obj, "last_login");

                String formattedLogin = "Never";
                if (lastLogin != null && !lastLogin.isEmpty() && !"null".equals(lastLogin)) {
                    formattedLogin = JsonHelper.formatDateTime(lastLogin);
                }

                allUsers.add(new String[]{userId, name, username, role, "Active", formattedLogin});
                tableModel.addRow(new Object[]{userId, name, username, role, "Active", formattedLogin, "View"});
                i = objEnd + 1;
            }
        } catch (Exception e) {
            // Keep empty table
        }
    }

    private void searchUsers(String query) {
        tableModel.setRowCount(0);
        String q = query.toLowerCase();
        for (String[] user : allUsers) {
            if (user[0].toLowerCase().contains(q)
                    || user[1].toLowerCase().contains(q)
                    || user[2].toLowerCase().contains(q)
                    || user[3].toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{user[0], user[1], user[2], user[3], user[4], user[5], "View"});
            }
        }
    }

    private void showAddStaffDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Staff", true);
        dialog.setSize(380, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"STAFF", "ADMIN"});
        panel.add(roleCombo, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            saveBtn.setEnabled(false);
            saveBtn.setText("Saving...");

            new Thread(() -> {
                String response = apiService.createUser(name, username, password, role);
                SwingUtilities.invokeLater(() -> {
                    if (response.contains("\"success\":true")) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, "Staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        String message = JsonHelper.extractMessage(response);
                        errorLabel.setText(message.isEmpty() ? "Failed to add staff" : message);
                        saveBtn.setEnabled(true);
                        saveBtn.setText("SAVE");
                    }
                });
            }).start();
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Button renderer for Action column
    private class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton viewBtn;
        private JButton deleteBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            viewBtn = new JButton("View");
            viewBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
            viewBtn.setBackground(new Color(52, 152, 219));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setOpaque(true);
            viewBtn.setContentAreaFilled(true);
            viewBtn.setBorderPainted(false);
            viewBtn.setPreferredSize(new Dimension(70, 26));

            deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setOpaque(true);
            deleteBtn.setContentAreaFilled(true);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setPreferredSize(new Dimension(70, 26));

            add(viewBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    // Button editor for Action column
    private class ButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private JPanel panel;
        private JButton viewBtn;
        private JButton deleteBtn;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            panel.setOpaque(true);

            viewBtn = new JButton("View");
            viewBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
            viewBtn.setBackground(new Color(52, 152, 219));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setOpaque(true);
            viewBtn.setContentAreaFilled(true);
            viewBtn.setBorderPainted(false);
            viewBtn.setPreferredSize(new Dimension(70, 26));
            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                int row = currentRow;
                if (row >= 0) {
                    String userId = String.valueOf(tableModel.getValueAt(row, 0));
                    String name = String.valueOf(tableModel.getValueAt(row, 1));
                    String username = String.valueOf(tableModel.getValueAt(row, 2));
                    String role = String.valueOf(tableModel.getValueAt(row, 3));
                    String status = String.valueOf(tableModel.getValueAt(row, 4));
                    String lastLogin = String.valueOf(tableModel.getValueAt(row, 5));

                    SwingUtilities.invokeLater(() -> {
                        StaffDetailsDialog dialog = new StaffDetailsDialog(
                                (Frame) SwingUtilities.getWindowAncestor(StaffManagementPanel.this),
                                userId, name, username, role, status, lastLogin);
                        dialog.setVisible(true);
                        loadUsers();
                    });
                }
            });

            deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setOpaque(true);
            deleteBtn.setContentAreaFilled(true);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setPreferredSize(new Dimension(70, 26));
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                int row = currentRow;
                if (row >= 0) {
                    String userId = String.valueOf(tableModel.getValueAt(row, 0));
                    String name = String.valueOf(tableModel.getValueAt(row, 1));
                    String role = String.valueOf(tableModel.getValueAt(row, 3));

                    int confirm = JOptionPane.showConfirmDialog(
                            StaffManagementPanel.this,
                            "Are you sure you want to delete \"" + name + "\"?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        new Thread(() -> {
                            String response = apiService.deleteUser(Integer.parseInt(userId));
                            SwingUtilities.invokeLater(() -> {
                                if (response.contains("\"success\":true")) {
                                    JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                            "Staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    loadUsers();
                                } else {
                                    String message = JsonHelper.extractMessage(response);
                                    JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                            message.isEmpty() ? "Failed to delete staff" : message,
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        }).start();
                    }
                }
            });

            panel.add(viewBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
}
