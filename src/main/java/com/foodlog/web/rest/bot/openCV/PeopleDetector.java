package com.foodlog.web.rest.bot.openCV;

import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rafa on 07/09/17.
 */
public class PeopleDetector {
    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public byte[] getPeopleInPhoto(byte[] photo) {

        int absoluteFaceSize = 0;

        BufferedImage image = null;
        try {
            OpenCV.loadLibrary();



            image = ImageIO.read(new ByteArrayInputStream(photo));
            Mat frame = bufferedImageToMat(image);

            MatOfRect faces = new MatOfRect();
            Mat grayFrame = new Mat();

            // convert the frame in gray scale
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            // equalize the frame histogram to improve the result
            Imgproc.equalizeHist(grayFrame, grayFrame);

            // compute minimum face size (40% of the frame height, in our case)
            int height = grayFrame.rows();
            if (Math.round(height * 0.45f) > 0)
            {
                absoluteFaceSize = Math.round(height * 0.45f);
            }

            int count = 0;
            //Face frontal
            CascadeClassifier faceCascadeFace = getCascadeClassifier("config/haarcascade_frontalface_alt.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeFace);
            salvar(frame, faces, "1");
            if(count > 0){
                MatOfByte mob = new MatOfByte();
                Highgui.imencode(".jpg", frame ,mob);
                return mob.toArray();
            }

            //Corpo inteiro
            CascadeClassifier faceCascadeCorpo = getCascadeClassifier("config/haarcascade_fullbody.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeCorpo);
            salvar(frame, faces, "2");
            if(count > 0){
                MatOfByte mob = new MatOfByte();
                Highgui.imencode(".jpg", frame ,mob);
                return mob.toArray();
            }

            //upper body
            CascadeClassifier faceCascadeUpper = getCascadeClassifier("config/haarcascade_upperbody.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeUpper);
            salvar(frame, faces, "3");
            if(count > 0){
                MatOfByte mob = new MatOfByte();
                Highgui.imencode(".jpg", frame ,mob);
                return mob.toArray();
            }

            //perfil
            CascadeClassifier faceCascadeProfile = getCascadeClassifier("config/haarcascade_profileface.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeProfile);
            salvar(frame, faces, "4");
            if(count > 0){
                MatOfByte mob = new MatOfByte();
                Highgui.imencode(".jpg", frame ,mob);
                return mob.toArray();
            }


            return null;

        } catch (Throwable e) {
            System.out.println("ERro crazy: ");
            e.printStackTrace();
            return null;
        }
    }

    private void salvar(Mat frame, MatOfRect faces, String n) {

       try {
           // each rectangle in faces is a face
           Rect[] facesArray = faces.toArray();
           for (int i = 0; i < facesArray.length; i++)
               Core.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
           save(frame, "/home/rafa/Documents/Projects/foodlogbotadm/foodlogbotadm/target/teste" + n + ".jpg");
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    private int countPattern(int absoluteFaceSize, MatOfRect faces, Mat grayFrame, CascadeClassifier faceCascadeFace) {
        faceCascadeFace.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
            new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        System.out.println("faces.toArray().length: " + faces.toArray().length);
        return faces.toArray().length;
    }

    private CascadeClassifier getCascadeClassifier(String cascadeFile) throws IOException {
        CascadeClassifier faceCascade = new CascadeClassifier();


        ClassLoader cl = this.getClass().getClassLoader();
        InputStream initialStream = cl.getResourceAsStream(cascadeFile);


        System.out.println("stream null: " + (initialStream == null) + "    cascadeFile:" + cascadeFile);

        File targetFile = new File("targetFile.tmp");
        FileUtils.copyInputStreamToFile(initialStream, targetFile);

        boolean carregou = faceCascade.load(targetFile.getName());

        System.out.println("######s##### carregou 2 :  " + carregou);
        return faceCascade;
    }
    private void save(Mat mat, String name)
    {
        MatOfByte mob = new MatOfByte();
        //convert the matrix into a matrix of bytes appropriate for
        //this file extension
        Highgui.imencode(".jpg", mat ,mob);
        //convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            File outputfile = new File(name);
            ImageIO.write(bufImage, "jpg", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
