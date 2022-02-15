import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

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
//
//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS6);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyHRData();
//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS7);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyHRData();
//
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS6);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS7);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());

        RepositoryHandler.executeRules();

        RepositoryHandler.closeConn();
    }

    static void createDailyHRData(){
        long[] hrData = InputFileHandler.getHeartRateData();
        String[] timeseries = InputFileHandler.getTimeseriesArray();
        double dailyHRsum = 0;
        double hrCount = 0;
        ArrayList<Double> avgDayilyHRData = new ArrayList<>();
        ArrayList<String> dailyHRtimeseries = new ArrayList<>();
        String currentDay = timeseries[0].substring(0,10);
        for (int i=0; i<timeseries.length; i++){
            if (currentDay.equals(timeseries[i].substring(0,10))) {
                dailyHRsum += hrData[i];
                hrCount++;
            }else{
                avgDayilyHRData.add(Math.round((dailyHRsum/hrCount) * 100.0) / 100.0);
                dailyHRtimeseries.add(currentDay);
                currentDay = timeseries[i].substring(0,10);
                dailyHRsum = hrData[i];
                hrCount = 1;
            }
            if (i == timeseries.length-1){
                avgDayilyHRData.add(Math.round((dailyHRsum/hrCount) * 100.0) / 100.0);
                dailyHRtimeseries.add(currentDay);
            }
        }

        System.out.println(InputFileHandler.getPatient());
        for (int k=0; k<avgDayilyHRData.toArray().length; k++){
            System.out.println(avgDayilyHRData.get(k)+", "+dailyHRtimeseries.get(k));
        }

        RepositoryHandler.addDailyHRMeasurements(avgDayilyHRData, dailyHRtimeseries, InputFileHandler.getPatient());

    }

}