import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
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
    // sleep properties
    private static final String REM_MINUTES = "http://www.semanticweb.org/patient-observations#remMinutes";
    private static final String LIGHT_MINUTES = "http://www.semanticweb.org/patient-observations#lightMinutes";
    private static final String DEEP_MINUTES = "http://www.semanticweb.org/patient-observations#deepMinutes";
    private static final String MINUTES_ASLEEP = "http://www.semanticweb.org/patient-observations#minutesAsleep";
    private static final String MINUTES_AWAKE = "http://www.semanticweb.org/patient-observations#minutesAwake";
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

    static void addSleepData(long[][] sleepData, String[] timeseries){
        con.begin();
        for (int i=0; i<sleepData.length; i++){
            IRI observationName = iri("pob:observation"+i);
            IRI observableProperty = iri("pob:sleepProp"+i);
            Literal startTime = literal(timeseries[i]+"T00:00:00+00:00");
            Literal endTime = literal(timeseries[i]+"T23:59:59+00:00");
            IRI patient = iri("http://www.semanticweb.org/patient-observations#TMS6");
            con.add(observationName, RDF.TYPE, iri(OBSERVATION));
            con.add(observationName, iri(OBSERVED_PROPERTY), observableProperty);
            con.add(observableProperty, RDF.TYPE, iri(SLEEP_PROPERTY));
            con.add(observationName, iri(IS_OBS_FOR), patient);
            con.add(observationName, iri(LIGHT_MINUTES), literal(sleepData[i][0]));
            con.add(observationName, iri(REM_MINUTES), literal(sleepData[i][1]));
            con.add(observationName, iri(DEEP_MINUTES), literal(sleepData[i][2]));
            con.add(observationName, iri(MINUTES_AWAKE), literal(sleepData[i][3]));
            con.add(observationName, iri(MINUTES_ASLEEP), literal(sleepData[i][4]));
            con.add(observationName, iri(START_TIME), startTime);
            con.add(observationName, iri(END_TIME), endTime);

        }
        con.commit();
    }

    static void getSleepStatements(){
//        IRI patient = iri("http://www.semanticweb.org/patient-observations#TMS6");
//        try (RepositoryResult<Statement> statements = con.getStatements(patient, null, null, true)) {
//            while (statements.hasNext()) {
//                Statement st = statements.next();
//                System.out.println(st.getObject());
//                System.out.println("ssds");
//            }
//        }
    }

    static void closeConn(){ con.close(); }
    static String getSERVER_URL() { return SERVER_URL; }
    String getREPO_ID() { return  REPO_ID; }
    static RemoteRepositoryManager getManager() { return manager; }
    static Repository getRepo() { return repo; }
    static RepositoryConnection getRepoConnection() { return con; }

}
