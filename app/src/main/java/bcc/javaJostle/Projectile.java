package bcc.javaJostle;

import java.awt.image.BufferedImage;

public class Projectile {
    private double x;
    private double y;
    // private double speed; // Replaced by projectileSpeed
    private double angle; // Will be calculated
    private Robot owner;

    // New fields based on the constructor
    private int xTarget;
    private int yTarget;
    private int projectileSpeed;
    private int projectileDamage;
    private BufferedImage projectileImage;

    public Projectile(double x, double y, int xTarget, int yTarget, int projectileSpeed, int projectileDamage, BufferedImage projectileImage, Robot owner) {
        this.x = x;
        this.y = y;
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.projectileSpeed = projectileSpeed;
        this.projectileDamage = projectileDamage;
        this.projectileImage = projectileImage;
        this.owner = owner;

        // Calculate the angle towards the target
        this.angle = Math.atan2(this.yTarget - this.y, this.xTarget - this.x);
    }

    public void updatePosition(double deltaTime) {
        // Use projectileSpeed for movement
        x += this.projectileSpeed * Math.cos(this.angle) * deltaTime;
        y += this.projectileSpeed * Math.sin(this.angle) * deltaTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Getter for the calculated angle
    public double getAngle() {
        return angle;
    }

    public Robot getOwner() {
        return owner;
    }

    // Getters for new fields
    public int getXTarget() {
        return xTarget;
    }

    public int getYTarget() {
        return yTarget;
    }

    public int getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getProjectileDamage() {
        return projectileDamage;
    }

    public BufferedImage getProjectileImage() {
        return projectileImage;
    }
}