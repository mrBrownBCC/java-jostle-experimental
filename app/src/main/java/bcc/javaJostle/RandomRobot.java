package bcc.javaJostle;

import java.util.ArrayList;

public class RandomRobot extends Robot{
    private int curXMovement = 0;
    private int curYMovement = 0;
    public RandomRobot(int x, int y){
        super(x, y, 3, 3, 2, 3,"bob", "randomBot.png", "defaultProjectileImage.png");
        // Health: 3, Speed: 3, Attack Speed: 2, Projectile Strength: 3
        // Total = 11
        // Image name is "myRobotImage.png"
    }

    public void think(ArrayList<Robot> robots, ArrayList<Projectile> projectiles, Map map, ArrayList<PowerUp> powerups) {
        /* Implement your robot's logic here
         For example, you can move towards the nearest robot or shoot at a target
         to move, choose a direciton to go
         to move left - use xMove = -1
         to move right - use xMove = 1
         to move up - use yMove = -1
         to move down - use yMove = 1
         You can ONLY move in one direction at a time, if your output doesn't match the above you will get an error

         to shoot, use shootAtLocation(x, y) where x and y are the coordinates of the target
         only shoot when canAttack() is true!
        */

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
                if (robot != this) {
                    shootAtLocation(robot.getX() + Utilities.ROBOT_SIZE/2, robot.getY() + Utilities.ROBOT_SIZE/2);
                    break; // Shoot at the first target found
                }
            }
        }
    }
}
