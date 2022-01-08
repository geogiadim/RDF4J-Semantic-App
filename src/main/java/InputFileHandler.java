import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class InputFileHandler {
    public static void JsonReader() throws IOException, ParseException {
        // initialize a jason parser
        JSONParser jsonParser = new JSONParser();
        // initialize a file reader with hardcoded file path
        FileReader reader  = new FileReader("./Files/TMS6-Sleep2.json");
        // create object that parses all json data
        Object obj = jsonParser.parse(reader);
        // cast obj as JSONObject
        JSONObject jsonObj = (JSONObject) obj;

        // insert data Stringo a JSONArray
        JSONArray dataArray = (JSONArray)jsonObj.get("data");
        long[][] sleepData = new long[dataArray.size()][5];
        // iterate JSONArray to take each value given specific key
        int i = 0;
        for (Object o : dataArray) {
            JSONObject dataRow = (JSONObject) o;
            long lightMinutes = (long) dataRow.get("light_minutes");
            sleepData[i][0] = lightMinutes;
            long remMinutes = (long) dataRow.get("rem_minutes");
            sleepData[i][1] = remMinutes;
            long deepMinutes = (long) dataRow.get("deep_minutes");
            sleepData[i][2] = deepMinutes;
            long minutesAwake = (long) dataRow.get("minutes_awake");
            sleepData[i][3] = minutesAwake;
            long minutesAsleep = (long) dataRow.get("minutes_asleep");
            sleepData[i][4] = minutesAsleep;
            String timeSeries = (String) dataRow.get("timeseries");
            i++;
            System.out.println(lightMinutes +", "+ remMinutes +", "+ deepMinutes +", "+ minutesAwake +", "+ minutesAsleep +", "+ timeSeries);
        }
        System.out.println();
        for(int k=0; k<dataArray.size();k++){
            for(int j=0; j<5;j++){
                System.out.print(sleepData[k][j] + ", ");
            }
            System.out.println();
        }
    }
}
