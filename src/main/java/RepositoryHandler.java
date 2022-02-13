import org.eclipse.rdf4j.exceptions.ValidationException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;

import static org.eclipse.rdf4j.model.util.Statements.statement;
import static org.eclipse.rdf4j.model.util.Values.iri;
import static org.eclipse.rdf4j.model.util.Values.literal;

public class RepositoryHandler {
    // URL of the remote RDF4J Server we want to access
    private static final String SERVER_URL = "http://localhost:7200/";
    // ID of repository we want to access
    private static final String REPO_ID = "rdf4j-repo";
    private static RemoteRepositoryManager manager;
    private static Repository repo;
    private static RepositoryConnection con;
    private static final String ONTOLOGY_URI = "http://www.semanticweb.org/patient-observations#";
    // sleep properties
    private static final String REM_MINUTES = ONTOLOGY_URI+"remMinutes";
    private static final String LIGHT_MINUTES = ONTOLOGY_URI+"lightMinutes";
    private static final String DEEP_MINUTES = ONTOLOGY_URI+"deepMinutes";
    private static final String WAKE_MINUTES = ONTOLOGY_URI+"wakeMinutes";
    private static final String ASLEEP_MINUTES = ONTOLOGY_URI+"asleepMinutes";
    private static final String RESTLESS_MINUTES = ONTOLOGY_URI+"restlessMinutes";
    private static final String AWAKE_MINUTES = ONTOLOGY_URI+"awakeMinutes";
    private static final String MINUTES_AWAKE = ONTOLOGY_URI+"minutesAwake";
    private static final String MINUTES_ASLEEP = ONTOLOGY_URI+"minutesAsleep";
    private static final String WAKE_COUNT = ONTOLOGY_URI+"wakeCount";
    private static final String LIGHT_COUNT = ONTOLOGY_URI+"lghtCount";
    private static final String REM_COUNT = ONTOLOGY_URI+"remCount";
    private static final String DEEP_COUNT = ONTOLOGY_URI+"deepCount";
    // heart rate property and step property
    private static final String HEART_RATE = ONTOLOGY_URI+"heartRate";
    private static final String NUM_OF_STEPS = ONTOLOGY_URI+"numOfSteps";

    private static final String OBSERVED_PROPERTY = "http://www.w3.org/ns/sosa/observedProperty";
    private static final String IS_OBS_FOR = ONTOLOGY_URI+"isObservationFor";

    // time properties
    private static final String START_TIME = ONTOLOGY_URI+"startTime";
    private static final String END_TIME = ONTOLOGY_URI+"endTime";
    private static final String RESULT_TIME = "http://www.w3.org/ns/sosa/resultTime";

    // classes
    private static final String OBSERVATION = "http://www.w3.org/ns/sosa/Observation";
    private static final String SLEEP_PROPERTY = ONTOLOGY_URI+"SleepProperty";
    private static final String HEART_RATE_PROPERTY = ONTOLOGY_URI+"HeartRateProperty";
    private static final String STEP_PROPERTY = ONTOLOGY_URI+"StepProperty";
    private static final String PERSON = ONTOLOGY_URI+"Person";

    static void initRepo(){
        // initiate a remote repo manager
        manager = new RemoteRepositoryManager(SERVER_URL);
        manager.init();
        // instantiate repo
        repo = manager.getRepository(REPO_ID);
        // connect to the repo
        con = repo.getConnection();
    }

