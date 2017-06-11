package com.foodlog.web.rest.bot;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by rafael on 04/06/17.
 */
public class ApiUrlBuilder {

    private static String bot_id = "380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM";
    private static String base_url = "https://api.telegram.org/bot@@BOTID@@/getFile?file_id=@@FILEID@@";

    private static String base_file_url = "https://api.telegram.org/file/bot@@BOTID@@/@@FILEPATH@@";

    public static URI getGetFile(String file_id) {
        try {
            return new URI(base_url.replace("@@BOTID@@", bot_id).replace("@@FILEID@@", file_id));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URI getBytesUrl(String file_path) {
        try {
            return new URI(base_file_url.replace("@@BOTID@@", bot_id).replace("@@FILEPATH@@", file_path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    //

}
