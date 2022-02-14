import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    private static final String PATH_TO_SLEEP_TMS6 = "./Files/TMS6-Sleep.json";
    private static final String PATH_TO_STEPS_TMS6 = "./Files/TMS6-Steps.json";
    private static final String PATH_TO_HR_TMS6 = "./Files/TMS6-HeartRates.json";
    private static final String PATH_TO_SLEEP_TMS7 = "./Files/TMS7-Sleep.json";
    private static final String PATH_TO_STEPS_TMS7 = "./Files/TMS7-Steps.json";
    private static final String PATH_TO_HR_TMS7 = "./Files/TMS7-HeartRates.json";

    public static void main(String[] args) throws IOException, ParseException {
        RepositoryHandler.initRepo();

//        InputFileHandler.SleepDataJsonReader(PATH_TO_SLEEP_TMS6);
//        RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        InputFileHandler.SleepDataJsonReader(PATH_TO_SLEEP_TMS7);
//        RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());

//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS6);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS7);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS6);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS7);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());

        RepositoryHandler.executeRules();

        RepositoryHandler.closeConn();
    }
}



//if (month<10){
//    queryString+= "    FILTER (?resultTime > \"2021-0"+month;
//    if (day<10){
//        queryString+="-0"+day+"T00:00:00Z\"^^xsd:dateTime )\n" +
//        "    FILTER (?resultTime < \"2021-0"+month+"-0"+day+"T23:59:59Z\"^^xsd:dateTime )\n" +
//        "}\n" +
//        "GROUP BY ?patient\n" +
//        "\n ";
//    }else if (day>10){
//        queryString+="-"+day+"T00:00:00Z\"^^xsd:dateTime )\n" +
//        "    FILTER (?resultTime < \"2021-0"+month+"-"+day+"T23:59:59Z\"^^xsd:dateTime )\n" +
//        "}\n" +
//        "GROUP BY ?patient\n" +
//        "\n ";
//    }
//}else if (month>10){
//    queryString+= "    FILTER (?resultTime > \"2021-"+month;
//    if (day<10){
//        queryString+="-0"+day+"T00:00:00Z\"^^xsd:dateTime )\n" +
//        "    FILTER (?resultTime < \"2021-"+month+"-0"+day+"T23:59:59Z\"^^xsd:dateTime )\n" +
//        "}\n" +
//        "GROUP BY ?patient\n" +
//        "\n ";
//    }else if (day>10){
//        queryString+="-"+day+"T00:00:00Z\"^^xsd:dateTime )\n" +
//        "    FILTER (?resultTime < \"2021-"+month+"-"+day+"T23:59:59Z\"^^xsd:dateTime )\n" +
//        "}\n" +
//        "GROUP BY ?patient\n" +
//        "\n ";
//    }
//}