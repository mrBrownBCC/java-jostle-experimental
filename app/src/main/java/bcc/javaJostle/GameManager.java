package bcc.javaJostle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays; // For Arrays.asList if needed for robotOptions
import java.text.DecimalFormat; // For formatting speed factor
import java.awt.image.BufferedImage;

public class GameManager {
    private ArrayList<String> robotOptions;
    private String[] mapNames = { "Standard" };

    private JFrame frame; // Menu frame
    private JComboBox<String> robotComboBox;
    private JComboBox<String> mapComboBox;
    private JButton startButton;

    // Game-specific fields
    private JFrame gameFrame;
    private Game gamePanel;
    private Timer gameTimer;
    private JLabel timerLabel;
    private JLabel speedLabel; // Label to display current speed
    private JPanel robotStatusPanel;

    private double cameraX = 0;
    private double cameraY = 0;
    private double zoomFactor = 1.0;
    private double gameSpeedFactor = 1.0;
    private int gameLoopCounter = 0;

    private final int SCROLL_STEP = 32;
    private final int GAME_TIMER_DELAY_MS = 16;

    private final double[] speedFactors = { 0.25, 0.5, 1.0, 2.0, 4.0 };
    private int currentSpeedIndex = 2; // Index for 1.0x speed

    private static final DecimalFormat df = new DecimalFormat("0.##");

    public GameManager(ArrayList<String> robotOptions, String[] mapNames) {
        this.robotOptions = robotOptions;
        this.mapNames = mapNames;
        Utilities.loadImages();
        initMenu();
    }

