import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageManager {

    private static LinkedHashMap<String, BufferedImage> images = new LinkedHashMap<>();

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

    public static String uploadImage() {
        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (PNG, JPG, GIF)", "jpg", "jpeg", "png", "gif");

        chooser.setFileFilter(filter);

        int result = chooser.showOpenDialog(null);

        if(result!=JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();

        try {
            BufferedImage image = ImageIO.read(file);

            if(image==null) {
                return null;
            }

            String id = "user_" + java.util.UUID.randomUUID();

            images.put(id, image);

            return id;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage get(String name) {
        return images.get(name);
    }

    public static ArrayList<String> imageKeyList() {
        return new ArrayList<>(images.keySet());
    }
}