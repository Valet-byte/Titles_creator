package com.valet.image_micro.model;

import lombok.Data;
import lombok.SneakyThrows;
import org.modelmapper.internal.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@Data
public class FrameMaker implements Runnable{

     private double posStart, posEnd;
     private int speed;
     private int distance;
     private Font font;
     private Color color;
     private Color backgroundColor;
     private List<List<Pair<Double, String>>> text;
     private String imgDir;
     private BufferedImage bufferedImage;
    private static final double x = 1920, y = 1080;

    private volatile boolean isEnd;

    @SneakyThrows
    @Override
    public void run() {
        isEnd = false;
        Graphics2D g;
        double pos = posStart;
        double step = x / (text.size() + 1);

        while (pos < posEnd){
            g = bufferedImage.createGraphics();
            g.setPaint(backgroundColor);
            g.fillRect(0, 0, (int) x, (int) y);
            g.setFont(font);
            g.setPaint(color);

            for (int i = 0; i < text.size(); i++) {
                for (int i1 = 0; i1 < text.get(i).size(); i1++) {
                    g.drawString(text.get(i).get(i1).getRight(), (int) ( (x -  (step * (i + 1))) - (text.get(i).get(i1).getLeft())), (int) ((pos) + (i1 * distance) ));
                }

            }

            File outputfile = new File(imgDir + ((int) (pos))  + ".jpg");

            ImageIO.write(bufferedImage, "jpg", outputfile);
            bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
            pos += speed;
        }
        isEnd = true;
    }
}
