package dbServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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
            
            //GET FilesDB Handlers
            server.createContext("/library", new GET_libraryFilesDB());
            server.createContext("/kernel_14", new GET_kernel_14FilesDB());
            server.createContext("/kernel_16", new GET_kernel_16FilesDB());
            server.createContext("/room_1.17", new GET_room_117FilesDB());
            server.createContext("/room_1.19", new GET_room_119FilesDB());
            server.createContext("/UTA_A4", new GET_UTA_A4FilesDB());
            server.createContext("/amphitheater_A4", new GET_amphitheater_A4FilesDB());
            server.createContext("/amphitheater_A5", new GET_amphitheater_A5FilesDB());
            server.createContext("/laboratory_1.58", new GET_laboratory_158FilesDB());
            
            
            server.setExecutor(null); // creates a default executor
            server.start();
                      
            System.out.println("Server is running at: "+server.getAddress());
            
            while(true){
                try {
                    storeLastSensorsData();
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }                                
            }
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    private static void startReading(){
        
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
            String response = readFile("index.html");
            System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_initTS implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {        
            dispacthRequest(t, ""+initTS);            
        }
    }
    
    static class GET_libraryFilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("library-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
   
   
    static class GET_kernel_14FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("kernel_14-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_kernel_16FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("kernel_16-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_room_117FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("room_1.17-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_room_119FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("room_1.19-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_UTA_A4FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("UTA_A4-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_amphitheater_A4FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("amphitheater_A4-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_amphitheater_A5FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("amphitheater_A5-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class GET_laboratory_158FilesDB implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("laboratory_1.58-"+format+".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);            
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
