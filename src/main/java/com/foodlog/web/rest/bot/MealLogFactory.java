package com.foodlog.web.rest.bot;


import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.User;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.repository.UserRepository;
import com.foodlog.repository.UserTelegramRepository;
import com.foodlog.security.DomainUserDetailsService;
import com.foodlog.security.SecurityUtils;
import com.foodlog.web.rest.bot.model.GetFile;
import com.foodlog.web.rest.bot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by rafael on 04/06/17.
 */
@Service
public class MealLogFactory {

    @Autowired
    ScheduledMealRepository scheduledMealRepository;

    @Autowired
    UserTelegramRepository userTelegramRepository;

    private MealLog getBaseMealLog(Update update, User current){
        MealLog mealLog = new MealLog();

        //Date = now
        mealLog.setMealDateTime(Instant.now());

        //Comment = caption
        String caption = update.getMessage().getCaption();
        if(caption != null) {
            mealLog.setComment((caption.trim()));
        }

        //ScheduledMeal
        ScheduledMeal scheduledMeal = defineScheduledMeal(mealLog, current);
        mealLog.setScheduledMeal(scheduledMeal);

        mealLog.setUpdateId(update.getUpdate_id());


        mealLog.setUser(current);

        return mealLog;
    }

    public MealLog create(Update update, User current) {

        MealLog mealLog = getBaseMealLog(update, current);

        //Photo
        byte[] imageBytes = getPicture(update);
        mealLog.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imageBytes)));
        mealLog.setPhotoContentType("image/jpg");

        return mealLog;
    }

    private ScheduledMeal defineScheduledMeal(MealLog mealLog, User currentUser) {
        try {

            if(mealLog.getComment() != null && !mealLog.getComment().isEmpty()){
                List<ScheduledMeal> scheduledMeals = scheduledMealRepository.findByNameAndUser(mealLog.getComment(), currentUser);
                if(scheduledMeals != null && !scheduledMeals.isEmpty()){
                    return scheduledMeals.get(0);
                }
            }


            for (ScheduledMeal scheduledMeal : scheduledMealRepository.findAll()) {
                if(checkTime(scheduledMeal)) {
                    return scheduledMeal;
                }
            }
        } catch (Exception ex){
            System.out.println("errrxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private boolean checkTime(ScheduledMeal scheduledMeal)  {
        String time[] = scheduledMeal.getTargetTime().split(":");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        ZonedDateTime target = now.with(LocalTime.of(hour, minute));

        ZonedDateTime after = target.plusMinutes(30);
        ZonedDateTime before = target.minusMinutes(30);

        return (now.isBefore(after) && now.isAfter(before));
    }


    public byte[] getPicture(Update update) {
        int id = update.getMessage().getPhoto().size() -1 ;
        String file_id = update.getMessage().getPhoto().get(id).getFile_id();

        RestTemplate restTemplate = new RestTemplate();
        URI getFileurl = ApiUrlBuilder.getGetFile(file_id);
        System.out.println("url:" + getFileurl.toString());
        GetFile getFile = (GetFile) restTemplate.getForObject(getFileurl, GetFile.class);


        System.out.println("result:" + getFile.getResult().getFile_path());

        //https://api.telegram.org/file/bot<token>/<file_path>

        String file_path = getFile.getResult().getFile_path();
        URI getBytesurl = ApiUrlBuilder.getBytesUrl(file_path);
        return restTemplate.getForObject(getBytesurl, byte[].class);
    }

    public MealLog createTextLog(Update update, User current) {
        MealLog mealLog = getBaseMealLog(update, current);

        String texto = update.getMessage().getText();
        if(texto.toLowerCase().indexOf("meal:") == 0){
            texto = texto.substring("meal:".length());
        }

        //Photo
        byte[] imageBytes = new Util().convertTextToGraphic(texto, new Font("Arial", Font.PLAIN, 14));

        mealLog.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imageBytes)));
        mealLog.setPhotoContentType("image/jpg");

        return mealLog;

    }
}
