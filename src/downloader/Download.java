package downloader;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

/**
 *
 * @author Joseph Arhar
 */
public /*abstract*/ class Download extends Observable implements Runnable {

    // Enum for the different states of the download
    /*public static enum State {
        READY,
        DOWNLOADING,
        PAUSED,
        COMPLETE,
        CANCELLED,
        ERROR
    }*/
    
    public static final int READY = 0;
    public static final int DOWNLOADING = 1;
    public static final int PAUSED = 2;
    public static final int COMPLETE = 3;
    public static final int CANCELLED = 4;
    public static final int ERROR = 5;
    
    // Error message to be shown to the user when the download fails
    protected String errorMessage;
    
    // Number of bytes currently downloaded
    protected int downloaded;

    // total number of bytes
    protected int size;

    // State of the download (downloading, paused, etc)
    protected int state;
    
    // URL object of the download
    protected URL url;
    
    // String of the URL to be downloaded
    protected String urlString;
    
    // Connection object to connect to server
    protected HttpURLConnection connection;
    
    // Output file
    protected RandomAccessFile file;
    
    // Stream to get data from server
    protected InputStream stream;

    // maximum buffer size for the download
    // this number of bytes will be downloaded before it checks to see if
    // it should be paused or stop
    protected static final int MAX_BUFFER_SIZE = 1024;
    
    public Download(String urlString) {
        downloaded = 0;
        size = -1;
        this.urlString = urlString;
        errorMessage = "";
        try {
            initialize();
            state = READY;
        } catch (Exception e) {
            e.printStackTrace();
            state = ERROR;
        }
        stateChanged();
    }
    
    // Prepares download, gets response code
    public void initialize() throws Exception {

            //Instantiates URL
            url = new URL(urlString);
            
            // Connect to URL
            connection = (HttpURLConnection) url.openConnection();
            
            // Set range of bytes to request to the amount that hasn't been downloaded
            // This will be used when the download gets resumed from a pause
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            
            // Send request
            connection.connect();
            
            // Check response code
            if (connection.getResponseCode() / 100 != 2) {
                state = ERROR;
                errorMessage = "Bad response code: " + connection.getResponseCode() + ", " + connection.getResponseMessage();
                stateChanged();
            }
            
            // Check content length and set it to the size
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                state = Download.ERROR;
                errorMessage = "Bad content length: " + contentLength;
                stateChanged();
            }
            if (size == -1) {
                size = contentLength;
            }
            
            // Open File
            file = new RandomAccessFile("C:\\Downloader\\" + getFileName(), "rw");
            
            // Seek to the end of the file in order to write bytes to the end
            file.seek(downloaded);
            
            // Instantiates input stream
            stream = connection.getInputStream();            
        /*catch (Exception e) {
            state = Download.ERROR;
            e.printStackTrace();
            errorMessage = "Exception: " + e.getMessage();
            stateChanged();
        }*/
    }
    
    /**
     * @pre initialize has been run already
     */
    public void download() {
        if (state == READY)
            state = DOWNLOADING;
        try {
            while (state == DOWNLOADING) {
                // buffer[] sized according to how much of the file is left to download
                byte[] buffer;
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Download from the stream into the buffer
                int read = stream.read(buffer);
                if (read == -1) {
                    // read is the number of bytes read from the stream
                    // when it gets to the end, it returns -1 number of bytes
                    // and it is done
                    break;
                }

                // Write the buffer to the file
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }
            
            // Download is complete if it has gotten this far
            if (state == DOWNLOADING) {
                state = COMPLETE;
            }
        } catch (Exception e) {
            state = Download.ERROR;
            e.printStackTrace();
            errorMessage = "Exception: " + e.getMessage();
            stateChanged();
        }
    }
    
    public void start() {
        state = DOWNLOADING;
        stateChanged();
        download();
    }
    
    public void pause() {
        state = PAUSED;
        stateChanged();
    }
    
    public void resume() {
        state = DOWNLOADING;
        stateChanged();
        download();
    }
    
    public void cancel() {
        state = CANCELLED;
        stateChanged();
    }
    
    public void error() {
        state = Download.ERROR;
        stateChanged();
    }

    public int getProgress() {
        return (int)((float) downloaded / size * 100);
    }
    
    public int getState() {
        return state;
    }
    
    public String getStateName() {
        switch (state) {
            case READY:
                return "Ready";
            case DOWNLOADING:
                return "Downloading";
            case Download.ERROR:
                return "Error";
            case PAUSED:
                return "Paused";
            case COMPLETE:
                return "Complete";
            case CANCELLED:
                return "Cancelled";
            default:
                return "Unknown";
        }
    }
    
    public String getError() {
        return errorMessage;
    }
    
    public String getSize() {
        //int size is in bytes
        if (size / 1024 <= 0) {
            return size + " B";
        } else if (size / (1024 * 1024) <= 0) {
            return (size / 1024) + " KB";
        } else if (size / (1024 * 1024 * 1024) <= 0) {
            return (size / (1024 * 1024)) + " MB"; 
        } else {
            return (size / (1024 * 1024 * 1024)) + " GB";
        }
    }
    
    public String getFileName() {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
    
    public void run() {
        try {
            initialize();
            download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
