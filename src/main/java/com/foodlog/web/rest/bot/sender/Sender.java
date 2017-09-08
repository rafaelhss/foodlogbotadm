package com.foodlog.web.rest.bot.sender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Sender {

    private String botId;
    private String UrlTemplate      = "https://api.telegram.org/bot@@BOTID@@/sendmessage?chat_id=@@CHATID@@&text=@@TEXT@@";
    private String UrlImageTemplate = "https://api.telegram.org/bot@@BOTID@@/sendPhoto?chat_id=@@CHATID@@";


    public Sender(String botId){
        this.botId = botId;
        this.UrlTemplate = UrlTemplate.replace("@@BOTID@@", botId);
    }

    public void sendResponse(Integer id, String text_response) throws IOException {
        text_response = URLEncoder.encode(text_response, "UTF-8");
        URL url = new URL(UrlTemplate.replace("@@CHATID@@", id.toString()).replace("@@TEXT@@", text_response));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public void sendImage(Integer id, byte[] image){
        URL url = null;
        try {
            url = new URL(UrlTemplate.replace("@@CHATID@@", id.toString()));

            StringBuilder parameters = new StringBuilder();

            parameters.append("file1=");
            parameters.append(URLEncoder.encode(new String(image),"UTF-8"));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset","UTF-8");
            connection.setRequestProperty("Content-Length",Integer.toString(parameters.toString().getBytes().length));

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(parameters.toString());
            wr.flush();
            wr.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
