import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class InputFileHandler {
    private static String[] timeseriesArray;
    private static final int NUM_OF_SLEEP_DATA_FIELDS = 13;
    private static long[][] sleepData;
    private static long[] heartRateData;
    private static long[] stepsData;
    private static String patient;
    private static int dataType; // if 0 -> Sleep, if 1 -> HR, if 2 -> Steps

    public static void StepsJsonReader(String pathToFile) throws IOException, ParseException {
        // initialize a jason parser
        JSONParser jsonParser = new JSONParser();
        // initialize a file reader with hardcoded file path
        FileReader reader  = new FileReader(pathToFile);
        // create object that parses all json data
        Object obj = jsonParser.parse(reader);
        // cast obj as JSONObject
        JSONObject jsonObj = (JSONObject) obj;

        patient = (String) jsonObj.get("patient");
        // insert data in JSONArray
        JSONArray dataArray = (JSONArray)jsonObj.get("data");
        stepsData = new long[dataArray.size()];
        timeseriesArray = new String[dataArray.size()];
        int i = 0;
        for (Object o : dataArray) {
            JSONObject dataRow = (JSONObject) o;
            stepsData[i] = (long) dataRow.get("fields.rate");
            timeseriesArray[i] = (String) dataRow.get("fields.created_at");
            i++;
        }
        setDataType(2);
//        printStepsData();
    }


    public static void HeartRateJsonReader(String pathToFile) throws IOException, ParseException {
        // initialize a jason parser
        JSONParser jsonParser = new JSONParser();
        // initialize a file reader with hardcoded file path
        FileReader reader  = new FileReader(pathToFile);
        // create object that parses all json data
        Object obj = jsonParser.parse(reader);
        // cast obj as JSONObject
        JSONObject jsonObj = (JSONObject) obj;

        patient = (String) jsonObj.get("patient");
        // insert data in JSONArray
        JSONArray dataArray = (JSONArray)jsonObj.get("data");
        heartRateData = new long[dataArray.size()];
        timeseriesArray = new String[dataArray.size()];
        int i = 0;
        for (Object o : dataArray) {
            JSONObject dataRow = (JSONObject) o;
            heartRateData[i] = (long) dataRow.get("fields.resting_hr");
            timeseriesArray[i] = (String) dataRow.get("fields.created_at");
            i++;
        }
        setDataType(1);
//        printHeartRateData();
    }

    public static void SleepDataJsonReader(String pathToFile) throws IOException, ParseException {
        // initialize a jason parser
        JSONParser jsonParser = new JSONParser();
        // initialize a file reader with hardcoded file path
        FileReader reader  = new FileReader(pathToFile);
        // create object that parses all json data
        Object obj = jsonParser.parse(reader);
        // cast obj as JSONObject
        JSONObject jsonObj = (JSONObject) obj;

        patient = (String) jsonObj.get("patient");
        // insert data in JSONArray
        JSONArray dataArray = (JSONArray)jsonObj.get("data");
        // initialize the two arrays
        sleepData = new long[dataArray.size()][NUM_OF_SLEEP_DATA_FIELDS];
        timeseriesArray = new String[dataArray.size()];
        // iterate JSONArray to take each value given specific key
        int i = 0;
        for (Object o : dataArray) {
            JSONObject dataRow = (JSONObject) o;
            sleepData[i][0] = (long) dataRow.get("light_minutes");
            sleepData[i][1] = (long) dataRow.get("rem_minutes");
            sleepData[i][2] = (long) dataRow.get("deep_minutes");
            sleepData[i][3] = (long) dataRow.get("wake_minutes");
            sleepData[i][4] = (long) dataRow.get("asleep_minutes");
            sleepData[i][5] = (long) dataRow.get("restless_minutes");
            sleepData[i][6] = (long) dataRow.get("awake_minutes");
            sleepData[i][7] = (long) dataRow.get("minutes_awake");
            sleepData[i][8] = (long) dataRow.get("minutes_asleep");
            sleepData[i][9] = (long) dataRow.get("wake_count");
            sleepData[i][10] = (long) dataRow.get("light_count");
            sleepData[i][11] = (long) dataRow.get("rem_count");
            sleepData[i][12] = (long) dataRow.get("deep_count");
            timeseriesArray[i] = (String) dataRow.get("timeseries");
            i++;
        }
        setDataType(0);
//        printSleepData();
    }

    private static void printSleepData(){
        System.out.println(patient);
        for(int k=0; k<timeseriesArray.length; k++){
            for(int j=0; j<NUM_OF_SLEEP_DATA_FIELDS;j++){
                System.out.print(sleepData[k][j] + ", ");
            }
            System.out.println(timeseriesArray[k]);
        }
    }

    private static void printHeartRateData(){
        System.out.println(patient);
        for (int k=0; k<timeseriesArray.length;k++){
            System.out.println(heartRateData[k] + ", " + timeseriesArray[k]);
        }
    }

    private static void printStepsData(){
        System.out.println(patient);
        for (int k=0; k<timeseriesArray.length;k++){
            System.out.println(stepsData[k] + ", " + timeseriesArray[k]);
        }
    }

    public static long[][] getSleepData() { return sleepData; }
    public static long[] getStepsData() {return stepsData;}
    public static String[] getTimeseriesArray() { return timeseriesArray; }
    public static String getPatient() { return patient; }
    public static long[] getHeartRateData() {return heartRateData;}
    public static int getDataType() {return dataType;}
    private static void setDataType(int val) {dataType = val;}
}
