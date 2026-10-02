import java.awt.Rectangle;

public class Object {
    private Rectangle rect;

    public Object(Rectangle rect) {
        this.rect=rect;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public void setX(int x) {
        rect.x=x;
    }

    public void setY(int y) {
        rect.y=y;
    }

    public void setW(int w) {
        rect.width=w;
    }

    public void setH(int h) {
        rect.height=h;
    }
}
