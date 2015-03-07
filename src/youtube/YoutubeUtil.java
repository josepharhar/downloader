package youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static util.Utilities.*;

public class YoutubeUtil {
    public static List<String> scanPageForYoutubeVideos(String url) {
        String page = null;
        try {
            page = downloadPage(url);
        } catch (IOException e) {
            System.out.println("unable to download page");
            e.printStackTrace();
        }

        return getVideoIds(page);
    }
    
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
        
        String output = downloadPage(request);

        return output;
    }
    
    // Used for url_encoded_fmt_stream_map and adaptive_fmts from youtube videoInfo
    public static List<Map<String, String>> parseFormatMap(String input) throws NumberFormatException {
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

    public static String getVideoId(String input) throws IllegalArgumentException {
        List<String> videoIds = getVideoIds(input);
        if (videoIds.size() < 1) {
            throw new IllegalArgumentException("Couldn't find a video id in the string: " + input);
        }
        
        return videoIds.get(0);
    }

    // Scans a string for videoIds and returns a list of them
    // Looks for "?v=" and then returns the remaining 11 characters
    // Removes duplicate Ids
    public static List<String> getVideoIds(String input) {
        // scan for ids
        List<String> output = new ArrayList<String>();
        for (int i = 0; i < input.length() - 13; i++) {
            if (input.substring(i, i + 3).equals("?v=")) {
                output.add(input.substring(i + 3, i + 14));
            }
        }
        
        // remove all used ids
        HashSet<String> usedIds = new HashSet<String>();
        Iterator<String> iter = output.iterator();
        while (iter.hasNext()) {
            String currentId = iter.next();
            if (usedIds.contains(currentId)) {
                iter.remove();
            } else {
                usedIds.add(currentId);
            }
        }
        
        return output;
    }
}
