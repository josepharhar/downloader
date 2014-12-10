package downloader;

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
        DownloaderGUI gui = new DownloaderGUI();
        gui.setVisible(true);
    }
    
}

