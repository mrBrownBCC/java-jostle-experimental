package bcc.javaJostle;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;

public class Test2 {
    @Test
    public void battleRock(){
        ArrayList<String> robotNames = new ArrayList<>();
        robotNames.add("MyRobot");
        robotNames.add("Rock");
        Game game = new Game(robotNames, "Standard",7500);
        while(!game.isGameOver()) {
            game.step();
        }
        if(game.getWinner() instanceof MyRobot){
            System.out.println("MyRobot won against Rock!");
            assertTrue(true);
        } else if(game.getWinner() == null) {
            System.out.println("The game ended in a draw against Rock.");
            assertTrue(false);
        } else {
            System.out.println("MyRobot lost against Rock.");
            assertTrue(false);
        }
    }
}
