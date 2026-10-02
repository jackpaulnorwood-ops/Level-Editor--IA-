import java.awt.Rectangle;

public class Player extends Object {
    private int moveX=0;
    private int moveY=0;
    private int speed;

    public Player(Rectangle rect, int speed) {
        super(rect);
        this.speed=speed;
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

    public void move() {
        setX(getRect().x+moveX*speed);
        setY(getRect().y+moveY*speed);
    }
}