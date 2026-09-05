package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SlotCard extends JPanel {

    private String slotNumber;
    private String status;
    private String vehicleType;
    private int slotId;
    private boolean isHovered = false;

    private static final Color AVAILABLE_COLOR = new Color(39, 174, 96);
    private static final Color OCCUPIED_COLOR = new Color(231, 76, 60);
    private static final Color AVAILABLE_BG = new Color(232, 245, 233);
    private static final Color OCCUPIED_BG = new Color(253, 237, 236);
    private static final Color HOVER_BORDER = new Color(180, 180, 180);
    private static final Color DEFAULT_BORDER = new Color(220, 220, 220);

    public SlotCard(int slotId, String slotNumber, String status, String vehicleType, Runnable onClick) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.vehicleType = vehicleType;

        setLayout(new BorderLayout(0, 2));
        setPreferredSize(new Dimension(100, 80));
        setMaximumSize(new Dimension(100, 80));
        setMinimumSize(new Dimension(100, 80));
        setBackground(getCardBackground());
        setBorder(new CompoundBorder(
                new LineBorder(getCardBorder(), 1, true),
                new EmptyBorder(8, 6, 8, 6)));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel numberLabel = new JLabel(slotNumber, SwingConstants.CENTER);
        numberLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        numberLabel.setForeground(getStatusColor());

        String statusText = getStatusDisplay();
        JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setForeground(getStatusColor());

        add(numberLabel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setBorder(new CompoundBorder(
                        new LineBorder(HOVER_BORDER, 2, true),
                        new EmptyBorder(7, 5, 7, 5)));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBorder(new CompoundBorder(
                        new LineBorder(getCardBorder(), 1, true),
                        new EmptyBorder(8, 6, 8, 6)));
                repaint();
            }
        });
    }

    private Color getCardBackground() {
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            return OCCUPIED_BG;
        }
        return AVAILABLE_BG;
    }

    private Color getCardBorder() {
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            return OCCUPIED_COLOR;
        }
        return AVAILABLE_COLOR;
    }

    private Color getStatusColor() {
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            return OCCUPIED_COLOR;
        }
        return AVAILABLE_COLOR;
    }

    private String getStatusDisplay() {
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            return vehicleType != null ? vehicleType.toUpperCase() : "OC";
        }
        return "AV";
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getSlotId() {
        return slotId;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        setBackground(getCardBackground());
        repaint();
    }
}
