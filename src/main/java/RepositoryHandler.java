import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final String REM_MINUTES = "http://www.semanticweb.org/patient-observations#remMinutes";
    private static final String LIGHT_MINUTES = "http://www.semanticweb.org/patient-observations#lightMinutes";
    private static final String DEEP_MINUTES = "http://www.semanticweb.org/patient-observations#deepMinutes";
    private static final String WAKE_MINUTES = "http://www.semanticweb.org/patient-observations#wakeMinutes";
    private static final String ASLEEP_MINUTES = "http://www.semanticweb.org/patient-observations#asleepMinutes";
    private static final String RESTLESS_MINUTES = "http://www.semanticweb.org/patient-observations#restlessMinutes";
    private static final String AWAKE_MINUTES = "http://www.semanticweb.org/patient-observations#awakeMinutes";
    private static final String MINUTES_AWAKE = "http://www.semanticweb.org/patient-observations#minutesAwake";
    private static final String MINUTES_ASLEEP = "http://www.semanticweb.org/patient-observations#minutesAsleep";
    private static final String WAKE_COUNT = "http://www.semanticweb.org/patient-observations#wakeCount";
    private static final String LIGHT_COUNT = "http://www.semanticweb.org/patient-observations#lghtCount";
    private static final String REM_COUNT = "http://www.semanticweb.org/patient-observations#remCount";
    private static final String DEEP_COUNT = "http://www.semanticweb.org/patient-observations#deepCount";

    private static final String OBSERVED_PROPERTY = "http://www.w3.org/ns/sosa/observedProperty";
    private static final String IS_OBS_FOR = "http://www.semanticweb.org/patient-observations#isObservationFor";
    // time properties
    private static final String START_TIME = "http://www.semanticweb.org/patient-observations#startTime";
    private static final String END_TIME = "http://www.semanticweb.org/patient-observations#endTime";
    // classes
    private static final String OBSERVATION = "http://www.w3.org/ns/sosa/Observation";
    private static final String SLEEP_PROPERTY = "http://www.semanticweb.org/patient-observations#SleepProperty";
    private static final String PERSON = "http://www.semanticweb.org/patient-observations#Person";

    static void initRepo(){
        // initiate a remote repo manager
        manager = new RemoteRepositoryManager(SERVER_URL);
        manager.init();
        // instantiate repo
        repo = manager.getRepository(REPO_ID);
        // connect to the repo
        con = repo.getConnection();
    }

    static void addSleepData(long[][] sleepData, String[] timeseries, String patientName){
        con.begin();
        Model model = new TreeModel();
        ValueFactory factory = SimpleValueFactory.getInstance();
        for (int i=0; i<sleepData.length; i++){
            IRI observationName = iri(ONTOLOGY_URI+"observation"+ timeseries[i] +"_for_"+ patientName);
            IRI observableProperty = iri(ONTOLOGY_URI+"sleepProp");
            Literal startTime = literal(timeseries[i]+"T00:00:00+00:00");
            Literal endTime = literal(timeseries[i]+"T23:59:59+00:00");
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
        con.add(model);
        con.commit();
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
