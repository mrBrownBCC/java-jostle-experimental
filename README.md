# Setup Instructions
In order to run the GUI:
Go to the ports tab. Click "Add port" and type the number 6080.
Follow the link to your forwarded address. This is where your GUI should be available once you run your code. Note that the GUI won't work very well until you have finished most of the code - tests will be used to make sure the intermediate steps works well. 

To access the code, go to app > src > main > java > MyRobot

To run the code, go to app.java and click run


# Programming Instructions
0. Choose your robot stats! This can be done by choosing the values in super() in the MyRobot constructor. 
1. Implement the 'think' method inside of the MyRobot class. More details are provided in comments at that location. That's it - that's the project
2. If you would like to add images for your robot and projectile, add these in resources/images. 

# Robot Strategy Ideas

1. Shoot where the enemy will be, not where they are
2. Dodge bullets - you get a list of these
3. Pathfind to powerups (use dijkstras!)
4. Have some sort of plan for the free for all mode. 
5. Be aggressive if winning, run away if losing

# Grading
50% - code compiles
85% - your robot beats the rock robot on the standard map
100% - your robot beats the random robot best 2 out of 3 on the standard map


# Submission
-1. Join the same group as your team on canvas. 
0. PLEASE PLEASE PLEASE add your team name, no spaces, into name.txt at the top level directory. 
1. Testing everything. Run the command - 
``` 
gradle test
```

2. Submit on github classroom by running the following commands. This also saves your work permanently (unless you actively want to delete it). 

```
git add . 
git commit -m "submitting"
git push
```


# TODO FOR NEXT YEAR
-Figure out class naming to have stuff work
-instructions for images
-leave in more demo robots
-leave more helper functions for robot to use
-better tooling - for example run 50x rounds and see win percent
-easier way to win - simpler bot to face than random - maybe switches direction less?
-allow for saving of different robot versions using command
-at least 3 weeks for this project
-more speed options