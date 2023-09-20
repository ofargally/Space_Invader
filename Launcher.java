//Contains main... run me to launch the game!
public class Launcher{
           
    //Initializes and launches the game
    public static void main(String[] args){              
        AbstractGame game = new FargallyGame();
        game.setDebugMode(false);
        	game.play();    
    }        
    
}
