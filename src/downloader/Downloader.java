package downloader;

import java.util.ArrayList;
import java.util.List;

import youtube.VideoId;
import youtube.YoutubeVideo;
import gui.DownloaderGUI;
import downloads.*;
import static youtube.YoutubeUtil.*;

/**
 * TODO Features
 * 
 * Settings:
 * Size of buffer (1024 bytes by default)
 * Number of simultaneous downloads (threads)
 *
 * Download individual files
 *
 * Download youtube videos
 *   - Download all links on page (playlists)
 *
 * Download links on html pages (with regular expressions)
 *
 * Recursive downloading (manga) with regular expressions
 * 
 * Download Twitch VODs
 * https://github.com/Kiskae/Twitch-Archiver
 */

/**
 * TODO
 * 
 * delete empty files when cancelled
 */

/**
 *
 * @author Joseph Arhar
 */
public class Downloader {
    
    public static void main(String[] args) {
        downloadPlaylist("https://www.youtube.com/playlist?list=PL02DC8EB49633BEAF");
//        DownloaderGUI gui = new DownloaderGUI();
//        gui.setVisible(true);
    }
    
    public static void downloadPlaylist(String url) {
        List<String> videoIds = scanPageForYoutubeVideos(url);
        List<YoutubeVideo> videos = new ArrayList<YoutubeVideo>();
        
        for (String id : videoIds) {
            videos.add(new YoutubeVideo(new VideoId(id)));
        }
        
        for (YoutubeVideo video : videos) {
            video.download();
        }
        
        System.out.println("done!");
    }
    
}

