package bcc.javaJostle;

import java.util.ArrayList;

public class Rando extends Robot{
    private int curXMovement = 0;
    private int curYMovement = 0;
    public Rando(int x, int y){
        super(x, y, 3, 2, 2, 3,"Rando", "randomBot.png", "defaultProjectile.png");
        // Health: 3, Speed: 2, Attack Speed: 2, Projectile Strength: 3
        // Total = 10
    }

    public void think(ArrayList<Robot> robots, ArrayList<Projectile> projectiles, Map map, ArrayList<PowerUp> powerups) {
      
        if(Math.random() < 0.1) {
          double r = Math.random();
            if (r < 0.25) {
                curXMovement = -1; // Move left
                curYMovement = 0; // No vertical movement
            } else if (r < 0.5) {
                curXMovement = 1; // Move right
                curYMovement = 0; // No vertical movement
            } else if (r < 0.75) {
                curYMovement = -1; // Move up
                curXMovement = 0; // No horizontal movement
            } else {
                curYMovement = 1; // Move down
                curXMovement = 0; // No horizontal movement
            }
        } 
        xMovement = curXMovement;
        yMovement = curYMovement;
        if(canAttack()){
            for(Robot robot : robots) {
                if (robot != this && robot.isAlive() ){
                    shootAtLocation(robot.getX() + Utilities.ROBOT_SIZE/2, robot.getY() + Utilities.ROBOT_SIZE/2);
                    break; // Shoot at the first target found
                }
            }
        }
    }
}
