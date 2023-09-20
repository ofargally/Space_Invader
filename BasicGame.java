import java.awt.*;
import java.awt.event.*;
import java.util.*;

//A Basic version of the scrolling game, featuring Avoids, Gets, and RareGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class BasicGame extends AbstractGame {
           
    //Dimensions of game window
    protected static final int DEFAULT_WIDTH = 900;
    protected static final int DEFAULT_HEIGHT = 600;  
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 2000;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE = 20;    
 
   
    protected static final String INTRO_SPLASH_FILE = "assets/Space/SPLASH_SCREEN.png";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    public static final int PLAY_AGAIN = KeyEvent.VK_Y;
    public static final int NO_PLAY_AGAIN = KeyEvent.VK_N;
    public static final int SHOOT_LASER = KeyEvent.VK_SPACE;
    
    
    public static final String BACK_GROUND_IMAGE = "assets/Space/BackGround.png"; 
    
    //Interval that Ent ities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;
    //Maximum Entities that can be spawned on a single call to spawnEntities
    protected static final int MAX_SPAWNS = 3;

   
    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();

    //Player's current score
    protected int score;
    
    //Stores a reference to game's Player object for quick reference
    //(This Player will also be in the displayList)
    protected Player player;
    
    protected int currentSpeed = 100;
    
    protected boolean winOrLoss;
    
    
    public BasicGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public BasicGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void preGame(){
    	this.setDebugMode(false);
        this.setBackgroundImage(BACK_GROUND_IMAGE);
        this.setSplashImage(INTRO_SPLASH_FILE);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        score = 0;
    }
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();   
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0)            
            spawnEntities();
        	deleteOldElements();
        handlePlayerCollisions();
        //Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + " Score: " + score);
    }
    
    protected void handlePlayerCollisions(){
    	for (int i = 0; i < displayList.size(); i++){
    		Entity element = displayList.get(i);
    		if (element instanceof Consumable && player.isCollidingWith(element))
    			handleCollision((Consumable)element);
    	}
    }
    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int i = 0; i < displayList.size(); i++){
        	Entity element = displayList.get(i);
           if (element instanceof Scrollable){
           	  ((Scrollable)element).scroll();
           }
        }
    }
    
    //Spawn new Entities on the right edge of the game board
    protected void spawnEntities(){
    	for (int i = 0; i <= rand.nextInt(MAX_SPAWNS); i++){
    		int randomSpawnFreq = rand.nextInt(100);
    		if (randomSpawnFreq < 50){
    			Avoid avoidElement = new Avoid();
    			Avoid randomElement = new Avoid(getWindowWidth(), rand.nextInt(getWindowHeight() - avoidElement.getHeight()));
    			if (checkCollision(randomElement) == null){
    				displayList.add(randomElement);
    			}
    		}
    		else if (randomSpawnFreq < 85){
    			Get getElement = new Get();
    			Get randomElement = new Get(getWindowWidth(), rand.nextInt(getWindowHeight() - getElement.getHeight()));
    			if (checkCollision(randomElement) == null){
    				displayList.add(randomElement);
    			}
    		}
    		else if (randomSpawnFreq < 100){
    			RareGet rareElement = new RareGet();
    			RareGet randomElement = new RareGet(getWindowWidth(), rand.nextInt(getWindowHeight() - rareElement.getHeight()));
    			if (checkCollision(randomElement) == null){
    				displayList.add(randomElement);
    			}
    		}                             
    	}
    }
    
    
    //Called whenever it has been determined that the Player collided with a consumable
    protected void handleCollision(Consumable collidedWith){
    		int initial_HP = player.getHP();
    		int damageValue = collidedWith.getDamageValue();
    		int scoredPoints = collidedWith.getPointsValue();
     	   	if (collidedWith instanceof Avoid){
     	   	   	player.setHP(initial_HP + damageValue);
     	   	}
     	   	else if (collidedWith instanceof Get){
     	   		if (collidedWith instanceof RareGet){
     	   			if (player.getHP() < 3){
     	   				player.setHP(initial_HP - damageValue);
     	   			}
     	   			score += 10;
     	   		}
     	   		else{
     	   		    score += 20;
     	   		}
     	   	}
     	  displayList.remove(collidedWith);
    }
    
    

    protected void deleteOldElements(){
    	for (int i = 0; i < displayList.size(); i++){
    		Entity element = ((Entity)displayList.get(i));
    		if (element.getX() < -50){
    			displayList.remove(element);
    		}	
    	}
    }
    
    //Called once the game is over, performs any end-of-game operations
    protected void postGame(){
    	if (winOrLoss == true){
    		super.setTitleText("GAME OVER! YOU WIN!");
    	}
    	else{
    		super.setTitleText("GAME OVER! YOU LOSE!");
    	}
    }
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
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
     
    
    //Reacts to a single key press on the keyboard
    //Override's AbstractGame's handleKey
    protected void handleKey(int key){
        //first, call AbstractGame's handleKey to deal with any of the 
        //fundamental key press operations
        super.handleKey(key);
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key));
        //if a splash screen is up, only react to the advance splash key
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            
        }
        else {
        	if (key == super.UP_KEY && player.getY() >= 0){
        		if (player.getY() < player.getMovementSpeed()){
        			player.setY(player.getY());
        		}
        		else{
        			player.setY(player.getY() - player.getMovementSpeed());
        			//System.out.println("Position of player: " + player.getY());
        			//System.out.println("Window Height: "+ this.getWindowHeight());
        		}
        	}
        	else if (key == super.DOWN_KEY && this.getWindowHeight() >= (player.getY() + player.getWidth())){
        		if (this.getWindowHeight() < player.getY() + player.getWidth() + player.getMovementSpeed()){
        			player.setY(player.getY());
        		}
        		else{
        			player.setY(player.getY() + player.getMovementSpeed());
        			//System.out.println(player.getY());
        		}
        	}
        	else if (key == super.LEFT_KEY && player.getX() >= 0){
        		if (player.getX() < player.getMovementSpeed()){
        			player.setX(player.getX());
        		}
        		else{
        			player.setX(player.getX() - player.getMovementSpeed());
        			//System.out.println(player.getX());
        		}
        	}
        	else if (key == super.RIGHT_KEY && this.getWindowWidth() >= (player.getX() + player.getHeight())){
        		if (this.getWindowWidth() < player.getX() + player.getWidth() + player.getMovementSpeed()){
        			player.setX(player.getX());
        		}
        		else{
        			player.setX(player.getX() + player.getMovementSpeed());
        			//System.out.println(player.getX());
        		}
        	}        	
        	else if (key == super.KEY_PAUSE_GAME){
        		if (super.isPaused == true)
        			super.isPaused = false;
        		else 
        			super.isPaused = true;
        	}
        	else if (key == SPEED_UP_KEY){
        		if (currentSpeed <= MAX_GAME_SPEED){
        			currentSpeed += SPEED_CHANGE;
        			this.setGameSpeed(currentSpeed);
        		}
        	}
        	else if (key == SPEED_DOWN_KEY){
        		if (currentSpeed > 0){
        			currentSpeed -= SPEED_CHANGE;
        			if (currentSpeed != 0)
        				this.setGameSpeed(currentSpeed);
        		}
        	}
        }
    }
}