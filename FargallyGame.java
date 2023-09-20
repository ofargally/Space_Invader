import java.awt.*;
import java.awt.event.*;
import java.util.*;




public class FargallyGame extends BasicGame {
	
	 protected static final String GAME_OVER_SPLASH = "assets/Space/gameover.gif";
	 protected static final String GAME_WON_SPLASH = "assets/Space/winner.gif";
	 
	//How many aliens were killed
	private int alienScore = 0;
	private int prevAlienScore = 0;
	private int currentCapacity = 10;
	
    protected void shootLaser(){
    	while (currentCapacity > 0){
    		LaserBeam beam = new LaserBeam(player.getX() + player.getWidth(), player.getY());
    		displayList.add(beam);
    		currentCapacity -= 1;
    		break;
    	}
    }
    
	protected void handleKey(int key){
		super.handleKey(key);
		if (key == SHOOT_LASER){
        		shootLaser();
        }	
	}
	
	
	protected void handleBeamCollisions(){
		for (int i = 0; i < displayList.size(); i++){
    		Entity element = displayList.get(i);
    		if (element instanceof LaserBeam){
    			if (checkCollision(element) != null){
    				displayList.remove(element);
    				if (checkCollision(element) instanceof Avoid){
    					displayList.remove(checkCollision(element));
    					alienScore += 1;
    					score += 10;
    				}
    			}
    		}
    	}
    }
    

    protected void handleCollision(Consumable collidedWith){
    	super.handleCollision(collidedWith);
    	if (collidedWith instanceof RareGet){
    		currentCapacity += 5;
    	}
    }
    
	
	
    //called on each "tick" (if not paused) to update the state of the game
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();   
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0)            
            spawnEntities();
        	deleteOldElements();
        handlePlayerCollisions();
        handleBeamCollisions();
        updateGameDifficulty();
        //Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + " Score: " + score + " Alien Score: " + alienScore  + " Laser Capacity: " + currentCapacity);
    
    	
    }
   
    protected void updateGameDifficulty(){
    	if (alienScore - prevAlienScore == 10){
    		if (currentSpeed <= MAX_GAME_SPEED){
        		currentSpeed += SPEED_CHANGE;
        		this.setGameSpeed(currentSpeed);
        	}
        	prevAlienScore = alienScore;
    	}
    }
    //called once before the game starts
    protected void preGame(){
    	super.preGame();
    	
    }
    
    //called once after the game ends
    protected void postGame(){
    	super.postGame();
    	if (winOrLoss)
    		this.setSplashImage(GAME_WON_SPLASH);
    	else
    		this.setSplashImage(GAME_OVER_SPLASH);
    }
    
    //Returns a boolean indicating whether the game is over (true) or not (false).

    protected boolean isGameOver(){
    	if (SCORE_TO_WIN <= score){
			winOrLoss = true;
			return true;
		}
		else if (player.getHP() == 0){
			winOrLoss = false;
			return true;
		}
		else{
			return false;
		}
	}
}