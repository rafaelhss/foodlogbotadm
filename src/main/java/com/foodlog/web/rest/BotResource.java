package com.foodlog.web.rest;

import com.foodlog.domain.*;
import com.foodlog.emoji.string.*;
import com.foodlog.emoji.string.Objects;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.repository.UserTelegramRepository;
import com.foodlog.repository.WeightRepository;
import com.foodlog.web.rest.bot.MealLogFactory;
import com.foodlog.web.rest.bot.model.Update;
import com.foodlog.web.rest.bot.sender.Sender;
import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class BotResource {

    @Autowired
    private HttpServletRequest request;

    private Map<Long, Long> receivedMessages;// = new ArrayList<Long>();


    @Autowired
    ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private MealLogFactory mealLogFactory;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    private static final String BOT_ID = "380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM";

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {

        try {
            int absoluteFaceSize = 0;

            OpenCV.loadLibrary();
            Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
            System.out.println("mat = " + mat.dump());

            CascadeClassifier faceCascade = new CascadeClassifier();
            //String classifierPath = new ClassPathResource("haarcascade_frontalface_alt.xml").getFile().getCanonicalPath();

            //File source = new File(ClassLoader.getSystemResource("config/haarcascade_frontalface_alt.xml").getPath());

           // File source = new File(this.getClass().getClassLoader().getResource("config/haarcascade_frontalface_alt.xml").getPath());


            //String folder = source.getParent().substring(source.getParent().lastIndexOf("\\")+1);

            //System.out.println("folder: " + folder);

            ClassLoader cl = this.getClass().getClassLoader();
            InputStream initialStream = cl.getResourceAsStream("config/haarcascade_frontalface_alt.xml");


            System.out.println("stream null: " + (initialStream == null));

            File targetFile = new File("targetFile.tmp");
            FileUtils.copyInputStreamToFile(initialStream, targetFile);

            boolean carregou = faceCascade.load(targetFile.getName());
            System.out.println("########### carregou: " + carregou);


            BufferedImage image = ImageIO.read(cl.getResourceAsStream("teste.jpg"));


            Mat frame = bufferedImageToMat(image);


            MatOfRect faces = new MatOfRect();
            Mat grayFrame = new Mat();

            // convert the frame in gray scale
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            // equalize the frame histogram to improve the result
            Imgproc.equalizeHist(grayFrame, grayFrame);

            // compute minimum face size (20% of the frame height, in our case)
            if (absoluteFaceSize == 0) {
                int height = grayFrame.rows();
                if (Math.round(height * 0.02f) > 0) {
                    absoluteFaceSize = Math.round(height * 0.02f);
                }
            }

            // detect faces
            faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize, absoluteFaceSize), new Size());


            return "size:" + faces.toArray().length;
        } catch (Throwable e){
            System.out.println("bad");
            e.printStackTrace();
            return e.getMessage();
        }
    }


    @RequestMapping(method= RequestMethod.POST, value="/update")
    public void ReceberUpdate(@RequestBody Update update){

        receivedMessages = (Map<Long, Long>) request.getSession().getAttribute("receivedMessages");
        if(receivedMessages == null){
            receivedMessages = new HashMap<>();
        }

        if(receivedMessages.get(update.getUpdate_id()) == null) {

            int user_id = update.getMessage().getFrom().getId();

            String message = "Algum erro aconteceu...";


            if(update.getMessage().getText() != null && update.getMessage().getText().trim().toLowerCase().equals("prox")){
                processaProx(update, user_id);
            } else if(checkForWeight(update)) {
                processWeight(update, user_id);
            } else if(checkForTimeline(update)) {
                processTimeline(update, user_id);
            } else if(checkForTextLog(update)){
                processTextLog(update, user_id);
            } else {
                processPhoto(update, user_id);
            }

            receivedMessages.put(update.getUpdate_id(),update.getUpdate_id());
            request.getSession().setAttribute("receivedMessages", receivedMessages);

        } else {
            System.out.println("mensagem Repetida: " + update.getUpdate_id() + " " + update.getMessage().getDate());
        }
    }

    private void processTextLog(Update update, int user_id) {

        MealLog mealLog = mealLogFactory.createTextLog(update, getCurrentUser(update));

        String message = saveMealLogAndGenerateMessage(update, mealLog);
        try {
            new Sender(BOT_ID).sendResponse(user_id, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTimeline(Update update, int user_id) {
        try {
            new Sender(BOT_ID).sendResponse(user_id, "Sua timeline sera gerada...");

            //chama o image report para mandar o peso
            HttpURLConnection conn = (HttpURLConnection) new URL("https://foodlogbotimagebatch.herokuapp.com/timeline?userid=" + getCurrentUser(update).getId()).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkForTimeline(Update update) {
        try {
            return update.getMessage().getText().trim().toLowerCase().equals("timeline");
        } catch (Exception e) {
            System.out.println("Erro ao tentar achar a palavra timeline. Segue o jogo:  " + e.getMessage());
            return false;
        }
    }

    private void processWeight(Update update, int user_id) {
        Weight weight = new Weight();
        Float value = Float.parseFloat(update.getMessage().getText());
        weight.setValue(value);
        weight.setWeightDateTime(Instant.now());
        weight.setUpdateId(update.getUpdate_id());

        User currentUser = getCurrentUser(update);
        weight.setUser(currentUser);

        weightRepository.save(weight);



        String message = "Peso (" + value + ") salvo com sucesso.";
/*
        List<Weight> weights = weightRepository.findTop15ByUserOrderByWeightDateTimeDesc(getCurrentUser(update));

        for(Weight w : weights){
            message += System.lineSeparator() + w.getValue() + " - "  + w.getWeightDateTime() + System.lineSeparator();
        }
*/

        try {
            new Sender(BOT_ID).sendResponse(user_id, message);
            //chama o image report para mandar o peso
            HttpURLConnection conn = (HttpURLConnection) new URL("https://foodlogbotimagebatch.herokuapp.com/?userid=" + currentUser.getId()).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkRegex(Update update, String regex){
        // Create a Pattern object
        Pattern r = Pattern.compile(regex);

        // Now create matcher object.
        if(update.getMessage().getText() != null) {
            Matcher m = r.matcher(update.getMessage().getText());
            return m.find();
        }
        return false;
    }
    private boolean checkForWeight(Update update) {
        return checkRegex(update, "^[+-]?([0-9]*[.])?[0-9]+$");
    }

    private boolean checkForTextLog(Update update) {
        //return checkRegex(update, "^([0-1]\\d|2[0-3]):([0-5]\\d).*");  //verifica xx:xx no comeco
        try {
            return update.getMessage().getText().toLowerCase().indexOf("meal:") == 0;
        } catch (Exception e){
            return false;
        }

    }

    private void processaProx(Update update, int user_id) {

        String message = "";

        ZonedDateTime nextTime = ZonedDateTime.now().plus(1,ChronoUnit.DAYS);
        ScheduledMeal next = null;

        try {
            User current = getCurrentUser(update);
            for (ScheduledMeal scheduledMeal : scheduledMealRepository.findByUser(current)) {
                if(getZonedTargetTime(scheduledMeal).isAfter(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    && getZonedTargetTime(scheduledMeal).isBefore(nextTime)) {
                    next = scheduledMeal;
                    nextTime = getZonedTargetTime(scheduledMeal);
                }
            }

            if (next == null) {
                message = "Não achei a proxima.";
            } else {
                message = "Sua proxima refeição agendada: " + next.getName() + "(" + next.getTargetTime() + ") " + next.getDescription();
            }

            new Sender(BOT_ID).sendResponse(user_id, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User getCurrentUser(Update update) {
        return userTelegramRepository.findOneByTelegramId(update.getMessage().getFrom().getId()).getUser();
    }

    private ZonedDateTime getZonedTargetTime(ScheduledMeal scheduledMeal) {
        String time[] = scheduledMeal.getTargetTime().split(":");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        return now.with(LocalTime.of(hour, minute));

    }


    private void processPhoto(@RequestBody Update update, int user_id) {
        String message;
        try {
            //testa se recebeu foto
            if (update.getMessage().getPhoto() != null && update.getMessage().getPhoto().size() > 0) {

                if(getPeopleInPhoto(update) > 0){
                    message = "achei " + getPeopleInPhoto(update) + " pessoas na foto. Nada fiz ainda";
                } else {

                    MealLog mealLog = mealLogFactory.create(update, getCurrentUser(update));
                    message = saveMealLogAndGenerateMessage(update, mealLog);

                    message += "   (getPeopleInPhoto(update):" + getPeopleInPhoto(update);
                }

            } else {
                System.out.println("nao veio foto");
                message = "Nenhuma foto encontrada na mensagem. Nada fiz...";
            }

            new Sender(BOT_ID).sendResponse(user_id, message);


        } catch (IOException | ConstraintViolationException ex) {
            Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
    private int getPeopleInPhoto(Update update) {

        int absoluteFaceSize = 0;


        byte[] photo = new MealLogFactory().getPicture(update);


        BufferedImage image = null;
        try {
            OpenCV.loadLibrary();

            CascadeClassifier faceCascade = new CascadeClassifier();
            //String classifierPath = new ClassPathResource("haarcascade_frontalface_alt.xml").getFile().getCanonicalPath();
            //String classifierPath = this.getClass().getClassLoader().getResource("haarcascade_frontalface_alt.xml").getPath();


            //File source = new File(this.getClass().getClassLoader().getResource("com/foodlog/web/rest/haarcascade_frontalface_alt.xml").getPath());
            File source = new File(ClassLoader.getSystemResource("config/haarcascade_frontalface_alt.xml").getPath());


            //String folder = source.getParent().substring(source.getParent().lastIndexOf("\\")+1);

            //System.out.println("folder: " + folder);


            ClassLoader cl = this.getClass().getClassLoader();
            InputStream initialStream = cl.getResourceAsStream("config/haarcascade_frontalface_alt.xml");


            System.out.println("stream null: " + (initialStream == null));

            File targetFile = new File("targetFile.tmp");
            FileUtils.copyInputStreamToFile(initialStream, targetFile);

            boolean carregou = faceCascade.load(targetFile.getName());

            System.out.println("######s##### carregou 2 :  " + carregou);



            image = ImageIO.read(new ByteArrayInputStream(photo));


            Mat frame = bufferedImageToMat(image);




            MatOfRect faces = new MatOfRect();
            Mat grayFrame = new Mat();

            // convert the frame in gray scale
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            // equalize the frame histogram to improve the result
            Imgproc.equalizeHist(grayFrame, grayFrame);

            // compute minimum face size (20% of the frame height, in our case)
            if (absoluteFaceSize == 0)
            {
                int height = grayFrame.rows();
                if (Math.round(height * 0.02f) > 0)
                {
                    absoluteFaceSize = Math.round(height * 0.02f);
                }
            }

            // detect faces
            faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize, absoluteFaceSize), new Size());

            return faces.toArray().length;
        } catch (Throwable e) {
            System.out.println("ERro crazy: ");
            e.printStackTrace();
            return -1;
        }
    }

    private String saveMealLogAndGenerateMessage(@RequestBody Update update, MealLog mealLog) {
        String message;
        MealLog mealLog1 = mealLogRepository.save(mealLog);
        message = People.BLACK_SMILING_FACE.toString();
        if (mealLog1.getScheduledMeal() == null) {
            message += People.THUMBS_UP.toString();
        } else {
            message += People.RIGHT_POINTING_BACKHAND_INDEX.toString()
                + " "
                + mealLog1.getScheduledMeal().getName();
        }

        message += calculateMealIntervals(getCurrentUser(update));
        return message;
    }

    private String calculateMealIntervals(User currentUser) {


        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX: " + Instant.now().truncatedTo(ChronoUnit.DAYS));

        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")):" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")).toInstant():" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant());

        List<MealLog> mealLogs2Days = mealLogRepository.findByUserAndMealDateTimeAfterOrderByMealDateTimeDesc(currentUser, now.truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        float secondsSum = 0;
        float count = 0;

        Instant lastMealTime = null;
        for (MealLog mealLog: mealLogs2Days){
            if(lastMealTime != null) {

                //ZonedDateTime brTime = mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo"));
                Instant current = mealLog.getMealDateTime();

                float seconds = Duration.between(current, lastMealTime).getSeconds();

                //if (!brTime.truncatedTo(ChronoUnit.DAYS).isBefore(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS))) { // passou um dia. ignora
                if(seconds > (60 * 60 * 5)) { //se for mais que 5 horas chegou no dia anterior. ai para
                    break;
                } else {
                    secondsSum += seconds;
                    count += 1F;
                }
                System.out.println(mealLog.getId() + "----> " + mealLog.getMealDateTime() + " ---> " + Duration.between(mealLog.getMealDateTime(), lastMealTime).getSeconds());
            }

            lastMealTime =  mealLog.getMealDateTime();
        }

        float avgSeconds = (secondsSum/count);


        float milliseconds = avgSeconds * 1000;

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        System.out.println("meals:"  + count+ " sum:" + secondsSum + " avg:" + avgSeconds);
        System.out.println("minuto:" + minutes);
        System.out.println("hours:" + hours);

        if(avgSeconds > 1) {
            return "\n" + Objects.STOP_WATCH.toString()
                + hours + "h:"+ minutes + "m ["
                + (int) ++count + "/" + scheduledMealRepository.findByUser(currentUser).size() + "] (" + Objects.ALARM_CLOCK.toString() //TODO arrumar
                + calcScheduledAvgIntervals() +")";
        } else {
            return "";
        }


    }

    private String calcScheduledAvgIntervals() {

        ZonedDateTime lastMealTime = null;
        float secondsSum = 0;
        float count = 0;

        for(ScheduledMeal scheduledMeal:scheduledMealRepository.findByOrderByTargetTimeDesc()){
            ZonedDateTime current = getZonedTargetTime(scheduledMeal);
            if(lastMealTime != null && !current.equals(lastMealTime)) {
                float seconds = Duration.between(current, lastMealTime).getSeconds();
                secondsSum += seconds;
                count += 1F;
            }
            lastMealTime = current;
        }
        float avgSeconds = (secondsSum/count);


        float milliseconds = avgSeconds * 1000;

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);


        return hours + "h:"+ minutes + "m";

    }


}
