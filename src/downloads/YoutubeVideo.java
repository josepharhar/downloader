package downloads;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static util.YoutubeUtil.*;
import static util.Utilities.*;

public class YoutubeVideo extends Download {

    // TEMPORARY main for testing youtube videos
    public static void main(String[] args) {
        YoutubeVideo video = new YoutubeVideo("https://www.youtube.com/watch?v=pXufDCOT9TE");
        try {
//            video.initialize();
            video.download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, String>> adaptive_fmts;
    private List<Map<String, String>> url_encoded_fmt_stream_map;
    private String videoId;
    
    private Map<String, String> infoMap;
    private String videoInfo;
    
    private String fileName;

    public YoutubeVideo(String urlString) {
        super(urlString);
    }

    // Change URL to the youtube video, then download like normal
    public void initialize() throws Exception {
        videoId = getVideoId(urlString);

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
        try {
            url_encoded_fmt_stream_map = parseFormatMap(infoMap.get("url_encoded_fmt_stream_map"));
            adaptive_fmts = parseFormatMap(infoMap.get("adaptive_fmts"));
        } catch (NumberFormatException e) {
            System.out.println("unable to parse format map");
            e.printStackTrace();
            throw e;
        }
        
        // Set the name of the video
        String newName = infoMap.get("title");
        newName = newName.replace("+", " ");
        newName = parseHex(newName);
        fileName = newName + ".mp4";

        
        // Change urlString to what is actually going to be downloaded
        // For now, we will use encoded.22, then adaptive.140, then encoded.18
        List<String> desiredFormats = new ArrayList<String>();
        desiredFormats.add("22");
        desiredFormats.add("140");
        desiredFormats.add("18");
        
        Map<String, String> formatMap = null;
        for (int i = 0; i < desiredFormats.size() && formatMap == null; i++) {
            formatMap = searchForItag(desiredFormats.get(i));
        }
        if (formatMap == null) {
            throw new Exception("Unable to find a desired format");
        }
        
        // Set urlString to the actual video link to download
        urlString = formatMap.get("url");

        super.initialize();
    }
    
    private Map<String, String> searchForItag(String itag) {
        for (Map<String, String> map : url_encoded_fmt_stream_map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (map.get("itag").equals(itag)) {
                    return map;
                }
            }
        }
        for (Map<String, String> map : adaptive_fmts) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (map.get("itag").equals(itag)) {
                    return map;
                }
            }
        }
        return null;
    }
    
    public String getFileName() {
        return fileName;
    }
}
