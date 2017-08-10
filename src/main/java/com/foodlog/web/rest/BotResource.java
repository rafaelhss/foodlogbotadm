package com.foodlog.web.rest;

import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.Weight;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.repository.WeightRepository;
import com.foodlog.service.MealLogService;
import com.foodlog.web.rest.bot.MealLogFactory;
import com.foodlog.web.rest.bot.model.Update;
import com.foodlog.web.rest.bot.sender.Sender;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
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

    private static final String BOT_ID = "380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM";

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
            } else {
                    processPhoto(update, user_id);
            }

            receivedMessages.put(update.getUpdate_id(),update.getUpdate_id());
            request.getSession().setAttribute("receivedMessages", receivedMessages);

        } else {
            System.out.println("mensagem Repetida: " + update.getUpdate_id() + " " + update.getMessage().getDate());
        }
    }

    private void processTimeline(Update update, int user_id) {
        try {
            new Sender(BOT_ID).sendResponse(user_id, "Sua timeline sera gerada...");

            //chama o image report para mandar o peso
            HttpURLConnection conn = (HttpURLConnection) new URL("https://foodlogbotimagebatch.herokuapp.com/timeline").openConnection();
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
            e.printStackTrace();
            return false;
        }
    }

    private void processWeight(Update update, int user_id) {
        Weight weight = new Weight();
        Float value = Float.parseFloat(update.getMessage().getText());
        weight.setValue(value);
        weight.setWeightDateTime(Instant.now());
        weight.setUpdateId(update.getUpdate_id());

        weightRepository.save(weight);

        List<Weight> weights = weightRepository.findTop15ByOrderByWeightDateTimeDesc();


        String message = "Peso (" + value + ") salvo com sucesso.";
/*
        for(Weight w : weights){
            message += System.lineSeparator() + w.getValue() + " - "  + w.getWeightDateTime() + System.lineSeparator();
        }
*/

        try {
            new Sender(BOT_ID).sendResponse(user_id, message);
            //chama o image report para mandar o peso
            HttpURLConnection conn = (HttpURLConnection) new URL("https://foodlogbotimagebatch.herokuapp.com/").openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForWeight(Update update) {
        String regex = "^[+-]?([0-9]*[.])?[0-9]+$";
        // Create a Pattern object
        Pattern r = Pattern.compile(regex);

        // Now create matcher object.
        if(update.getMessage().getText() != null) {
            Matcher m = r.matcher(update.getMessage().getText());
            return m.find();
        }
        return false;
    }

    private void processaProx(Update update, int user_id) {

        String message = "";

        ZonedDateTime nextTime = ZonedDateTime.now().plus(1,ChronoUnit.DAYS);
        ScheduledMeal next = null;

        try {
            for (ScheduledMeal scheduledMeal : scheduledMealRepository.findAll()) {
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

                MealLog mealLog = mealLogFactory.create(update);
                MealLog mealLog1 = mealLogRepository.save(mealLog);
                message = "Foto salva com sucesso, ";
                if (mealLog1.getScheduledMeal() == null) {
                    message += "sem classificação";
                } else {
                    message += "como " + mealLog1.getScheduledMeal().getName();
                }

                message += calculateMealIntervals();

            } else {
                System.out.println("nao veio foto");
                message = "Nenhuma foto encontrada na mensagem. Nada fiz...";
            }

            new Sender(BOT_ID).sendResponse(user_id, message);


        } catch (IOException | ConstraintViolationException ex) {
            Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String calculateMealIntervals() {


        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX: " + Instant.now().truncatedTo(ChronoUnit.DAYS));

        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")):" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")).toInstant():" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant());

        List<MealLog> mealLogs2Days = mealLogRepository.findByMealDateTimeAfterOrderByMealDateTimeDesc(now.truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));
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
            return ". Media de intervalo: " + hours + "h:"+ minutes + "m entre " + (int) ++count + " refeicoes. (Scheduled: " + calcScheduledAvgIntervals() +")";
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
