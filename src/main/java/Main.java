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
//        createDailyData();
//        InputFileHandler.SleepDataJsonReader(PATH_TO_SLEEP_TMS7);
//        RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyData();
//
//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS6);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyData();
//        InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS7);
//        RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyData();
//
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS6);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyData();
//        InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS7);
//        RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//        createDailyData();

        RepositoryHandler.executeRules();

        RepositoryHandler.closeConn();
    }

    static void createDailyData(){
        // if 0 -> Sleep, if 1 -> HR, if 2 -> Steps
        int dataType = InputFileHandler.getDataType();
        if(dataType != 0){
            long[] fitbitData;
            if (dataType == 1){
                fitbitData = InputFileHandler.getHeartRateData();
            }else{
                fitbitData = InputFileHandler.getStepsData();
            }

            String[] timeseries = InputFileHandler.getTimeseriesArray();
            double dailySum = 0;
            double dailyCount = 0;
            ArrayList<Double> avgDayilyfitbitData = new ArrayList<>();
            ArrayList<Double> sumDayilyfitbitData = new ArrayList<>();
            ArrayList<String> dailyTimeseries = new ArrayList<>();
            String currentDay = timeseries[0].substring(0,10);
            for (int i=0; i<timeseries.length; i++){
                if (currentDay.equals(timeseries[i].substring(0,10))) {
                    dailySum += fitbitData[i];
                    dailyCount++;
                }else{
                    if (dataType == 1){
                        avgDayilyfitbitData.add(Math.round((dailySum/dailyCount) * 100.0) / 100.0);
                    }else{
                        sumDayilyfitbitData.add(dailySum);
                    }

                    dailyTimeseries.add(currentDay);
                    currentDay = timeseries[i].substring(0,10);
                    dailySum = fitbitData[i];
                    dailyCount = 1;
                }
                if (i == timeseries.length-1){
                    avgDayilyfitbitData.add(Math.round((dailySum/dailyCount) * 100.0) / 100.0);
                    sumDayilyfitbitData.add(dailySum);
                    dailyTimeseries.add(currentDay);
                }
            }

            System.out.println(InputFileHandler.getPatient());
            if (dataType == 1){
                for (int k=0; k<avgDayilyfitbitData.toArray().length; k++){
                    System.out.println(avgDayilyfitbitData.get(k)+", "+dailyTimeseries.get(k));
                }
                RepositoryHandler.addDailyMeasurements(avgDayilyfitbitData, dailyTimeseries, InputFileHandler.getPatient(), dataType);
            }else{
                for (int k=0; k<sumDayilyfitbitData.toArray().length; k++){
                    System.out.println(sumDayilyfitbitData.get(k)+", "+dailyTimeseries.get(k));
                }
                RepositoryHandler.addDailyMeasurements(sumDayilyfitbitData, dailyTimeseries, InputFileHandler.getPatient(), dataType);
            }
        }

    }

}