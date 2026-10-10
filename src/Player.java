import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Object {
    private int moveX=0;
    private int moveY=0;
    private int speed;

    public Player(Rectangle rect, BufferedImage image, int speed) {
        super(rect, image);
        this.speed=speed;
    }

    public boolean canMove(Rectangle nextPosition, ArrayList<Solid> solids) {
        for (Solid sol : solids) {
            if (nextPosition.intersects(sol.getRect())) {
                return false;
            }
        }

        return true;
    }

    public Rectangle getNextPosition(int dX, int dY) {
        return new Rectangle(getRect().x+dX, getRect().y+dY, getRect().width, getRect().height);
    }

    public void setMove(char key, boolean pressed) {
        if(key=='w'||key=='W') {
            moveY=pressed?-1:0;
        }
        if(key=='s'||key=='S') {
            moveY=pressed?1:0;
        }
        if(key=='a'||key=='A') {
            moveX=pressed?-1:0;
        }
        if(key=='d'||key=='D') {
            moveX=pressed?1:0;
        }
    }

    public void move(ArrayList<Solid> solids) {
        for (int i = 0; i < speed; i++) {

            Rectangle nextPosition = getNextPosition(moveX, moveY);

            if (canMove(nextPosition, solids)) {
                setX(nextPosition.x);
                setY(nextPosition.y);
            } else {
                break;
            }
        }
    }
}