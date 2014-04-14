package dbServer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.ws.Dispatch;

import sun.org.mozilla.javascript.internal.ast.CatchClause;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DBServer {

    private static int __PORT__ = 62491;
    public static long initTS;
    
    public static void main(String[] args) { 
        
        HttpServer server = null;
        initTS = System.currentTimeMillis();
        try{
            
            server = HttpServer.create(new InetSocketAddress(__PORT__), 0);
           
            // Chart WebPage Init Handlers
            server.createContext("/", new GET_IndexPage());
            server.createContext("/initTS", new GET_initTS());
            
            server.setExecutor(null); // creates a default executor
            server.start();
                      
            System.out.println("Server is running at: "+server.getAddress());
            
            while(true){
                storeLastSensorsData();
                Thread.sleep(10000);                
            }
               
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    private static void storeLastSensorsData(){
        
        SensorDriver sensor;
        
        sensor = new SensorDriver("https://172.20.70.232/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"library");
        
        sensor = new SensorDriver("https://172.20.70.229/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"kernel_14");
        
        sensor = new SensorDriver("https://172.20.70.238/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"kernel_16");
        
        sensor = new SensorDriver("https://172.20.70.234/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"room_1.17");
        
        sensor = new SensorDriver("https://172.20.70.235/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"room_1.19");
        
        sensor = new SensorDriver("https://172.20.70.237/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"UTA_A4");
        
        sensor = new SensorDriver("https://172.20.70.231/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"amphitheater_A4");
        
        sensor = new SensorDriver("https://172.20.70.233/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"amphitheater_A5");
        
        sensor = new SensorDriver("https://172.20.70.236/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor,"laboratory_1.58");
        
        
    }
    
    private static void fetchAndSaveDataFromSensor(SensorDriver sensor, String filename){
        String measureResultJSON =  sensor.getNewMeasureJSONformat();
        SensorMeasure mr = new SensorMeasure(measureResultJSON); 
        String measureResultNotJSON = "id="+mr.getID()+"|name="+mr.getName()+"|ts="+mr.geTimestamp()+"|p1="+mr.getPower1()+"|p2="+mr.getPower2()+"|p3="+mr.getPower3();         
        writeLastReadingInFile(measureResultJSON, filename+"-JSON_FileDB");
        writeLastReadingInFile(measureResultNotJSON, filename+"-Plain_FileDB");
    }
    
    
    private static void writeLastReadingInFile(String lastReadingJSON, String fileName){
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName+".txt", true)));       
            out.println(lastReadingJSON);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
  
        
    static class GET_IndexPage implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("src/dbServer/index.html");
            System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_initTS implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {        
            dispacthRequest(t, ""+initTS);            
        }
    }
   
    
    
   private static void dispacthRequest(HttpExchange t, String response) throws IOException {
       t.sendResponseHeaders(200, response.length());
       OutputStream os = t.getResponseBody();
       os.write(response.getBytes());
       os.close();
       
   }
  
    
    private static String readFile(String filename){
        
        //String filename = "src/httpServer/index.html";
        BufferedReader br = null;
        String response = null;
        StringBuilder sb = new StringBuilder();
        
        try {
            System.out.println("|-->"+ System.getProperty("user.dir")); //DEBUG 
            br = new BufferedReader(new FileReader(filename));         
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            response = sb.toString();
        }catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    
    
}