    static void addSleepData(long[][] sleepData, String[] timeseries, String patientName) throws IOException {
        con.begin();
        Model model = new TreeModel();
        ValueFactory factory = SimpleValueFactory.getInstance();
        for (int i=0; i<sleepData.length; i++){
            IRI observationName = iri(ONTOLOGY_URI+"observation"+ timeseries[i] +"_for_"+ patientName);
            IRI observableProperty = iri(ONTOLOGY_URI+"sleepProp");
            Literal startTime = factory.createLiteral(timeseries[i]+"T00:00:00+00:00", XSD.DATETIME);
            Literal endTime = factory.createLiteral(timeseries[i]+"T23:59:59+00:00", XSD.DATETIME);
            IRI patient = iri(ONTOLOGY_URI+patientName);
            model.add(observationName, RDF.TYPE, iri(OBSERVATION));
            model.add(observationName, iri(OBSERVED_PROPERTY), observableProperty);
            model.add(observableProperty, RDF.TYPE, iri(SLEEP_PROPERTY));
            model.add(observationName, iri(IS_OBS_FOR), patient);
            model.add(observationName, iri(LIGHT_MINUTES), literal(sleepData[i][0]));
            model.add(observationName, iri(REM_MINUTES), literal(sleepData[i][1]));
            model.add(observationName, iri(DEEP_MINUTES), literal(sleepData[i][2]));
            model.add(observationName, iri(WAKE_MINUTES), literal(sleepData[i][3]));
            model.add(observationName, iri(ASLEEP_MINUTES), literal(sleepData[i][4]));
            model.add(observationName, iri(RESTLESS_MINUTES), literal(sleepData[i][5]));
            model.add(observationName, iri(AWAKE_MINUTES), literal(sleepData[i][6]));
            model.add(observationName, iri(MINUTES_AWAKE), literal(sleepData[i][7]));
            model.add(observationName, iri(MINUTES_ASLEEP), literal(sleepData[i][8]));
            model.add(observationName, iri(WAKE_COUNT), literal(sleepData[i][9]));
            model.add(observationName, iri(LIGHT_COUNT), literal(sleepData[i][10]));
            model.add(observationName, iri(REM_COUNT), literal(sleepData[i][11]));
            model.add(observationName, iri(DEEP_COUNT), literal(sleepData[i][12]));
            model.add(observationName, iri(START_TIME), startTime);
            model.add(observationName, iri(END_TIME), endTime);

        }
//        sleepDataValidation(model);
        con.add(model);
        con.commit();
    }

    static void addHeartRateData(long[] heartRateData, String[] timeseries, String patientName) throws IOException {
        con.begin();
        Model model = new TreeModel();
        ValueFactory factory = SimpleValueFactory.getInstance();
        for (int i=0; i<heartRateData.length; i++){
            IRI observationName = iri(ONTOLOGY_URI+"observation"+ timeseries[i] +"_for_"+ patientName);
            IRI observableProperty = iri(ONTOLOGY_URI+"heartRateProp");
            Literal resultTime = factory.createLiteral(timeseries[i], XSD.DATETIME);
            IRI patient = iri(ONTOLOGY_URI+patientName);
            model.add(observationName, RDF.TYPE, iri(OBSERVATION));
            model.add(observationName, iri(OBSERVED_PROPERTY), observableProperty);
            model.add(observableProperty, RDF.TYPE, iri(HEART_RATE_PROPERTY));
            model.add(observationName, iri(IS_OBS_FOR), patient);
            model.add(observationName, iri(HEART_RATE), literal(heartRateData[i]));
            model.add(observationName, iri(RESULT_TIME), resultTime);

        }
//        sleepDataValidation(model);
        con.add(model);
        con.commit();
    }

    static void addStepsData(long[] stepsData, String[] timeseries, String patientName) throws IOException {
        con.begin();
        Model model = new TreeModel();
        ValueFactory factory = SimpleValueFactory.getInstance();
        for (int i=0; i<stepsData.length; i++){
            IRI observationName = iri(ONTOLOGY_URI+"observation"+ timeseries[i] +"_for_"+ patientName);
            IRI observableProperty = iri(ONTOLOGY_URI+"stepProp");
            Literal resultTime = factory.createLiteral(timeseries[i], XSD.DATETIME);
            IRI patient = iri(ONTOLOGY_URI+patientName);
            model.add(observationName, RDF.TYPE, iri(OBSERVATION));
            model.add(observationName, iri(OBSERVED_PROPERTY), observableProperty);
            model.add(observableProperty, RDF.TYPE, iri(HEART_RATE_PROPERTY));
            model.add(observationName, iri(IS_OBS_FOR), patient);
            model.add(observationName, iri(NUM_OF_STEPS), literal(stepsData[i]));
            model.add(observationName, iri(RESULT_TIME), resultTime);

        }
//        sleepDataValidation(model);
        con.add(model);
        con.commit();
    }



