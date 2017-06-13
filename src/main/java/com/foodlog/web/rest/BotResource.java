package com.foodlog.web.rest;

import com.foodlog.domain.MealLog;
import com.foodlog.service.MealLogService;
import com.foodlog.web.rest.bot.MealLogFactory;
import com.foodlog.web.rest.bot.model.Update;
import com.foodlog.web.rest.bot.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api")
public class BotResource {

    @Autowired
    private MealLogService mealLogService;

    private static final String BOT_ID = "380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM";

    @RequestMapping(method= RequestMethod.POST, value="/update")
    public void ReceberUpdate(@RequestBody Update update){

        String mensagem = update.getMessage().getText();
        int user_id = update.getMessage().getFrom().getId();

        String message = "Algum erro aconteceu...";

        //testa se recebeu foto
        if(update.getMessage().getPhoto() != null && update.getMessage().getPhoto().size() > 0){

            MealLog mealLog = new MealLogFactory(update).create();
            MealLog mealLog1 = mealLogService.save(mealLog);
            message = "Foto salva com sucesso. Meallog (" + mealLog1.getId() + ") registrado ";
            if(mealLog1.getScheduledMeal() == null) {
                message += "sem classificação";
            } else {
                message += "como " + mealLog1.getScheduledMeal().getName();
            }
        } else {
            System.out.println("nao veio foto");
            message = "Nenhuma foto encontrada na mensagem. Nada fiz...";
        }

        try {
            new Sender(BOT_ID).sendResponse(user_id, message);
        } catch (IOException ex) {
            Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
