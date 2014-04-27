package dbServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class DBServer {

    private static int __PORT__ = 62490;
    public static long initTS;

    public static void main(String[] args) {

        HttpServer server = null;
        initTS = System.currentTimeMillis();
        try {
            server = HttpServer.create(new InetSocketAddress(__PORT__), 0);

            // Chart WebPage Init Handlers
            server.createContext("/",       new GET_IndexPage());
            server.createContext("/initTS", new GET_initTS());

            //GET FilesDB
            //usage: 
            server.createContext("/library",         new GET_libraryFilesDB());
            server.createContext("/kernel_14",       new GET_kernel_14FilesDB());
            server.createContext("/kernel_16",       new GET_kernel_16FilesDB());
            server.createContext("/room_1.17",       new GET_room_117FilesDB());
            server.createContext("/room_1.19",       new GET_room_119FilesDB());
            server.createContext("/UTA_A4",          new GET_UTA_A4FilesDB());
            server.createContext("/amphitheater_A4", new GET_amphitheater_A4FilesDB());
            server.createContext("/amphitheater_A5", new GET_amphitheater_A5FilesDB());
            server.createContext("/laboratory_1.58", new GET_laboratory_158FilesDB());
           
            
            server.createContext("/getAllHistoricReading/library", new GET_AllHistoricReading(SensorID.LIBRARY));
            server.createContext("/getAllHistoricReading/department_14", new GET_AllHistoricReading(SensorID.DEPARTMENT_14));
            server.createContext("/getAllHistoricReading/department_16", new GET_AllHistoricReading(SensorID.DEPARTMENT_16));
            server.createContext("/getAllHistoricReading/room_1_17", new GET_AllHistoricReading(SensorID.ROOM_1_17));
            server.createContext("/getAllHistoricReading/room_1_19", new GET_AllHistoricReading(SensorID.ROOM_1_19));
            server.createContext("/getAllHistoricReading/UTA_A4", new GET_AllHistoricReading(SensorID.UTA_A4));
            server.createContext("/getAllHistoricReading/amphitheater_A4", new GET_AllHistoricReading(SensorID.AMPHITHEATER_A4));
            server.createContext("/getAllHistoricReading/amphitheater_A5", new GET_AllHistoricReading(SensorID.AMPHITHEATER_A5));
            server.createContext("/getAllHistoricReading/laboratory_1_58", new GET_AllHistoricReading(SensorID.LABORATORY_1_58));
            
            
            //== REST Core Resources ====================================
            //usage: /getCurrentReading/library
            server.createContext("/getCurrentReading/library",          new GET_CurrentReading(SensorID.LIBRARY));
            server.createContext("/getCurrentReading/department_14",    new GET_CurrentReading(SensorID.DEPARTMENT_14));
            server.createContext("/getCurrentReading/department_16",    new GET_CurrentReading(SensorID.DEPARTMENT_16));
            server.createContext("/getCurrentReading/room_1_17",        new GET_CurrentReading(SensorID.ROOM_1_17));
            server.createContext("/getCurrentReading/room_1_19",        new GET_CurrentReading(SensorID.ROOM_1_19));
            server.createContext("/getCurrentReading/UTA_A4",           new GET_CurrentReading(SensorID.UTA_A4));
            server.createContext("/getCurrentReading/amphitheater_A4",  new GET_CurrentReading(SensorID.AMPHITHEATER_A4));
            server.createContext("/getCurrentReading/amphitheater_A5",  new GET_CurrentReading(SensorID.AMPHITHEATER_A5));
            server.createContext("/getCurrentReading/laboratory_1_58",  new GET_CurrentReading(SensorID.LABORATORY_1_58));
            
            //usage: /getHistoricReading/last/17            
            server.createContext("/getHistoricReading/library",         new GET_HistoricReading(SensorID.LIBRARY));
            server.createContext("/getHistoricReading/department_14",   new GET_HistoricReading(SensorID.DEPARTMENT_14));
            server.createContext("/getHistoricReading/department_16",   new GET_HistoricReading(SensorID.DEPARTMENT_16));
            server.createContext("/getHistoricReading/room_1_17",       new GET_HistoricReading(SensorID.ROOM_1_17));
            server.createContext("/getHistoricReading/room_1_19",       new GET_HistoricReading(SensorID.ROOM_1_19));
            server.createContext("/getHistoricReading/UTA_A4",          new GET_HistoricReading(SensorID.UTA_A4));
            server.createContext("/getHistoricReading/amphitheater_A4", new GET_HistoricReading(SensorID.AMPHITHEATER_A4));
            server.createContext("/getHistoricReading/amphitheater_A5", new GET_HistoricReading(SensorID.AMPHITHEATER_A5));
            server.createContext("/getHistoricReading/laboratory_1_58", new GET_HistoricReading(SensorID.LABORATORY_1_58));
            //============================================================

            server.setExecutor(null); // creates a default executor
            server.start();

            System.out.println("Server is running at: " + server.getAddress());

            while (true) {
                try {
                    storeLastSensorsData();
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void storeLastSensorsData() {

        SensorDriver sensor;

        for(SensorID id : SensorID.values()){
            System.out.println("[T]:"+id);
            sensor = new SensorDriver(IOMetadata.getSensorAddr(id), IOMetadata.getSensorUsername(), IOMetadata.getSensorPassword());
            fetchAndSaveDataFromSensor(sensor, IOMetadata.getSensorFileName(id));            
        }
        
        
        /*sensor = new SensorDriver("https://172.20.70.232/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "library");

        sensor = new SensorDriver("https://172.20.70.229/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "kernel_14");

        sensor = new SensorDriver("https://172.20.70.238/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "kernel_16");

        sensor = new SensorDriver("https://172.20.70.234/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "room_1.17");

        sensor = new SensorDriver("https://172.20.70.235/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "room_1.19");

        sensor = new SensorDriver("https://172.20.70.237/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "UTA_A4");

        sensor = new SensorDriver("https://172.20.70.231/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "amphitheater_A4");

        sensor = new SensorDriver("https://172.20.70.233/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "amphitheater_A5");

        sensor = new SensorDriver("https://172.20.70.236/reading", "root", "root");
        fetchAndSaveDataFromSensor(sensor, "laboratory_1.58"); */


    }

    private static String buildJSONmeasureFile(SensorMeasure sm) {
        String id = sm.getID();
        String name = sm.getName();
        long ts = sm.geTimestamp();

        double vPh1 = sm.getVoltagePhase1();
        double vPh2 = sm.getVoltagePhase2();
        double vPh3 = sm.getVoltagePhase3();

        double cPh1 = sm.getCurrentPhase1();
        double cPh2 = sm.getCurrentPhase2();
        double cPh3 = sm.getCurrentPhase3();

        double pfPh1 = sm.getPowerFactorPhase1();
        double pfPh2 = sm.getPowerFactorPhase2();
        double pfPh3 = sm.getPowerFactorPhase3();

        return "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"ts\":" + ts
                + ",\"ph\":{\"1\":{\"v\":" + vPh1 + ",\"c\":" + cPh1 + ",\"pf\":" + pfPh1
                + "},\"2\":{\"v\":" + vPh2 + ",\"c\":" + cPh2 + ",\"pf\":" + pfPh2
                + "},\"3\":{\"v\":" + vPh3 + ",\"c\":" + cPh3 + ",\"pf\":" + pfPh3 + "}}}";

    }

    private static void fetchAndSaveDataFromSensor(SensorDriver sensor, String filename) {
        String measureResultJSON = sensor.getNewMeasureJSONformat();
        SensorMeasure mr = new SensorMeasure(measureResultJSON);
        //String measureResultNotJSON = "id="+mr.getID()+"|name="+mr.getName()+"|ts="+mr.geTimestamp()+"|p1="+mr.getPower1()+"|p2="+mr.getPower2()+"|p3="+mr.getPower3();         
        String measureResultNotJSON = buildJSONmeasureFile(mr);
        writeLastReadingInFile(measureResultNotJSON, filename);
        // writeLastReadingInFile(measureResultNotJSON, filename+"-Plain_FileDB");
    }

    /*
     * private static void writeLastReadingInFile(String lastReadingJSON, String
     * fileName){ try { PrintWriter out = new PrintWriter(new BufferedWriter(new
     * FileWriter(fileName+".txt", true))); out.println(lastReadingJSON); out.close(); }
     * catch (IOException e) { e.printStackTrace(); } }
     */

    private static void writeLastReadingInFile(String lastReadingJSON, String fileName) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "rws");
            byte[] text = new byte[(int) file.length()];
            file.readFully(text);
            file.seek(0);
            file.writeBytes(lastReadingJSON + "\n");
            file.write(text);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class GET_IndexPage
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("index.html");
            System.out.println("Loaded: " + "index.html");
            dispacthRequest(t, response);
        }
    }

    static class GET_initTS
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            dispacthRequest(t, "" + initTS);
        }
    }
    
    /*
    static class GET_CurrentReading implements HttpHandler {
        // /getCurrentReading/library
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getPath();            
            String sensorID = parametersListURI.split("/")[2]; // e.g. extract "library"
            
            SensorDriver sensor = null;            
            switch(sensorID){
                case "library"          : sensor = new SensorDriver("https://172.20.70.232/reading", "root", "root"); break;
                case "kernel_14"        : sensor = new SensorDriver("https://172.20.70.229/reading", "root", "root"); break;
                case "kernel_16"        : sensor = new SensorDriver("https://172.20.70.238/reading", "root", "root"); break;
                case "room_1.17"        : sensor = new SensorDriver("https://172.20.70.234/reading", "root", "root"); break;
                case "room_1.19"        : sensor = new SensorDriver("https://172.20.70.235/reading", "root", "root"); break;                
                case "UTA_A4"           : sensor = new SensorDriver("https://172.20.70.237/reading", "root", "root"); break;                
                case "amphitheater_A4"  : sensor = new SensorDriver("https://172.20.70.231/reading", "root", "root"); break;
                case "amphitheater_A5"  : sensor = new SensorDriver("https://172.20.70.233/reading", "root", "root"); break;
                case "laboratory_1.58"  : sensor = new SensorDriver("https://172.20.70.236/reading", "root", "root"); break;
                default                 : System.out.println("*** Error ***: This sensor does NOT exist");
            }                        
            SensorMeasure measure =  sensor.getNewMeasure();
            String response = buildJSONmeasureFile(measure);
            dispacthRequest(t, response);
        }
    }*/
    
    
    static class GET_CurrentReading implements HttpHandler {
        private SensorID id;
        
        public GET_CurrentReading(SensorID id){
            this.id = id;
        }
        
        public void handle(HttpExchange t) throws IOException {
            SensorDriver sensor = new SensorDriver(IOMetadata.getSensorAddr(id), IOMetadata.getSensorUsername(), IOMetadata.getSensorPassword());
            String response = buildJSONmeasureFile(sensor.getNewMeasure());
            dispacthRequest(t, response);
        }
    }
    
    
    
    static class GET_lastLibraryFilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getPath(); // get the "idSensor=library" URL's part            
            int numberOfLastReadings = Integer.parseInt(parametersListURI.split("/")[3]); //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFileFisrtLines("library-JSON_FileDB.txt", numberOfLastReadings);
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }
    
    //server.createContext("/getHistoricReading/library", new GET_HistoricReading());    
    static class GET_HistoricReading implements HttpHandler {
        
        private SensorID id;
        
        public GET_HistoricReading(SensorID id){
            this.id = id;
        }
       
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getPath();            
            int numberOfLastReadings = Integer.parseInt(parametersListURI.split("/")[3]);
            String response = readFileFisrtLines(IOMetadata.getSensorFileName(id), numberOfLastReadings);
            dispacthRequest(t, response);
        
        }
    }

    static class GET_AllHistoricReading implements HttpHandler {
        
        private SensorID id;
        
        public GET_AllHistoricReading(SensorID id){
            this.id = id;
        }
        
        public void handle(HttpExchange t) throws IOException {
            String response = readFile(IOMetadata.getSensorFileName(id));
            dispacthRequest(t, response);
        }
    }
    
    
    static class GET_libraryFilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("library-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }


    static class GET_kernel_14FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("kernel_14-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_kernel_16FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("kernel_16-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_room_117FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("room_1.17-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_room_119FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("room_1.19-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_UTA_A4FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("UTA_A4-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_amphitheater_A4FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("amphitheater_A4-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_amphitheater_A5FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("amphitheater_A5-" + format + ".txt");
            //System.out.println("Loaded: "+"index.html");            
            dispacthRequest(t, response);
        }
    }

    static class GET_laboratory_158FilesDB
            implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String parametersListURI = t.getRequestURI().getQuery(); // get the "idSensor=library" URL's part            
            String format = parametersListURI.split("=")[1]; //extract resource name (eg. amcharts.js)
            //library-JSON_FileDB.txt
            //ANTES DE PASSAR PARA JAR: "src/dbServer/index.html" --> "index.html"
            String response = readFile("laboratory_1.58-" + format + ".txt");
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


    /*
     * private static String calculateTotalPower(String read){ JSONParser parser = new
     * JSONParser(); String ts; String ph1; String ph2; String ph3;
     * 
     * try { JSONObject measureJSONobject = (JSONObject) parser.parse(read); ts = (String)
     * measureJSONobject.get("timestamp"); ph1 = (String)
     * parsePhaseJSONobject(measureJSONobject, "1"); ph2 = (String)
     * parsePhaseJSONobject(measureJSONobject, "2"); ph3 = (String)
     * parsePhaseJSONobject(measureJSONobject, "3"); } catch (ParseException e) {
     * System.err
     * .println("[Diogo] Constructor JavaMeasure.Java: probably malformed JSON file...");
     * e.printStackTrace(); }
     * 
     * return ""; }
     */

    private static String readFileFisrtLines(String filename, int last) {

        //String filename = "src/httpServer/index.html";
        BufferedReader br = null;
        String response = null;
        StringBuilder sb = new StringBuilder();
        int index = 1;

        try {
            //  System.out.println("|-->"+ System.getProperty("user.dir")); //DEBUG 
            br = new BufferedReader(new FileReader(filename));

            String line = br.readLine();
            while ((index <= last) && (line != null)) {
                //calculateTotalPower(line);
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                index++;
            }
            response = sb.toString();
        } catch (IOException e) {
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

    private static String readFile(String filename) {

        //String filename = "src/httpServer/index.html";
        BufferedReader br = null;
        String response = null;
        StringBuilder sb = new StringBuilder();

        try {
            //  System.out.println("|-->"+ System.getProperty("user.dir")); //DEBUG 
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            response = sb.toString();
        } catch (IOException e) {
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
