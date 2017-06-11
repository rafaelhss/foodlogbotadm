package com.foodlog.web.rest.bot;


import com.foodlog.domain.MealLog;
import com.foodlog.web.rest.bot.model.GetFile;
import com.foodlog.web.rest.bot.model.Update;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Created by rafael on 04/06/17.
 */
public class MealLogFactory {
    private Update update;

    public MealLogFactory(Update update) {
        this.update = update;
    }

    public MealLog create() {
        MealLog mealLog = new MealLog();
        mealLog.setPhotoContentType("image/jpg");
        mealLog.setMealDateTime(Instant.now());
        mealLog.setComment((
                update.getMessage().getCaption() + " | " +
                        update.getMessage().getText()));
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
        byte[] imageBytes = restTemplate.getForObject(getBytesurl, byte[].class);


        mealLog.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imageBytes)));



            System.out.println("byte:" + imageBytes);
            try {
                Files.write(Paths.get("/home/rafael/Documents/Projects/foodlogbot/target/image.jpg"), imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }


        return mealLog;
    }
}
