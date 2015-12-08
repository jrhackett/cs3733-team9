package logic;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ikeandmike on 12/4/15.
 */

//This class contains static methods for fetching objects from the filesystem
public class FileFetch {

    /**
     *Path is from inside the images directory, with no leading slash
     *ie. pass "floorplans/image.png" not "/floorplans/image.png"
     */

    public static Image getImageFromFile(String path) {
        InputStream img = makeStream(path);
        return new Image(img);
    }

    public static Image getImageForPictures(String path, double w, double h, boolean ratio, boolean smooth) {
        InputStream img = makeStreamforPictures(path);
        return new Image(img, w, h, ratio, smooth);
    }

    public static Image getImageFromFile(String path, double w, double h, boolean ratio, boolean smooth) {
        InputStream img = makeStream(path);
        return new Image(img, w, h, ratio, smooth);
    }

    //Helper
    private static InputStream makeStream(String path) {
        String fp = new File("").getAbsolutePath() + "/WPI-Nav/src/images/" + path;
        InputStream img;
        try { img = new FileInputStream(fp); }
        catch (FileNotFoundException e) {
            try {
                fp = new File("").getAbsolutePath() + "/src/images/" + path;
                img = new FileInputStream(fp);
            } catch (FileNotFoundException f) {
                f.printStackTrace();
                return null;
            }
        }
        return img;
    }

    private static InputStream makeStreamforPictures(String path) {
        String fp = new File("").getAbsolutePath() + "/WPI-Nav/src/images/pictures/" + path;
        InputStream img;
        try { img = new FileInputStream(fp); }
        catch (FileNotFoundException e) {
            try {
                fp = new File("").getAbsolutePath() + "/src/images/" + path;
                img = new FileInputStream(fp);
            } catch (FileNotFoundException f) {
                f.printStackTrace();
                return null;
            }
        }
        return img;
    }


    private static InputStream makeStream(String root, String path) {
        String fp = new File("").getAbsolutePath() + root + path;
        InputStream img;
        try { img = new FileInputStream(fp); }
        catch (FileNotFoundException e) { e.printStackTrace(); return null; } //BAD
        return img;
    }
}
