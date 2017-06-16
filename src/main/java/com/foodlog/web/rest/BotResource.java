package com.foodlog.web.rest;

import com.foodlog.domain.MealLog;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.service.MealLogService;
import com.foodlog.web.rest.bot.MealLogFactory;
import com.foodlog.web.rest.bot.model.Update;
import com.foodlog.web.rest.bot.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api")
public class BotResource {

    @Autowired
    private HttpServletRequest request;

    private Map<Long, Long> receivedMessages;// = new ArrayList<Long>();


    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private MealLogFactory mealLogFactory;

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

                    receivedMessages.put(update.getUpdate_id(),update.getUpdate_id());
                    request.getSession().setAttribute("receivedMessages", receivedMessages);

                } catch (IOException ex) {
                    Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
                }

        } else {
            System.out.println("mensagem Repetida: " + update.getUpdate_id() + " " + update.getMessage().getDate());
        }
    }

    private String calculateMealIntervals() {


        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX: " + Instant.now().truncatedTo(ChronoUnit.DAYS));

        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")):" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
        System.out.println("ZonedDateTime.now(ZoneId.of(\"America/Sao_Paulo\")).toInstant():" + ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant());

        List<MealLog>mealLogs = mealLogRepository.findByMealDateTimeAfterOrderByMealDateTimeDesc(now.truncatedTo(ChronoUnit.DAYS));
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        long minutesSum = 0;
        int count = 0;

        ZonedDateTime lastMealTime = null;
            for (MealLog mealLog: mealLogs){
                if(lastMealTime != null) {

                    ZonedDateTime brTime = mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo"));

                    long minutes = Duration.between(brTime, lastMealTime).getSeconds() / (60); //minutos
                    if (brTime.truncatedTo(ChronoUnit.DAYS).isBefore(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS))) { // passou um dia. ignora
                        minutesSum += minutes;
                        count++;
                    }
                    System.out.println(mealLog.getMealDateTime() + " ---> " + Duration.between(mealLog.getMealDateTime(), lastMealTime).getSeconds() / (60) + "  ignore:" +(brTime.truncatedTo(ChronoUnit.DAYS).isBefore(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS))));
                }

                lastMealTime =  mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo"));;
            }

            Long avg = minutesSum/mealLogs.size();

            System.out.println("meals:"  + mealLogs.size() + " sum:" + minutesSum + " avg:" + avg + " conta:" + minutesSum/mealLogs.size() + " cois:" + avg/60);

            if(mealLogs.size() > 1) {
                return ". Media de intervalo: " + avg / 60 + " horas entre " + count + " refeicoes";
            } else {
                return "";
            }


    }


}
