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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api")
public class BotResource {

    @Autowired
    private HttpServletRequest request;

    private List<Long> receivedMessages;// = new ArrayList<Long>();


    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private MealLogFactory mealLogFactory;

    private static final String BOT_ID = "380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM";

    @RequestMapping(method= RequestMethod.POST, value="/update")
    public void ReceberUpdate(@RequestBody Update update){

        receivedMessages = (List<Long>) request.getSession().getAttribute("receivedMessages");
        if(receivedMessages == null){
            receivedMessages = new ArrayList<>();
        }

        String mensagem = update.getMessage().getText();
        int user_id = update.getMessage().getFrom().getId();

        String message = "Algum erro aconteceu...";

        if(!checkDuplicatedMessage(update.getUpdate_id())) {

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

                receivedMessages.add(update.getUpdate_id());
                request.getSession().setAttribute("receivedMessages", receivedMessages);

            } catch (IOException ex) {
                Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String calculateMealIntervals() {


            List<MealLog>mealLogs = mealLogRepository.findByMealDateTimeAfterOrderByMealDateTimeDesc(Instant.now().truncatedTo(ChronoUnit.DAYS));

            long minutesSum = 0;

            Instant lastMealTime = null;
            for (MealLog mealLog: mealLogs){
                if(lastMealTime != null) {
                    minutesSum += Duration.between(mealLog.getMealDateTime(), lastMealTime).getSeconds() / (60); //minutos
                }
                lastMealTime = mealLog.getMealDateTime();
            }

            Long avg = minutesSum/mealLogs.size();

            System.out.println("meals:"  + mealLogs.size() + " sum:" + minutesSum);

            if(mealLogs.size() > 1) {
                return " media de intervalo entre refeicoes: " + avg / 60 + " horas";
            } else {
                return "";
            }


    }

    private boolean checkDuplicatedMessage(Long update_id) {
        for (Long id : receivedMessages) {
            if(id == update_id){
                return true;
            }
        }
        return false;
    }
}
