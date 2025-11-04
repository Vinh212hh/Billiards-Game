package b4_update;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class BallAnimation extends JPanel implements ActionListener, MouseListener {
    private boolean collisionEnabled = true;

    private class Ball {
        double x, y;
        int radius;
        double dx, dy;
        Color color;
        boolean active = true;
        int number;

        Ball(double x, double y, int radius, double dx, double dy, Color color, int number) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.dx = dx;
            this.dy = dy;
            this.color = color;
            this.number = number;
        }

        void move() {
            if (!active) return;
            x += dx;
            y += dy;
            if (x < 0 || x + radius * 2 > getWidth()) {
                dx = -dx;
                x += dx;
            }
            if (y < 0 || y + radius * 2 > getHeight()) {
                dy = -dy;
                y += dy;
            }
        }

        void draw(Graphics2D g) {
            if (!active) return;
            if (number == predictedBallNumber) {
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(4));
                g.drawOval((int) x - 2, (int) y - 2, radius * 2 + 4, radius * 2 + 4);
            }
            g.setColor(color);
            g.fillOval((int) x, (int) y, radius * 2, radius * 2);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, Math.max(10, radius)));
            String text = String.valueOf(number);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g.drawString(text, (int) (x + radius - textWidth / 2), (int) (y + radius + textHeight / 4));
        }

        boolean isColliding(Ball other) {
            if (!active || !other.active) return false;
            double dxC = (x + radius) - (other.x + other.radius);
            double dyC = (y + radius) - (other.y + other.radius);
            double distance = Math.sqrt(dxC * dxC + dyC * dyC);
            return distance < radius + other.radius;
        }

        void bounce(Ball other) {
            double dxC = (x + radius) - (other.x + other.radius);
            double dyC = (y + radius) - (other.y + other.radius);
            double dist = Math.sqrt(dxC * dxC + dyC * dyC);
            if (dist == 0) return;
            double overlap = (radius + other.radius) - dist;
            if (overlap > 0) {
                double nx = dxC / dist;
                double ny = dyC / dist;
                x += nx * overlap / 2;
                y += ny * overlap / 2;
                other.x -= nx * overlap / 2;
                other.y -= ny * overlap / 2;

                double tx = -ny;
                double ty = nx;
                double dpTan1 = dx * tx + dy * ty;
                double dpTan2 = other.dx * tx + other.dy * ty;
                double dpNorm1 = dx * nx + dy * ny;
                double dpNorm2 = other.dx * nx + other.dy * ny;

                double m1 = dpNorm2;
                double m2 = dpNorm1;
                dx = tx * dpTan1 + nx * m1;
                dy = ty * dpTan1 + ny * m1;
                other.dx = tx * dpTan2 + nx * m2;
                other.dy = ty * dpTan2 + ny * m2;
            }
        }

        boolean checkHole(Point hole) {
            double cx = x + radius;
            double cy = y + radius;
            double distance = Math.sqrt(Math.pow(cx - hole.x, 2) + Math.pow(cy - hole.y, 2));
            return distance < radius + 15;
        }

        boolean contains(int mx, int my) {
            double cx = x + radius;
            double cy = y + radius;
            return Math.sqrt(Math.pow(mx - cx, 2) + Math.pow(my - cy, 2)) <= radius;
        }
    }

    private Ball[] balls;
    private java.util.List<Point> holes;
    private javax.swing.Timer timer;
    private static final double SLOW_FACTOR = 0.995;
    private Color tableColor;
    private boolean started = false;
    private int centerX, centerY;
    private int predictedBallNumber;
    private boolean firstBallDown = false;
    private int firstDownBallNumber;

    public BallAnimation(int numBalls, int mapType, int predicted, Dimension vizSize) {
        addMouseListener(this);
        int w = vizSize.width, h = vizSize.height;
        setPreferredSize(new Dimension(w, h));
        centerX = w / 2;
        centerY = h / 2;
        holes = new ArrayList<>();
        loadMap(mapType, w, h);
        initBalls(numBalls);
        predictedBallNumber = predicted;
        setBackground(tableColor);
        timer = new javax.swing.Timer(20, this);
        timer.start();
    }

    private void loadMap(int mapType, int w, int h) {
        holes.clear();
        if (mapType == 1) {
            tableColor = new Color(25, 100, 40);
            holes.add(new Point(15, 15));
            holes.add(new Point(w / 2, 15));
            holes.add(new Point(w - 15, 15));
            holes.add(new Point(15, h - 15));
            holes.add(new Point(w / 2, h - 15));
            holes.add(new Point(w - 15, h - 15));
        } else if (mapType == 2) {
            tableColor = new Color(20, 70, 130);
            holes.add(new Point(w / 4, 15));
            holes.add(new Point(3 * w / 4, 15));
            holes.add(new Point(w / 2, h / 2));
            holes.add(new Point(15, h - 15));
            holes.add(new Point(w - 15, h - 15));
        } else {
            tableColor = new Color(100, 40, 40);
            holes.add(new Point(w / 2, 15));
            holes.add(new Point(15, h / 2));
            holes.add(new Point(w - 15, h / 2));
            holes.add(new Point(w / 2, h - 15));
        }
    }

    private void initBalls(int numBalls) {
        Random rand = new Random();
        java.util.List<Integer> numbers = new java.util.ArrayList<>();
        for (int i = 1; i <= numBalls; i++) numbers.add(i);
        Collections.shuffle(numbers);
        int radius = Math.max(8, 25 - numBalls / 10);
        balls = new Ball[numBalls];

        double baseX = centerX - radius;
        double baseY = centerY - radius;

        for (int i = 0; i < numBalls; i++) {
            double x = baseX;
            double y = baseY;

            boolean overlap;
            do {
                overlap = false;
                for (Point hole : holes) {
                    double dx = (x + radius) - hole.x;
                    double dy = (y + radius) - hole.y;
                    if (Math.sqrt(dx * dx + dy * dy) < radius + 30) {
                        baseX += rand.nextInt(60) - 30;
                        baseY += rand.nextInt(60) - 30;
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);

            balls[i] = new Ball(x, y, radius, 0, 0,
                    new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
                    numbers.get(i));
        }
    }

    private void explodeBalls() {
        Random rand = new Random();
        for (Ball b : balls) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double speed = rand.nextDouble() * 4 + 2;
            b.dx = Math.cos(angle) * speed;
            b.dy = Math.sin(angle) * speed;
        }
        started = true;
        firstBallDown = false;
        firstDownBallNumber = -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!started) return;
        for (Ball b : balls) {
            if (!b.active) continue;
            b.move();
            b.dx *= SLOW_FACTOR;
            b.dy *= SLOW_FACTOR;
            for (Ball other : balls) {
                if (b != other && b.isColliding(other)) b.bounce(other);
            }
            for (Point hole : holes) {
                if (b.checkHole(hole)) {
                    b.active = false;
                    if (!firstBallDown) {
                        firstBallDown = true;
                        firstDownBallNumber = b.number;
                        SwingUtilities.invokeLater(() -> {
                            if (firstDownBallNumber == predictedBallNumber)
                                JOptionPane.showMessageDialog(this, "🎯 Bạn đoán đúng! Bóng số " + b.number + " rơi đầu tiên.");
                            else
                                JOptionPane.showMessageDialog(this, "❌ Sai rồi! Bóng đầu tiên rơi là số " + b.number);
                        });
                    }
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // viền bàn bi-a
        g2.setColor(new Color(60, 30, 10));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(tableColor);
        g2.fillRect(20, 20, getWidth() - 40, getHeight() - 40);
        g2.setColor(Color.BLACK);
        for (Point hole : holes)
            g2.fillOval(hole.x - 15, hole.y - 15, 30, 30);
        for (Ball b : balls)
            b.draw(g2);
    }

    @Override public void mouseClicked(MouseEvent e) {
        if (!started) return;
        int mx = e.getX(), my = e.getY();
        Random rand = new Random();
        for (Ball b : balls) {
            if (b.contains(mx, my)) {
                b.dx = (rand.nextDouble() * 4 + 1) * (rand.nextBoolean() ? 1 : -1);
                b.dy = (rand.nextDouble() * 4 + 1) * (rand.nextBoolean() ? 1 : -1);
                break;
            }
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public void applyParameters(int numBalls, int mapType, int predicted) {
        loadMap(mapType, getWidth(), getHeight());
        predictedBallNumber = predicted;
        initBalls(numBalls);
        repaint();
    }

    public void resetGame(int numBalls, int mapType, int predicted) {
        started = false;
        applyParameters(numBalls, mapType, predicted);
        repaint();
    }

    public void startSimulation() {
        started = true;
        explodeBalls();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🎱 Mô phỏng Bi-a");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension vizSize = new Dimension(900, 600);
            int defaultBalls = 20, defaultMap = 1, defaultPred = 1;
            BallAnimation simPanel = new BallAnimation(defaultBalls, defaultMap, defaultPred, vizSize);

            JPanel control = new JPanel();
            control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
            control.setPreferredSize(new Dimension(300, vizSize.height));
            control.setBorder(new CompoundBorder(
                    new MatteBorder(0, 2, 0, 0, new Color(50, 50, 50)),
                    new EmptyBorder(15, 15, 15, 15)
            ));
            control.setBackground(new Color(235, 240, 250));

            JLabel hTitle = new JLabel("⚙️ BẢNG ĐIỀU KHIỂN");
            hTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            hTitle.setForeground(new Color(40, 60, 110));
            hTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            control.add(hTitle);
            control.add(Box.createVerticalStrut(10));

            control.add(new JLabel("Số bóng:"));
            JTextField numField = new JTextField(String.valueOf(defaultBalls));
            control.add(numField);
            control.add(Box.createVerticalStrut(8));

            control.add(new JLabel("Chọn Map:"));
            JComboBox<String> mapCombo = new JComboBox<>(new String[]{"Map 1", "Map 2", "Map 3"});
            mapCombo.setSelectedIndex(defaultMap - 1);
            control.add(mapCombo);
            control.add(Box.createVerticalStrut(8));

            control.add(new JLabel("Bóng dự đoán:"));
            JTextField predField = new JTextField(String.valueOf(defaultPred));
            control.add(predField);
            control.add(Box.createVerticalStrut(12));

            JButton applyBtn = new JButton("Áp dụng");
            JButton resetBtn = new JButton("Reset");
            JButton startBtn = new JButton("Chạy");

            // Style nút
            Color green = new Color(70, 170, 70);
            Color blue = new Color(60, 130, 200);
            Color gray = new Color(150, 150, 150);

            startBtn.setBackground(green);
            startBtn.setForeground(Color.WHITE);
            resetBtn.setBackground(gray);
            resetBtn.setForeground(Color.WHITE);
            applyBtn.setBackground(blue);
            applyBtn.setForeground(Color.WHITE);

            Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
            startBtn.setFont(btnFont);
            resetBtn.setFont(btnFont);
            applyBtn.setFont(btnFont);

            Dimension btnSize = new Dimension(150, 35);
            for (JButton btn : new JButton[]{applyBtn, resetBtn, startBtn}) {
                btn.setMaximumSize(btnSize);
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            }

            control.add(applyBtn);
            control.add(Box.createVerticalStrut(10));
            control.add(resetBtn);
            control.add(Box.createVerticalStrut(10));
            control.add(startBtn);
            control.add(Box.createVerticalGlue());

            frame.getContentPane().add(simPanel, BorderLayout.CENTER);
            frame.getContentPane().add(control, BorderLayout.EAST);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            applyBtn.addActionListener(ev -> {
                try {
                    int nb = Integer.parseInt(numField.getText().trim());
                    int mapIdx = mapCombo.getSelectedIndex() + 1;
                    int pred = Integer.parseInt(predField.getText().trim());
                    simPanel.applyParameters(nb, mapIdx, pred);
                    JOptionPane.showMessageDialog(frame, "✅ Đã áp dụng! Hãy nhấn Reset hoặc Chạy.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "⚠️ Vui lòng nhập số hợp lệ.");
                }
            });

            resetBtn.addActionListener(ev -> {
                try {
                    int nb = Integer.parseInt(numField.getText().trim());
                    int mapIdx = mapCombo.getSelectedIndex() + 1;
                    int pred = Integer.parseInt(predField.getText().trim());
                    simPanel.resetGame(nb, mapIdx, pred);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "⚠️ Nhập sai giá trị.");
                }
            });

            startBtn.addActionListener(ev -> simPanel.startSimulation());
        });
    }
}
