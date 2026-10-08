import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class ImageManager {

    private static HashMap<String, BufferedImage> images = new HashMap<>();

    public static void loadImage(String name, String path) {
        try {
            BufferedImage image = ImageIO.read(
                ImageManager.class.getResource(path)
            );

            images.put(name, image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage get(String name) {
        return images.get(name);
    }
}