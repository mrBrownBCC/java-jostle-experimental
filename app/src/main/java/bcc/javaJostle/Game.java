package bcc.javaJostle;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension; // For setPreferredSize
import java.util.ArrayList;

class Game extends JPanel {
    ArrayList<Robot> robots;
    ArrayList<Projectile> projectiles = new ArrayList<>(); // Placeholder
    ArrayList<PowerUp> powerUps; // Assuming PowerUp is the class for individual power-ups
    Map map;
    int duration = 0;

    // Fields to store current display parameters
    private int currentWidth, currentHeight, currentCameraX, currentCameraY, currentTileSize;
    private double currentZoomFactor;
    private int maxDuration;
    public Game(ArrayList<String> robotFileNames, String mapName, int maxDuration){
        this.robots = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.maxDuration = maxDuration;
        // TODO: Implement actual robot loading based on robotFileNames
        // For example, you might have different Robot subclasses:
        // for (String robotName : robotFileNames) {
        //     if (robotName.equals("TankBot")) {
        //         // Assuming TankBot constructor: new TankBot(args...);
        //         // robots.add(new TankBot(...)); 
        //     } else {
        //         // robots.add(new DefaultPlayerRobot(...)); // A default or player-controlled robot
        //     }
        // }
        // For now, this list will be empty or populated by a specific mechanism not detailed here.
        robots.add(new MyRobot(128,128));
        robots.add(new RandomRobot(500, 400));
        map = new Map(mapName);
        // Set a preferred size for the game panel to help with layout
        setPreferredSize(new Dimension(Utilities.SCREEN_WIDTH, Utilities.SCREEN_HEIGHT));
        setFocusable(true); // Important for receiving keyboard events if needed later
    }

    // Method to update display parameters from GameManager
    public void setDisplayParameters(int width, int height, int cameraX, int cameraY,double zoomFactor) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.currentCameraX = cameraX;
        this.currentCameraY = cameraY;
        this.currentZoomFactor = zoomFactor; 
    }

    @Override
    protected void paintComponent(Graphics g) {
        long startTime = System.nanoTime();
        super.paintComponent(g); // Clears the panel
        if (map != null) {
            // Pass the integer zoomFactor directly
            map.display(g, currentWidth, currentHeight, currentCameraX, currentCameraY,  currentZoomFactor);
        }

        // Draw robots
        if (robots != null) {
            for (Robot robot : robots) {
                if (robot.getImage() != null && robot.isAlive()) {
                    int screenX = (int) (robot.getX() * currentZoomFactor - currentCameraX);
                    int screenY = (int) (robot.getY() * currentZoomFactor - currentCameraY);
                    int scaledSize = (int) (Utilities.ROBOT_SIZE * currentZoomFactor);
                    g.drawImage(robot.getImage(), screenX, screenY, scaledSize, scaledSize, null);
                }
            }
        }

        // Draw projectiles
        if (projectiles != null) {
            for (Projectile projectile : projectiles) {
                if (projectile.getProjectileImage() != null) {
                    int screenX = (int) (projectile.getX() * currentZoomFactor - currentCameraX);
                    int screenY = (int) (projectile.getY() * currentZoomFactor - currentCameraY);
                    int scaledSize = (int) (Utilities.PROJECTILE_SIZE * currentZoomFactor);
                    g.drawImage(projectile.getProjectileImage(), screenX, screenY, scaledSize, scaledSize, null);
                }
            }
        }

        // TODO: Draw powerUps
        if (powerUps != null) {
            for (PowerUp powerUp : powerUps) {
                // powerUp.draw(g, currentCameraX, currentCameraY, currentTileSize, currentZoomFactor);
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime); // in nanoseconds
        System.out.println("Map drawing time: " + duration / 1_000_000.0 + " ms");
    }

    public void step(){
        if (robots != null) {
            for (Robot robot : robots) {
                // Assuming Robot.think expects ArrayList<Robot>, ArrayList<Projectile>, Map, ArrayList<PowerUp>
                // Create a temporary empty list for projectiles if not managed elsewhere in Game
                
                // The Robot class's abstract think method signature is:
                // abstract void think(ArrayList<Robots> robots, ArrayList<Projectile> projectiles, Map map, ArrayList<PowerUps> powerups);
                // Assuming "Robots" is a typo for "Robot" and "PowerUps" for "PowerUp" in the abstract declaration.
                // If not, Game class needs to manage ArrayList<Robots> and ArrayList<PowerUps> types.
                robot.think(this.robots, projectiles, this.map, this.powerUps);
                robot.step(this); // Robot.step() is final and takes no arguments
            }
        }
        duration++;
        // addPowerUp(); // Logic to add power-ups periodically
    }

    private void addPowerUp(){
        // TODO: Implement logic to add new power-ups to the game
        // For example:
        // if (Math.random() < 0.01) { // 1% chance per step
        //     int x = (int)(Math.random() * (map.getTiles()[0].length)); // Random x within map bounds
        //     int y = (int)(Math.random() * (map.getTiles().length));    // Random y within map bounds
        //     if (map.getTiles()[y][x] != Utilities.WALL) { // Don't spawn in walls
        //         powerUps.add(new SpeedPowerUp(x, y)); 
        //     }
        // }
    }

    public int getDuration() {
        return duration;
    }
    public int getMaxDuration() {
        return maxDuration;
    }
    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }
    public Map getMap() {
        return map;
    }
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
    public ArrayList<PowerUp> getPowerUps() {
        return powerUps;
    }
}