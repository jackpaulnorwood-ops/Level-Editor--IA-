import java.awt.Rectangle;

public class Object {
    private Rectangle rect;
    private String imageID;

    public Object(Rectangle rect, String imageID) {
        this.rect=rect;
        this.imageID=imageID;
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
    
    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
