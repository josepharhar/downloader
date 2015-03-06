package downloads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static util.Utilities.*;
import static util.YoutubeUtil.*;

public class YoutubeVideo extends Download {

    // TEMPORARY main for testing youtube videos
    public static void main(String[] args) {

    }

    private List<Map<String, String>> adaptive_fmts;
    private List<Map<String, String>> url_encoded_fmt_stream_map;
    private String videoId;
    
    private Map<String, String> infoMap;
    private String videoInfo;

    public YoutubeVideo(String urlString) {
        super(urlString);
    }

    // Change URL to the youtube video, then download like normal
    public void initialize() throws Exception {
        setYoutubeURL();

        // initialize maps
        adaptive_fmts = new ArrayList<Map<String, String>>();
        url_encoded_fmt_stream_map = new ArrayList<Map<String, String>>();

        // Get "video info" string from youtube
        videoInfo = getVideoInfo(videoId);

        infoMap = getInfoMap(videoInfo);

        // Fix youtube blocking videos from certain providers
        //If the key "reason" is present, it will be blocked
        if (infoMap.get("reason") != null) {
            // get the video info again with proper override parameters
            videoInfo = getVideoInfo(videoId, infoMap.get("reason"), "embedded");
            infoMap = getInfoMap(videoInfo);
        }
        
        // Parse adaptive_fmts and url_encoded_fmt_stream_map into formats
        url_encoded_fmt_stream_map = parseFormatMap(infoMap.get("url_encoded_fmt_stream_map"));
        adaptive_fmts = parseFormatMap(infoMap.get("adaptive_fmts"));

        
        // Set the name of the video
//        String newName = infoMap.get("title");
//        newName = newName.replace("+", " ");
//        newName = Global.removeHex(newName);
//        setName(newName);

//        super.initialize();
    }

    // Changes URL from the youtube site to the actual download
    private void setYoutubeURL() {
        videoId = getVideoid(urlString);
    }

    // Finds a youtube video ID from the given input string
    // Looks for "?v=" and then returns the remaining 11 characters
    private String getVideoid(String input) {
        for (int i = 0; i < input.length() - 14; i++) {
            if (input.substring(i, i + 3).equals("?v=")) {
                return input.substring(i + 3, i + 14);
            }
        }
        // Couldn't find a video id in the string
        System.out.println("Couldn't find a video id in the string");
        return null;
    }

    public String getFileName() {
        return infoMap.get("name");
    }
}
