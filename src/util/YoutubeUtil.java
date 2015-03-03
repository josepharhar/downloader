package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Utilities.*;

public class YoutubeUtil {
    public static Map<String, String> getInfoMap(String videoInfo) {
        Map<String, String> output = new HashMap<String, String>();
        String[] splitVideoInfo = videoInfo.split("&");
        
        for (int i = 0; i < splitVideoInfo.length; i++) {
            String[] keyVal = splitVideoInfo[i].split("=");

            String key = keyVal[0];
            
            String value = null;
            if (keyVal.length < 2 || keyVal[1] == null) {
                value = "";
            } else {
                value = keyVal[1];
            }
            
            output.put(key, value);
        }
        
        return output;
    }
    
    public static String getVideoInfo(String videoId, String... args) throws IOException {
        String request = "http://www.youtube.com/get_video_info?&video_id=" + videoId;
        for (String arg : args) {
            request += "&el=" + arg;
        }
        String output = null;
        try {
            output = getVideoInfo(request);
        } catch (IOException e) {
            System.out.println("Failed to retrieve video info!");
            e.printStackTrace();
            throw e;
        }
        return output;
    }
    
    // Used for url_encoded_fmt_stream_map and adaptive_fmts from youtube videoInfo
    public static List<Map<String, String>> parseFormatMap(String input) {
        List<Map<String, String>> output = new ArrayList<Map<String, String>>();
        String[] videoStrings = parseHex(input).split(",");
        
        for (String videoString : videoStrings) {
            Map<String, String> videoMap = new HashMap<String, String>();
            
            String[] videoAttributes = videoString.split("&");
            
            for (String videoAttribute : videoAttributes) {
                String attribKey = parseHex(videoAttribute.split("=")[0]);
                String attribValue = parseHex(videoAttribute.split("=")[1]);
                videoMap.put(attribKey, attribValue);
            }
            output.add(videoMap);
        }
        return output;
    }
}
