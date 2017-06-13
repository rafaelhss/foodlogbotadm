package com.foodlog.web.rest.bot;


import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.web.rest.bot.model.GetFile;
import com.foodlog.web.rest.bot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
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

    public MealLog create(Update update) {
        MealLog mealLog = new MealLog();

        //Date = now
        mealLog.setMealDateTime(Instant.now());

        //Comment = caption
        String caption = update.getMessage().getCaption();
        if(caption != null) {
            mealLog.setComment((caption.trim()));
        }

        //Photo
        byte[] imageBytes = getPicture(update);
        mealLog.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imageBytes)));
        mealLog.setPhotoContentType("image/jpg");


        //ScheduledMeal
        ScheduledMeal scheduledMeal = defineScheduledMeal(mealLog);
        mealLog.setScheduledMeal(scheduledMeal);

        return mealLog;
    }

    private ScheduledMeal defineScheduledMeal(MealLog mealLog) {
        try {

            if(mealLog.getComment() != null && !mealLog.getComment().isEmpty()){
                List<ScheduledMeal> scheduledMeals = scheduledMealRepository.findByName(mealLog.getComment());
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

        System.out.println("datas:");
        System.out.println(now);
        System.out.println(target);
        System.out.println(before);
        System.out.println(after);

        return (now.isBefore(after) && now.isAfter(before));
    }


    private byte[] getPicture(Update update) {
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
}
