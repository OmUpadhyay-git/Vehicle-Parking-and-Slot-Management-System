package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class DashboardFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private String currentRole;
    private String currentUser;

    private JButton dashboardBtn;
    private JButton vehicleBtn;
    private JButton slotBtn;
    private JButton entryBtn;
    private JButton exitBtn;
    private JButton historyBtn;
    private JButton logoutBtn;

    private JButton activeButton;

    public DashboardFrame(String role, String username) {
        this.currentRole = role;
        this.currentUser = username;
        initUI();
    }

    private void initUI() {
        setTitle("Vehicle Parking & Slot Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 600));

        JPanel mainPanel = new JPanel(new BorderLayout());

        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new VehiclePanel(), "VEHICLES");
        contentPanel.add(new SlotPanel(), "SLOTS");
        contentPanel.add(new ParkingEntryPanel(), "ENTRY");
        contentPanel.add(new ParkingExitPanel(), "EXIT");
        contentPanel.add(new HistoryPanel(), "HISTORY");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        setActiveButton(dashboardBtn);
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel appLabel = new JLabel("Parking System");
        appLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        appLabel.setForeground(Color.WHITE);
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(appLabel);

        JLabel roleLabel = new JLabel("(" + currentRole.toUpperCase() + ")");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(189, 195, 199));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(roleLabel);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        dashboardBtn = createSidebarButton("Dashboard");
        vehicleBtn = createSidebarButton("Vehicles");
        slotBtn = createSidebarButton("Parking Slots");
        entryBtn = createSidebarButton("Vehicle Entry");
        exitBtn = createSidebarButton("Vehicle Exit");
        historyBtn = createSidebarButton("Parking History");

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(vehicleBtn);
        sidebar.add(Box.createVerticalStrut(5));

        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            sidebar.add(slotBtn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(entryBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(exitBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(historyBtn);

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        logoutBtn = createSidebarButton("Logout");
        sidebar.add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
            }
        });

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(190, 35));
        button.setPreferredSize(new Dimension(190, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setActiveButton(button);
                String cmd = button.getText();
                switch (cmd) {
                    case "Dashboard":
                        cardLayout.show(contentPanel, "DASHBOARD");
                        break;
                    case "Vehicles":
                        cardLayout.show(contentPanel, "VEHICLES");
                        break;
                    case "Parking Slots":
                        cardLayout.show(contentPanel, "SLOTS");
                        break;
                    case "Vehicle Entry":
                        cardLayout.show(contentPanel, "ENTRY");
                        break;
                    case "Vehicle Exit":
                        cardLayout.show(contentPanel, "EXIT");
                        break;
                    case "Parking History":
                        cardLayout.show(contentPanel, "HISTORY");
                        break;
                }
            }
        });

        return button;
    }

    private void setActiveButton(JButton selected) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(44, 62, 80));
        }
        activeButton = selected;
        activeButton.setBackground(new Color(52, 152, 219));
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(180, 1));
        sep.setForeground(new Color(52, 73, 94));
        return sep;
    }
}
