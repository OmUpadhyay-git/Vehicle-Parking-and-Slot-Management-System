package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.ApiService;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private ApiService apiService;

    private static final Color DARK_BG = new Color(30, 39, 46);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color ACCENT_HOVER = new Color(41, 128, 185);
    private static final Color LIGHT_BG = new Color(245, 246, 250);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color TEXT_GRAY = new Color(127, 140, 141);
    private static final Color INPUT_BORDER = new Color(189, 195, 199);
    private static final Color ERROR_RED = new Color(231, 76, 60);

    private static final int FIELD_HEIGHT = 42;
    private static final int BUTTON_HEIGHT = 46;

    public LoginFrame() {
        apiService = new ApiService();
        initUI();
    }

    private void initUI() {
        setTitle("Vehicle Parking System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(800, 520));

        // Dynamic sizing based on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.60);
        int frameHeight = (int) (screenSize.height * 0.70);
        frameWidth = Math.max(900, Math.min(frameWidth, 1200));
        frameHeight = Math.max(550, Math.min(frameHeight, 750));
        setSize(frameWidth, frameHeight);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        // Left panel - Branding
        JPanel leftPanel = createBrandingPanel();
        // Right panel - Login form
        JPanel rightPanel = createFormPanel();

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private JPanel createBrandingPanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(70, 40, 50, 40));

        JLabel iconLabel = new JLabel("\u2315");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 80));
        iconLabel.setForeground(ACCENT);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(iconLabel);

        leftPanel.add(Box.createVerticalStrut(20));

        JLabel brandTitle = new JLabel("Vehicle Parking");
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(brandTitle);

        JLabel brandSubtitle = new JLabel("& Slot Management");
        brandSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        brandSubtitle.setForeground(new Color(149, 165, 166));
        brandSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(brandSubtitle);

        leftPanel.add(Box.createVerticalStrut(40));

        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(200, 1));
        sep1.setForeground(new Color(52, 73, 94));
        sep1.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(sep1);

        leftPanel.add(Box.createVerticalStrut(30));

        JLabel desc1 = new JLabel("Manage parking slots");
        desc1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc1.setForeground(new Color(178, 186, 191));
        desc1.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(desc1);

        JLabel desc2 = new JLabel("Track vehicle entry & exit");
        desc2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc2.setForeground(new Color(178, 186, 191));
        desc2.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(desc2);

        JLabel desc3 = new JLabel("Calculate parking fees");
        desc3.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc3.setForeground(new Color(178, 186, 191));
        desc3.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(desc3);

        leftPanel.add(Box.createVerticalGlue());

        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(100, 110, 115));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(versionLabel);

        return leftPanel;
    }

    private JPanel createFormPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Form container — centered with a fixed readable width
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(320, 460));
        formPanel.setMinimumSize(new Dimension(300, 460));
        formPanel.setMaximumSize(new Dimension(340, 460));

        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(welcomeLabel);

        formPanel.add(Box.createVerticalStrut(4));

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(subtitleLabel);

        formPanel.add(Box.createVerticalStrut(30));

        // Username label
        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        usernameLabel.setForeground(TEXT_GRAY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameLabel);

        formPanel.add(Box.createVerticalStrut(8));

        // Username field
        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(189, 195, 199));
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    g2.drawString("Enter your username", getInsets().left + 5,
                            getHeight() / 2 + getFont().getSize() / 3);
                    g2.dispose();
                }
            }
        };
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setPreferredSize(new Dimension(320, FIELD_HEIGHT));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BORDER, 8),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        usernameField.setBackground(LIGHT_BG);
        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(18));

        // Password label
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        passwordLabel.setForeground(TEXT_GRAY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordLabel);

        formPanel.add(Box.createVerticalStrut(8));

        // Password field
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(189, 195, 199));
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    g2.drawString("Enter your password", getInsets().left + 5,
                            getHeight() / 2 + getFont().getSize() / 3);
                    g2.dispose();
                }
            }
        };
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setPreferredSize(new Dimension(320, FIELD_HEIGHT));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BORDER, 8),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        passwordField.setBackground(LIGHT_BG);
        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(6));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(errorLabel);

        formPanel.add(Box.createVerticalStrut(10));

        // Login button
        loginButton = new JButton("SIGN IN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_HOVER);
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(320, BUTTON_HEIGHT));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(loginButton);

        formPanel.add(Box.createVerticalStrut(12));

        // Forgot Password link
        JLabel forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        forgotPasswordLabel.setForeground(ACCENT);
        forgotPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Please contact your administrator to reset your password.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgotPasswordLabel.setText("Forgot Password?");
            }
        });
        formPanel.add(forgotPasswordLabel);

        // Center form in the right panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        rightPanel.add(formPanel, gbc);

        // Actions
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        passwordField.addActionListener(e -> handleLogin());

        return rightPanel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            errorLabel.setForeground(ERROR_RED);
            return;
        }

        errorLabel.setText(" ");
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        new Thread(() -> {
            String response = apiService.login(username, password);

            SwingUtilities.invokeLater(() -> {
                try {
                    if (response.contains("\"success\":true")) {
                        String role = extractJsonValue(response, "role");
                        if (role.isEmpty()) role = "STAFF";
                        openDashboard(role, username);
                    } else {
                        String message = extractJsonValue(response, "message");
                        if (message.isEmpty()) message = "Invalid username or password";
                        errorLabel.setText(message);
                        errorLabel.setForeground(ERROR_RED);
                    }
                } catch (Exception e) {
                    errorLabel.setText("Unable to connect to server.");
                    errorLabel.setForeground(ERROR_RED);
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("SIGN IN");
                }
            });
        }).start();
    }

    private void openDashboard(String role, String username) {
        DashboardFrame dashboard = new DashboardFrame(role, username);
        dashboard.setVisible(true);
        dispose();
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }
}