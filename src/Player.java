import java.awt.Rectangle;
import java.util.ArrayList;

public class Player extends Object {
    private int moveX=0;
    private int moveY=0;
    private int speed;

    public Player(Rectangle rect, int speed) {
        super(rect);
        this.speed=speed;
    }

    public boolean canMove(Rectangle nextPosition, ArrayList<Object> objects) {
        for (Object obj : objects) {
            if (nextPosition.intersects(obj.getRect())) {
                return false;
            }
        }

        return true;
    }

    public Rectangle getNextPosition(int dX, int dY) {
        return new Rectangle(getRect().x+dX, getRect().y+dY, getRect().width, getRect().height);
    }

    public void setMove(char key, boolean pressed) {
        if(key=='w') {
            moveY=pressed?-1:0;
        }
        if(key=='s') {
            moveY=pressed?1:0;
        }
        if(key=='a') {
            moveX=pressed?-1:0;
        }
        if(key=='d') {
            moveX=pressed?1:0;
        }
    }

    public void move(ArrayList<Object> objects) {
        for (int i = 0; i < speed; i++) {

            Rectangle nextPosition = getNextPosition(moveX, moveY);

            if (canMove(nextPosition, objects)) {
                setX(nextPosition.x);
                setY(nextPosition.y);
            } else {
                break;
            }
        }
    }
}