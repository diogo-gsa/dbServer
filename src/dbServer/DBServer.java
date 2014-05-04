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

    private static int __PORT__ = 62492;
    private static long initTS;
    private static long __PERIODIC_READING__ = 10000; // 10 Seconds
    
    public static void main(String[] args) {
        try {
            starttServerAndResources();
            startSensorsDataGathering();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void starttServerAndResources() throws IOException {
        HttpServer server = null;
        initTS = System.currentTimeMillis();
     
        System.out.println("Starting Server...");
        server = HttpServer.create(new InetSocketAddress(__PORT__), 0);

        server.createContext("/",       new GET_IndexPage());
        server.createContext("/initTS", new GET_initTS());

        System.out.println("Initialized resources:");
        for(SensorID id : SensorID.values()){
            String resource = id.toString().toLowerCase();
            server.createContext("/getAllHistoricReading/"+resource, new GET_AllHistoricReading(id));                
            System.out.println("/getAllHistoricReading/"+resource);
            
            server.createContext("/getCurrentReading/"+resource, new GET_CurrentReading(id));
            System.out.println("/getCurrentReading/"+resource);
            
            server.createContext("/getHistoricReading/"+resource, new GET_HistoricReading(id));
            System.out.println("/getHistoricReading/"+resource+"/int:pastReadings");
        }

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: " + server.getAddress());
    }

    private static void startSensorsDataGathering() throws InterruptedException {
        while (true) {
            storeLastSensorsData();
            Thread.sleep(__PERIODIC_READING__);
        }
    }

    private static void storeLastSensorsData() {
        SensorDriver sensor;
        for(SensorID id : SensorID.values()){
            sensor = new SensorDriver(IOMetadata.getSensorAddr(id), IOMetadata.getSensorUsername(), IOMetadata.getSensorPassword());
            fetchAndSaveDataFromSensor(sensor, IOMetadata.getSensorFileName(id));            
        }
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
        String measureResultNotJSON = buildJSONmeasureFile(mr);
        writeLastReadingInFile(measureResultNotJSON, filename);
    }

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

    private static void dispacthRequest(HttpExchange t, String response) throws IOException {
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

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
