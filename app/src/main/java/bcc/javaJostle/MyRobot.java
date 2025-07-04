package bcc.javaJostle;

import java.util.ArrayList;
public class MyRobot extends Robot{
    private int timer = 0;
    public MyRobot(int x, int y){
        super(x, y, 3, 3, 2, 2,"LeftRightBot", "myRobotImage.png", "defaultProjectileImage.png");
        // Health: 3, Speed: 3, Attack Speed: 2, Projectile Strength: 2
        // Total = 10
        // Image name is "myRobotImage.png"
    }

    public void think(ArrayList<Robot> robots, ArrayList<Projectile> projectiles, Map map, ArrayList<PowerUp> powerups) {
       /* Implement your robot's logic here
         For example, you can move towards the nearest robot or shoot at a target
         to move, choose a direciton to go
         to move left - use xMovement = -1
         to move right - use xMovement = 1
         to move up - use yMovement = -1
         to move down - use yMovement = 1
         You can ONLY move in one direction at a time, if your output doesn't match the above you will get an error

         to shoot, use shootAtLocation(x, y) where x and y are the coordinates of the target
         only shoot when canAttack() is true!
         make sure to check if the target is alive before shooting at it
        */
        timer++;
        if(timer % 40 < 10){
            yMovement = -1;
        } else if(timer % 40 < 20){ 
            xMovement = 1;
        } else if(timer % 40 < 30){
            yMovement = 1;
        } else {
            xMovement = -1;
        }
    }
}
