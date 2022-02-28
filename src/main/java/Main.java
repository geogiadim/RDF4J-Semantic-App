import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String PATH_TO_SLEEP_TMS6 = "./Files/TMS6-Sleep.json";
    private static final String PATH_TO_STEPS_TMS6 = "./Files/TMS6-Steps.json";
    private static final String PATH_TO_HR_TMS6 = "./Files/TMS6-HeartRates.json";
    private static final String PATH_TO_SLEEP_TMS7 = "./Files/TMS7-Sleep.json";
    private static final String PATH_TO_STEPS_TMS7 = "./Files/TMS7-Steps.json";
    private static final String PATH_TO_HR_TMS7 = "./Files/TMS7-HeartRates.json";

    public static void main(String[] args) throws IOException, ParseException {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter 1 if you want to populate GraphDB with raw data, \n2 if you want to execute rules into GraphDB, \n3 if you want to wipe all rules in GraphDB:  ");
        int number = input.nextInt();

        while(number != 1 && number!=2 && number!=3){
            System.out.print("Wrong input. Please try again:  ");
            number = input.nextInt();
        }

        if (number == 1){
            Scanner input2 = new Scanner(System.in);
            System.out.print("Enter 1 if you want to populate sleep data, \n2 for heartRate data, \n3 for step data:  ");
            int number2 = input2.nextInt();

            while(number2 != 1 && number2 !=2 && number2 !=3){
                System.out.print("Wrong input. Please try again:  ");
                number2 = input2.nextInt();
            }
            System.out.print("Give the full path of json file that contains the data:  ");
            Scanner input3 = new Scanner(System.in);
            String file_path = input3.nextLine();
            RepositoryHandler.initRepo();
            if (number2 == 1){
                //get data
                InputFileHandler.SleepDataJsonReader(file_path);
                //add data to a model
                RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
                //init shacl repository
                ShaclRepo shaclRepository = new ShaclRepo();
                shaclRepository.loadShaclRules();
                executeValidation(shaclRepository);
            }else if (number2 == 2){
                InputFileHandler.HeartRateJsonReader(file_path);
                RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
                //init shacl repository
                ShaclRepo shaclRepository = new ShaclRepo();
                shaclRepository.loadShaclRules();
                executeValidation(shaclRepository);
            }else{
                InputFileHandler.StepsJsonReader(file_path);
                RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
                //init shacl repository
                ShaclRepo shaclRepository = new ShaclRepo();
                shaclRepository.loadShaclRules();
                executeValidation(shaclRepository);
            }
        }else if (number == 2){
            RepositoryHandler.initRepo();
            RepositoryHandler.executeRules();
            RepositoryHandler.closeConn();
        }else{
            RepositoryHandler.initRepo();
            RepositoryHandler.wipeRules();
            RepositoryHandler.closeConn();
        }
    }

    private static void executeValidation(ShaclRepo shaclRepository){
        //execute validation and check if model passes it
        shaclRepository.executeShaclValidation(RepositoryHandler.getModel());
        boolean validation = shaclRepository.getValidation();
        if (validation){
            //if true commit data to repo and add daily data to repo
            RepositoryHandler.getRepoConnection().commit();
            createDailyData();
        }else{
            RepositoryHandler.closeConn();
            Rio.write(shaclRepository.getValidationReportModel(), System.out, RDFFormat.TURTLE);
            System.out.println("SHACL VIOLATION");
        }
        shaclRepository.closeConnection();
    }

    private static void createDailyData(){
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


//            RepositoryHandler.initRepo();
//            InputFileHandler.SleepDataJsonReader(PATH_TO_SLEEP_TMS6);
//            RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();
//            InputFileHandler.SleepDataJsonReader(PATH_TO_SLEEP_TMS7);
//            RepositoryHandler.addSleepData(InputFileHandler.getSleepData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();
//
//            InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS6);
//            RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();
//            InputFileHandler.HeartRateJsonReader(PATH_TO_HR_TMS7);
//            RepositoryHandler.addHeartRateData(InputFileHandler.getHeartRateData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();
//
//            InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS6);
//            RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();
//            InputFileHandler.StepsJsonReader(PATH_TO_STEPS_TMS7);
//            RepositoryHandler.addStepsData(InputFileHandler.getStepsData(), InputFileHandler.getTimeseriesArray(), InputFileHandler.getPatient());
//            createDailyData();