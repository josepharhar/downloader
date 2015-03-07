package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.stream.Stream;

public class Utilities {
    
    // TESTING
    public static void main(String[] args) {
//        System.out.println(parseHex("%3D"));
        
//        long asdf = Long.parseLong("3D", );
//        System.out.println(asdf);
        
//        try {
//            downloadFile("http://arhar.net/arharnet.jpg", "c:\\users\\joseph\\desktop\\arharnet.jpg");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        try {
//            System.out.println(downloadPage("http://arhar.net"));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }
    
    public static void downloadFile(String urlString, String filepath) throws IOException {
        System.out.println("Attempting to download " + urlString + " to " + filepath);
        
        URL url = new URL(urlString);
        
        InputStream stream = url.openStream();
        
        ReadableByteChannel rbc = Channels.newChannel(stream);
        File outputFile = new File(filepath);
        outputFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(filepath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        
        stream.close();
    }
    
    public static String downloadPage(String urlString) throws IOException {
        URL url = new URL(urlString);

        InputStream stream = url.openStream();
        
        Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
        
        String output = scanner.next();
        
        stream.close();
        
        return output;
    }
    
    public static String parseHex(String input) throws NumberFormatException {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i+1).equals("%")) {
                output.append(hexToChar(input.substring(i+1, i+3)));
                i += 2;
            } else {
                output.append(input.substring(i, i+1));
            }
        }
        return output.toString();
    }
    
    public static char hexToChar(String input) throws NumberFormatException {
        return (char) Long.parseLong(input, 16);
    }
}
