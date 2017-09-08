package com.foodlog.web.rest.bot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.aspectj.bridge.ISourceLocation.MAX_LINE;

/**
 * Created by rafa on 26/08/17.
 */
public class Util {
    private static final int MAX_LINE_LENGHT = 50;
//    public static void main(String[] args) throws Exception {
//        //BufferedImage image = new Util().convertTextToGraphic("Arroz, feijão e carne", new Font("Arial", Font.PLAIN, 32));
//        //write BufferedImage to file
//        //ImageIO.write(image, "png", new File("/home/rafa/Documents/Projects/foodlogbotadm/foodlogbotadm/path-to-file.png"));
//    }

    public byte[] convertTextToGraphic(String text, Font font) {

        int max = MAX_LINE_LENGHT;
        if(text.length() < MAX_LINE_LENGHT)
            max = text.length();
        String finaltext = text.substring(0, max);
        text = text.substring(max);
        while(text != null && !text.trim().equals("")){
            finaltext += "\n" + text;

            if(text.length() < MAX_LINE_LENGHT)
                max = text.length();
            text = text.substring(max);
        }


        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(finaltext);
        if(finaltext.length() > MAX_LINE_LENGHT){
            width = fm.stringWidth(finaltext.split("\n")[0]);
        }
        int height = fm.getHeight()*5;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        //g2d.drawString(text, 0, fm.getAscent());

        int y = 0;
        for (String line : finaltext.split("\n"))
            g2d.drawString(line, 0, y += g2d.getFontMetrics().getHeight());

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos );
            baos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
