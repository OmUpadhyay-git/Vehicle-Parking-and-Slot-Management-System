package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import service.ApiService;
import service.JsonHelper;

public class StaffDetailsDialog extends JDialog {

    private ApiService apiService;
    private String userId;
    private String name;
    private String username;
    private String role;
    private String status;
    private String lastLogin;

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JButton changePasswordBtn;
    private JButton saveBtn;
    private JButton cancelBtn;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private static final Color ACCENT_COLOR = new Color(52, 152, 219);

    public StaffDetailsDialog(Frame owner, String userId, String name, String username,
            String role, String status, String lastLogin) {
        super(owner, "Staff Details", true);
        this.apiService = new ApiService();
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
        this.status = status;
        this.lastLogin = lastLogin;
        initUI();
    }

    private void initUI() {
        setSize(450, 480);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel("Staff Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

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

        mainPanel.add(header, BorderLayout.NORTH);

        // Card layout for view and password change
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(createViewPanel(), "VIEW");
        cardPanel.add(createPasswordPanel(), "PASSWORD");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createViewPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));

        // Staff info section
        JPanel infoSection = createInfoSection("Account Information");
        infoSection.add(createInfoRow("Staff ID:", userId));
        infoSection.add(createInfoRow("Name:", name));
        infoSection.add(createInfoRow("Username:", username));
        infoSection.add(createInfoRow("Role:", role));
        contentPanel.add(infoSection);
        contentPanel.add(Box.createVerticalStrut(15));

        // Status section
        JPanel statusSection = createInfoSection("Status Information");
        statusSection.add(createInfoRow("Status:", status));
        statusSection.add(createInfoRow("Last Login:", lastLogin));
        contentPanel.add(statusSection);
        contentPanel.add(Box.createVerticalStrut(20));

        // Change Password button
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.setOpaque(false);
        changePasswordBtn = new JButton("Change Password");
        changePasswordBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        changePasswordBtn.setForeground(Color.WHITE);
        changePasswordBtn.setBackground(ACCENT_COLOR);
        changePasswordBtn.setOpaque(true);
        changePasswordBtn.setContentAreaFilled(true);
        changePasswordBtn.setBorderPainted(false);
        changePasswordBtn.setFocusPainted(false);
        changePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePasswordBtn.setPreferredSize(new Dimension(160, 32));
        changePasswordBtn.addActionListener(e -> cardLayout.show(cardPanel, "PASSWORD"));
        btnWrapper.add(changePasswordBtn);
        contentPanel.add(btnWrapper);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        wrapper.add(contentPanel, gbc);

        return wrapper;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Change Password for " + username);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // New Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(confirmPasswordField, gbc);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(errorLabel, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        cancelBtn.setBackground(new Color(189, 195, 199));
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> {
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            errorLabel.setText(" ");
            cardLayout.show(cardPanel, "VIEW");
        });

        saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setContentAreaFilled(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> changePassword());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void changePassword() {
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (newPassword.isEmpty()) {
            errorLabel.setText("New password cannot be empty.");
            return;
        }

        if (confirmPassword.isEmpty()) {
            errorLabel.setText("Confirm password cannot be empty.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (newPassword.length() < 2) {
            errorLabel.setText("Password must be at least 2 characters.");
            return;
        }

        saveBtn.setEnabled(false);
        saveBtn.setText("Saving...");
        errorLabel.setText(" ");

        final String pw = newPassword;
        new Thread(() -> {
            String response = apiService.changePassword(Integer.parseInt(userId), pw);
            SwingUtilities.invokeLater(() -> {
                if (response.contains("\"success\":true")) {
                    JOptionPane.showMessageDialog(this,
                            "Password updated successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                    errorLabel.setText(" ");
                    cardLayout.show(cardPanel, "VIEW");
                } else {
                    String message = JsonHelper.extractMessage(response);
                    if (message.isEmpty()) message = "Failed to update password.";
                    errorLabel.setText(message);
                }
                saveBtn.setEnabled(true);
                saveBtn.setText("Save");
            });
        }).start();
    }

    private JPanel createInfoSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(250, 250, 250));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        sectionTitle.setForeground(ACCENT_COLOR);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(8));

        return section;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(110, 28));
        row.add(lbl, BorderLayout.WEST);

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(new Font("SansSerif", Font.PLAIN, 12));
        row.add(val, BorderLayout.CENTER);

        return row;
    }
}
