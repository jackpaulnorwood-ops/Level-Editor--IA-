import java.awt.Rectangle;
import java.util.ArrayList;

public class Player extends Object {
    private static final int JUMP_BUFFER_FRAMES = 8;
    private int moveX=0;
    private int moveY=0;
    private int speed;
    private double gravity;
    private double vY;
    private boolean onGround;
    private int jumpBufferFrames;

    public Player(Rectangle rect, String imageID, int speed, double gravity) {
        super(rect, imageID);
        this.speed=speed;
        this.gravity=gravity;
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

    public void setMove(char key, boolean pressed, boolean gravityOn) {
        if(key=='a'||key=='A') {
            moveX=pressed?-1:0;
        }
        if(key=='d'||key=='D') {
            moveX=pressed?1:0;
        }
        if(key=='w'||key=='W') {
            if (pressed && !gravityOn) {
                moveY=-1;
            } else if (!pressed && moveY==-1) {
                moveY=0;
            }
        }
        if(key=='s'||key=='S') {
            if (pressed && !gravityOn) {
                moveY=1;
            } else if (!pressed && moveY==1) {
                moveY=0;
            }
        }
    }

    public void move(ArrayList<Solid> solids, boolean gravityOn) {
        for (int i = 0; i < speed; i++) {

            int verticalMove = gravityOn ? 0 : moveY;
            Rectangle nextPosition = getNextPosition(moveX, verticalMove);

            if (canMove(nextPosition, solids)) {
                setX(nextPosition.x);
                setY(nextPosition.y);
            } else {
                break;
            }
        }

        if(gravityOn) {
            onGround=false;
            vY=Math.min(vY+gravity, 20);

            int direction=Integer.signum((int)vY);
            for(int i=0; i<Math.abs(vY); i++) {
                Rectangle nextPosition = getNextPosition(0, direction);

                if (!canMove(nextPosition, solids)) {
                    if (direction > 0) {
                        onGround = true;
                    }
                    vY = 0;
                    break;
                }

                setY(nextPosition.y);
            }

            if(jumpBufferFrames > 0) {
                if(onGround) {
                    vY=-15;
                    onGround=false;
                    jumpBufferFrames=0;
                } else {
                    jumpBufferFrames--;
                }
            }
        } else {
            vY = 0;
            onGround = false;
            jumpBufferFrames=0;
        }
    }

    public void jump(boolean gravityOn) {
        if(gravityOn) {
            jumpBufferFrames=JUMP_BUFFER_FRAMES;
        }
    }
}