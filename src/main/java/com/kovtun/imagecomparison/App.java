package com.kovtun.imagecomparison;

import com.kovtun.imagecomparison.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App
{
    public static void main( String[] args ) throws IOException {
        try(InputStream inputStream = App.class.getResourceAsStream("/" + Constants.PROPERTIES_FILE_NAME)){
            Properties props = new Properties();
            props.load(inputStream);

            BufferedImage firstImage = loadImage(props.getProperty("firstImage"));
            BufferedImage secondImage = loadImage(props.getProperty("secondImage"));

            BufferedImage result = ImageComparator.compareImages(firstImage, secondImage);
            ImageIO.write(result, Constants.EXTENSION, new File(System.getProperty("user.dir") + "/" + Constants.RESULT_FILE_NAME + "." + Constants.EXTENSION));
        }
    }

    private static BufferedImage loadImage(String fileName){
        try(InputStream in = App.class.getResourceAsStream("/" + fileName)) {
            return ImageIO.read(in);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
