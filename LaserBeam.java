public class LaserBeam extends Entity implements Scrollable{
	
	private static final String LASER_BEAM = "assets/Space/LaserBeam.png";
	
	private static final int GET_WIDTH = 50;
    private static final int GET_HEIGHT = 50;
    
    private static final int GET_SCROLL_SPEED = 10;
    
    public LaserBeam(){
    	this(0, 0);
    }
    public LaserBeam(int x, int y){
    	super(x, y, GET_WIDTH, GET_HEIGHT, LASER_BEAM);
    }
    public LaserBeam(int x, int y, String imageFileName){
        super(x, y, GET_WIDTH, GET_HEIGHT, imageFileName);
    }
    public void scroll(){
        setX(getX() + GET_SCROLL_SPEED);
    }
    public int getScrollSpeed(){
    	return GET_SCROLL_SPEED;
    }
}