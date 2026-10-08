import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Object {
    private Rectangle rect;
    private BufferedImage image;

    public Object(Rectangle rect, BufferedImage image) {
        this.rect=rect;
        this.image=image;
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

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