    private static void sleepDataValidation(Model model) throws IOException {
        ShaclSail shaclSail = new ShaclSail(new MemoryStore());

        //Logger root = (Logger) LoggerFactory.getLogger(ShaclSail.class.getName());
        //root.setLevel(Level.INFO);

        //shaclSail.setLogValidationPlans(true);
        //shaclSail.setGlobalLogValidationExecution(true);
        //shaclSail.setLogValidationViolations(true);

        SailRepository sailRepository = new SailRepository(shaclSail);
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {
            connection.begin();
            connection.add(model);

            StringReader shaclRules = new StringReader(
                    String.join("\n", "",
                            "@prefix sh: <http://www.w3.org/ns/shacl#> .",
                            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
                            "@prefix pob:<http://www.semanticweb.org/patient-observations#> .",
                            "@prefix sosa: <http://www.w3.org/ns/sosa/> .",
                            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .",
                            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .",

                            "pob:PersonShape",
                            "  a sh:NodeShape  ;",
                            "  sh:targetClass foaf:Person ;",
                            "  sh:property pob:PersonShapeProperty .",

                            "pob:PersonShapeProperty ",
                            "  sh:path foaf:age ;",
                            "  sh:datatype xsd:int ;",
                            "  sh:maxCount 1 ;",
                            "  sh:minCount 1 ."
                    ));

            connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();

            connection.begin();

            StringReader invalidSampleData = new StringReader(
                    String.join("\n", "",
                            "@prefix sh: <http://www.w3.org/ns/shacl#> .",
                            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
                            "@prefix pob:<http://www.semanticweb.org/patient-observations#> .",
                            "@prefix sosa: <http://www.w3.org/ns/sosa/> .",
                            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .",
                            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .",


                            "pob:peter a foaf:Patient ;",
                            "  pob:hasMovementProblem 20, '30'^^xsd:int  ."

                    ));

            connection.add(invalidSampleData, "", RDFFormat.TURTLE);
            try {
                connection.commit();
                connection.close();
            } catch (RepositoryException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof ValidationException) {
                    Model validationReportModel = ((ValidationException) cause).validationReportAsModel();

                    Rio.write(validationReportModel, System.out, RDFFormat.TURTLE);
                }
                throw exception;
            }
        }
    }

    static void getSleepStatements(){
        try (RepositoryResult<Statement> statements = con.getStatements(null, RDF.TYPE, iri(PERSON), true)) {
            while (statements.hasNext()) {
                Statement st = statements.next();
                RepositoryResult<Statement> statements2 = con.getStatements(null, iri(IS_OBS_FOR),st.getSubject());
                while(statements2.hasNext()){
                    Statement observation = statements2.next();
                    System.out.println("Observation: "+ observation.getSubject());
                }
            }
        }
    }

//    static void getAllStatements() throws IOException {
//        try (RepositoryResult<Statement> statements = con.getStatements(null, null, null, true)) {
//            Model model = new TreeModel();
//            while (statements.hasNext()) {
//                Statement st = statements.next();
//                System.out.println(st.getObject() + ", " +st.getPredicate() + ", " + st.getObject());
//                model.add(st);
//            }
//            exportData(model);
//        }
//    }
//
//    static void exportData(Model model) throws IOException {
//        FileOutputStream out = new FileOutputStream("./Files/export-data.rdf");
//        try (out) {
//            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
//            RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
//            rdfParser.setRDFHandler(writer);
//            rdfParser.getParserConfig().set(BasicParserSettings.VERIFY_URI_SYNTAX, false);
//            rdfParser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
//
//            writer.startRDF();
//            for (Statement st : model) {
//                writer.handleStatement(st);
//
//            }
//            writer.endRDF();
//        } catch (RDFHandlerException e) {
//            // oh no, do something!
//        }
//    }

    static void closeConn(){ con.close(); }
    static String getSERVER_URL() { return SERVER_URL; }
    String getREPO_ID() { return  REPO_ID; }
    static RemoteRepositoryManager getManager() { return manager; }
    static Repository getRepo() { return repo; }
    static RepositoryConnection getRepoConnection() { return con; }

}
