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

    private static final String BOT_ID = "374481790:AAHgscpBDG2zs4VsDbeg140VmSVZZeItPEw";

    @RequestMapping(method= RequestMethod.POST, value="/update")
    public void ReceberUpdate(@RequestBody Update update){

        String mensagem = update.getMessage().getText();
        int user_id = update.getMessage().getFrom().getId();

        //testa se recebeu foto
        if(update.getMessage().getPhoto() != null && update.getMessage().getPhoto().size() > 0){
            System.out.println("fileid:" + update.getMessage().getPhoto().get(0).getFile_id());
            System.out.println("text:" + update.getMessage().getCaption());
            MealLog mealLog = new MealLogFactory(update).create();

            mealLogService.save(mealLog);

        } else {
            System.out.println("nao veio foto");
        }



        try {
            new Sender(BOT_ID).sendResponse(user_id, "response");
        } catch (IOException ex) {
            Logger.getLogger(BotResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
