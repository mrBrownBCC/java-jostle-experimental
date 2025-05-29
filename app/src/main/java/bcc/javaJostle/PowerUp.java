package bcc.javaJostle;

import java.awt.image.BufferedImage;


public class PowerUp {
    private BufferedImage image;
    private int x;
    private int y;
    private String type;
    public PowerUp(int x, int y) {
        double r = Math.random();
        if(r < .33) {
            this.type = "health";
        this.image = Utilities.loadImage("healthPack.png");
        } else if(r < .66) {
            this.type = "speed";
            this.image = Utilities.loadImage("speedBoost.png");
        } else {
            this.type = "attack";
            this.image = Utilities.loadImage("attackBoost.png");
        } 
        this.x = x;
        this.y = y;
    }
}