    private void initMenu() {
        frame = new JFrame("Java Jostle - Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel robotLabelText = new JLabel("Choose Robot:");
        if (this.robotOptions == null) {
            this.robotOptions = new ArrayList<>(Arrays.asList("DefaultBot"));
        }
        robotComboBox = new JComboBox<>(robotOptions.toArray(new String[0]));

        JLabel mapLabelText = new JLabel("Choose Map:");
        if (this.mapNames == null || this.mapNames.length == 0) {
            this.mapNames = new String[] { "Standard" };
        }
        mapComboBox = new JComboBox<>(mapNames);

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.addActionListener(e -> startGame());

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(robotLabelText, gbc);
        gbc.gridx = 1;
        frame.add(robotComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(mapLabelText, gbc);
        gbc.gridx = 1;
        frame.add(mapComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(startButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startGame() {
        String selectedRobotName = (String) robotComboBox.getSelectedItem();
        String selectedMapName = (String) mapComboBox.getSelectedItem();

        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }

        ArrayList<String> selectedRobotList = new ArrayList<>();
        if (selectedRobotName != null) {
            selectedRobotList.add(selectedRobotName);
        } else {
            selectedRobotList.add("DefaultBot");
        }

        int maxGameDurationSeconds = 300;

        gamePanel = new Game(selectedRobotList, selectedMapName, maxGameDurationSeconds);
        gamePanel.setPreferredSize(new Dimension(Utilities.SCREEN_WIDTH, Utilities.SCREEN_HEIGHT));
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);

        gameFrame = new JFrame("Java Jostle - Game");
        gameFrame.setBackground(Color.BLACK);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBackground(Color.DARK_GRAY);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(Color.GRAY);
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.insets = new Insets(2, 5, 2, 5);

        // Zoom Buttons (Left Side)
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Increased gap
        leftControls.setOpaque(false);
        JButton zoomInButton = new JButton("+");
        zoomInButton.setFont(new Font("Arial", Font.BOLD, 18));
        zoomInButton.addActionListener(e -> {
            double oldZoomFactor = this.zoomFactor;
            this.zoomFactor *= 1.5; // Adjusted zoom step
            adjustCameraForCenteredZoom(oldZoomFactor, this.zoomFactor);
            updateGameDisplayAndRepaint();
            gamePanel.requestFocusInWindow();
        });
        leftControls.add(zoomInButton);

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setFont(new Font("Arial", Font.BOLD, 18));
        zoomOutButton.addActionListener(e -> {
            double oldZoomFactor = this.zoomFactor;
            this.zoomFactor = Math.max(0.1, this.zoomFactor / 1.5); // Adjusted zoom step
            adjustCameraForCenteredZoom(oldZoomFactor, this.zoomFactor);
            updateGameDisplayAndRepaint();
            gamePanel.requestFocusInWindow();
        });
        leftControls.add(zoomOutButton);

        cgbc.gridx = 0;
        cgbc.gridy = 0;
        cgbc.weightx = 0.33;
        cgbc.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(leftControls, cgbc);

        // Timer and Speed Display (Center)
        JPanel centerControls = new JPanel();
        centerControls.setLayout(new BoxLayout(centerControls, BoxLayout.Y_AXIS));
        centerControls.setOpaque(false);

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerControls.add(timerLabel);

        speedLabel = new JLabel("Speed: " + df.format(gameSpeedFactor) + "x");
        speedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerControls.add(speedLabel);

        cgbc.gridx = 1;
        cgbc.gridy = 0;
        cgbc.weightx = 0.34;
        cgbc.anchor = GridBagConstraints.CENTER;
        controlPanel.add(centerControls, cgbc);

        // Speed Toggle Button (Right Side)
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Increased gap
        rightControls.setOpaque(false);
        JButton speedToggleButton = new JButton("Speed");
        speedToggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        speedToggleButton.addActionListener(e -> {
            currentSpeedIndex = (currentSpeedIndex + 1) % speedFactors.length;
            gameSpeedFactor = speedFactors[currentSpeedIndex];
            speedLabel.setText("Speed: " + df.format(gameSpeedFactor) + "x");
            gamePanel.requestFocusInWindow();
        });
        rightControls.add(speedToggleButton);

        cgbc.gridx = 2;
        cgbc.gridy = 0;
        cgbc.weightx = 0.33;
        cgbc.anchor = GridBagConstraints.LINE_END;
        controlPanel.add(rightControls, cgbc);

        controlPanel.setPreferredSize(new Dimension(Utilities.SCREEN_WIDTH, 60));
        bottomContainer.add(controlPanel);

        robotStatusPanel = new JPanel();
        robotStatusPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        robotStatusPanel.setBackground(Color.DARK_GRAY.darker());
        robotStatusPanel.setPreferredSize(new Dimension(Utilities.SCREEN_WIDTH, 100));
        bottomContainer.add(robotStatusPanel);

        gameFrame.add(gamePanel, BorderLayout.CENTER);
        gameFrame.add(bottomContainer, BorderLayout.SOUTH);

        gameFrame.pack();
        gameFrame.setMinimumSize(new Dimension(600, 400));
        gameFrame.setLocationRelativeTo(null);

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                boolean moved = false;
                double scrollAmount = SCROLL_STEP / zoomFactor; // Adjust scroll speed by zoom

                if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
                    cameraX -= scrollAmount;
                    moved = true;
                } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                    cameraX += scrollAmount;
                    moved = true;
                } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                    cameraY -= scrollAmount;
                    moved = true;
                } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                    cameraY += scrollAmount;
                    moved = true;
                }
                if (moved)
                    updateGameDisplayAndRepaint();
            }
        });

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    double mousePanelX = e.getX();
                    double mousePanelY = e.getY();
                    double oldZoomFactor = zoomFactor;
                    double newZoomFactor = zoomFactor * 1.5; // Zoom in
                    double worldMouseX = (mousePanelX + cameraX) / oldZoomFactor;
                    double worldMouseY = (mousePanelY + cameraY) / oldZoomFactor;
                    zoomFactor = newZoomFactor;
                    cameraX = worldMouseX * newZoomFactor - mousePanelX;
                    cameraY = worldMouseY * newZoomFactor - mousePanelY;
                    updateGameDisplayAndRepaint();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    double mousePanelX = e.getX();
                    double mousePanelY = e.getY();
                    double oldZoomFactor = zoomFactor;
                    double newZoomFactor = Math.max(0.1, zoomFactor / 1.5); // Zoom out
                    double worldMouseX = (mousePanelX + cameraX) / oldZoomFactor;
                    double worldMouseY = (mousePanelY + cameraY) / oldZoomFactor;
                    zoomFactor = newZoomFactor;
                    cameraX = worldMouseX * newZoomFactor - mousePanelX;
                    cameraY = worldMouseY * newZoomFactor - mousePanelY;
                    updateGameDisplayAndRepaint();
                }
                gamePanel.requestFocusInWindow();
            }
        });

        gameFrame.setVisible(true);
        gamePanel.requestFocusInWindow();

        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        gameTimer = new Timer(GAME_TIMER_DELAY_MS, ae -> gameLoop());
        gameTimer.start();

        updateGameDisplayAndRepaint();
    }

    private void adjustCameraForCenteredZoom(double oldZoomFactor, double newZoomFactor) {
        if (gamePanel == null || gamePanel.getWidth() == 0 || gamePanel.getHeight() == 0)
            return;
        double panelCenterX = gamePanel.getWidth() / 2.0;
        double panelCenterY = gamePanel.getHeight() / 2.0;
        double worldCenterX = (panelCenterX + cameraX) / oldZoomFactor;
        double worldCenterY = (panelCenterY + cameraY) / oldZoomFactor;
        cameraX = worldCenterX * newZoomFactor - panelCenterX;
        cameraY = worldCenterY * newZoomFactor - panelCenterY;
    }

    private void updateRobotStatusDisplay() {
        if (robotStatusPanel == null || gamePanel == null || gamePanel.getRobots() == null) {
            return;
        }
        robotStatusPanel.removeAll();

        for (Robot robot : gamePanel.getRobots()) {
            JPanel singleRobotPanel = new JPanel();
            singleRobotPanel.setOpaque(false);
            singleRobotPanel.setLayout(new BoxLayout(singleRobotPanel, BoxLayout.Y_AXIS));
            singleRobotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            BufferedImage img = robot.getImage();
            JLabel imageLabel;
            if (img != null) {
                Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                imageLabel = new JLabel(new ImageIcon(scaledImg));
            } else {
                imageLabel = new JLabel("No Img");
            }
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            singleRobotPanel.add(imageLabel);

            JLabel nameLabel = new JLabel(robot.getName());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            singleRobotPanel.add(nameLabel);

            HealthBar healthBar = new HealthBar(robot.getHealth(), robot.getMaxHealth());
            healthBar.setPreferredSize(new Dimension(50, 10));
            healthBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            singleRobotPanel.add(healthBar);

            robotStatusPanel.add(singleRobotPanel);
        }
        robotStatusPanel.revalidate();
        robotStatusPanel.repaint();
    }

    private void updateGameDisplayAndRepaint() {
        if (gamePanel != null && gameFrame != null && gameFrame.isVisible()) {
            gamePanel.setDisplayParameters(
                    gamePanel.getWidth(),
                    gamePanel.getHeight(),
                    (int) Math.round(cameraX),
                    (int) Math.round(cameraY),
                    this.zoomFactor);
            gamePanel.repaint();

            if (timerLabel != null) {
                double ticksPerSecond = 1000.0 / GAME_TIMER_DELAY_MS;
                double elapsedSeconds = gamePanel.getDuration() / ticksPerSecond;
                int secondsRemaining = (int) (gamePanel.getMaxDuration() - elapsedSeconds);
                if (secondsRemaining < 0)
                    secondsRemaining = 0;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            }
            if (speedLabel != null) { // Update speed label text if it exists
                speedLabel.setText("Speed: " + df.format(gameSpeedFactor) + "x");
            }
            updateRobotStatusDisplay();
        }
    }

    private void gameLoop() {
        if (gamePanel == null)
            return;
        long startTime = System.nanoTime();

        // Code section you want to measure
        // e.g., your gamePanel.step() or a part of paintComponent()

        
        double stepsToExecute = 0;
        double epsilon = 0.001; // Adjusted epsilon for more precise float comparison

        // Check current gameSpeedFactor against predefined values
        if (Math.abs(gameSpeedFactor - speedFactors[0]) < epsilon) { // 0.25x
            if (gameLoopCounter % 4 == 0)
                stepsToExecute = 1;
        } else if (Math.abs(gameSpeedFactor - speedFactors[1]) < epsilon) { // 0.5x
            if (gameLoopCounter % 2 == 0)
                stepsToExecute = 1;
        } else if (Math.abs(gameSpeedFactor - speedFactors[2]) < epsilon) { // 1.0x
            stepsToExecute = 1;
        } else if (Math.abs(gameSpeedFactor - speedFactors[3]) < epsilon) { // 2.0x
            stepsToExecute = 2;
        } else if (Math.abs(gameSpeedFactor - speedFactors[4]) < epsilon) { // 4.0x
            stepsToExecute = 4;
        }

        for (int i = 0; i < stepsToExecute; i++) {
            gamePanel.step();
            if (gamePanel.getDuration() >= gamePanel.getMaxDuration() * (1000.0 / GAME_TIMER_DELAY_MS)) {
                if (gameTimer != null)
                    gameTimer.stop();
                break;
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime); // in nanoseconds
        System.out.println("step time " + duration / 1_000_000.0 + " ms");
        updateGameDisplayAndRepaint();
        gameLoopCounter++;
        if (gameLoopCounter >= 10000)
            gameLoopCounter = 0;
    }

    class HealthBar extends JPanel {
        private int currentHealth;
        private int maxHealth;

        public HealthBar(int currentHealth, int maxHealth) {
            this.currentHealth = currentHealth;
            this.maxHealth = maxHealth;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (maxHealth <= 0)
                return;

            int width = getWidth();
            int height = getHeight();
            double healthPercentage = (double) currentHealth / maxHealth;

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, width, height);

            if (healthPercentage > 0.7) {
                g.setColor(Color.GREEN);
            } else if (healthPercentage > 0.3) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(0, 0, (int) (width * healthPercentage), height);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, width - 1, height - 1);
        }
    }
}