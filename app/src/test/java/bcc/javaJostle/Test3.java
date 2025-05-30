package bcc.javaJostle;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;

public class Test3 {
    @Test
    public void battleRock(){
        ArrayList<String> robotNames = new ArrayList<>();
        robotNames.add("MyRobot");
        robotNames.add("Rando");
        int myRobotWins = 0;
        int totalGames = 3;

        for (int i = 0; i < totalGames; i++) {
            Game game = new Game(robotNames, "Standard", 7500);
            while(!game.isGameOver()) {
                game.step();
            }
            if(game.getWinner() instanceof MyRobot){
                myRobotWins++;
                System.out.println("Game " + (i + 1) + ": MyRobot won against Rando!");
            } else {
                System.out.println("Game " + (i + 1) + ": MyRobot lost against Rando. Winner: " + (game.getWinner() != null ? game.getWinner().getName() : "Draw/Null"));
            }
        }

        System.out.println("MyRobot won " + myRobotWins + " out of " + totalGames + " games against Rando.");
        assertTrue("MyRobot should win at least 2 out of 3 games. Wins: " + myRobotWins, myRobotWins >= 2);
    }
}