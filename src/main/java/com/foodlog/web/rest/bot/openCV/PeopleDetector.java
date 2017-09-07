package com.foodlog.web.rest.bot.openCV;

import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
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

    public int getPeopleInPhoto(byte[] photo) {

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

            // compute minimum face size (5% of the frame height, in our case)
            int height = grayFrame.rows();
            if (Math.round(height * 0.1f) > 0)
            {
                absoluteFaceSize = Math.round(height * 0.1f);
            }

            int count = 0;
            //Face frontal
            CascadeClassifier faceCascadeFace = getCascadeClassifier("config/haarcascade_frontalface_alt.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeFace);


            //Corpo inteiro
            CascadeClassifier faceCascadeCorpo = getCascadeClassifier("config/haarcascade_fullbody.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeCorpo);

            //upper body
            CascadeClassifier faceCascadeUpper = getCascadeClassifier("config/haarcascade_upperbody.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeUpper);

            //perfil
            CascadeClassifier faceCascadeProfile = getCascadeClassifier("config/haarcascade_profileface.xml");
            count += countPattern(absoluteFaceSize, faces, grayFrame, faceCascadeProfile);

            return count;
        } catch (Throwable e) {
            System.out.println("ERro crazy: ");
            e.printStackTrace();
            return -1;
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

}
