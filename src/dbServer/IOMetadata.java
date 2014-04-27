package dbServer;

import java.util.Map;
import java.util.TreeMap;

public class IOMetadata {
    
    private static Map<SensorID, String> sensorAddresses;
    private static Map<SensorID, String> sensorDBFileNames;
    private static String sensorUsername;
    private static String sensorPassword;

    static{
        sensorAddresses = new TreeMap<SensorID, String>();
        sensorDBFileNames = new TreeMap<SensorID, String>();
        sensorUsername = "root";        
        sensorPassword = "root";
        
        sensorAddresses.put(SensorID.LIBRARY,           "https://172.20.70.232/reading");
        sensorAddresses.put(SensorID.DEPARTMENT_14,     "https://172.20.70.229/reading");
        sensorAddresses.put(SensorID.DEPARTMENT_16,     "https://172.20.70.238/reading");
        sensorAddresses.put(SensorID.ROOM_1_17,         "https://172.20.70.234/reading");
        sensorAddresses.put(SensorID.ROOM_1_19,         "https://172.20.70.235/reading");
        sensorAddresses.put(SensorID.UTA_A4,            "https://172.20.70.237/reading");
        sensorAddresses.put(SensorID.AMPHITHEATER_A4,   "https://172.20.70.231/reading");
        sensorAddresses.put(SensorID.AMPHITHEATER_A5,   "https://172.20.70.233/reading");
        sensorAddresses.put(SensorID.LABORATORY_1_58,   "https://172.20.70.236/reading");
        
        sensorDBFileNames.put(SensorID.LIBRARY,         "library-JSON_FileDB");
        sensorDBFileNames.put(SensorID.DEPARTMENT_14,   "kernel_14-JSON_FileDB");
        sensorDBFileNames.put(SensorID.DEPARTMENT_16,   "kernel_16-JSON_FileDB");
        sensorDBFileNames.put(SensorID.ROOM_1_17,       "room_1.17-JSON_FileDB");
        sensorDBFileNames.put(SensorID.ROOM_1_19,       "room_1.19-JSON_FileDB");
        sensorDBFileNames.put(SensorID.UTA_A4,          "UTA_A4-JSON_FileDB");
        sensorDBFileNames.put(SensorID.AMPHITHEATER_A4, "amphitheater_A4-JSON_FileDB");
        sensorDBFileNames.put(SensorID.AMPHITHEATER_A5, "amphitheater_A5-JSON_FileDB");
        sensorDBFileNames.put(SensorID.LABORATORY_1_58, "laboratory_1.58-JSON_FileDB");
    }

    public static String getSensorUsername(){
        return sensorUsername;
    }
    
    public static String getSensorPassword(){
        return sensorPassword;
    }
    
    public static String getSensorAddr(SensorID id){
        return sensorAddresses.get(id);
    }
    
    public static String getSensorFileName(SensorID id){
        return sensorDBFileNames.get(id);
    }    
}//EOF