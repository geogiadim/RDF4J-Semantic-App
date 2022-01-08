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
        // iterate JSONArray to take each value given specific key
        for (Object o : dataArray) {
            JSONObject dataRow = (JSONObject) o;
            long lightMinutes = (long) dataRow.get("light_minutes");
            long remMinutes = (long) dataRow.get("rem_minutes");
            long deepMinutes = (long) dataRow.get("deep_minutes");
            long minutesAwake = (long) dataRow.get("minutes_awake");
            long minutesAsleep = (long) dataRow.get("minutes_asleep");
            long totalMinutes = minutesAsleep + minutesAwake;
            String timeSeries = (String) dataRow.get("timeseries");
            System.out.println(lightMinutes +", "+ remMinutes +", "+ deepMinutes +", "+ minutesAwake +", "+ minutesAsleep +", "+ totalMinutes +", "+ timeSeries);
        }
    }
}
