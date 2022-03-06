import org.eclipse.rdf4j.exceptions.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import java.io.*;


public class ShaclRepo {
    private static final String PREFIXES = "@prefix pob:<http://www.semanticweb.org/patient-observations#> .\n" +
            "@prefix sh: <http://www.w3.org/ns/shacl#> .\n" +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix sosa: <http://www.w3.org/ns/sosa/>.\n";
    private static final String PATIENT_SHAPE = PREFIXES +
            "pob:PatientShape\n" +
            "  a sh:NodeShape ;\n" +
            "  sh:targetClass sosa:Observation ;\n" +
            "  sh:property [ sh:path pob:isObservationFor ; sh:maxCount 1 ; sh:minCount 1 ;].";
    private static final String DATE_SHAPE = PREFIXES +
            "pob:DateShape\n" +
            "  a sh:NodeShape ;\n" +
            "  sh:targetClass sosa:Observation ;\n" +
            "  sh:property [ sh:path pob:startTime ; sh:datatype xsd:dateTime ;];\n" +
            "  sh:property [ sh:path pob:endTime ; sh:datatype xsd:dateTime ;];\n" +
            "  sh:property [ sh:path sosa:resultTime ; sh:datatype xsd:dateTime ;].";
    private static final String OBS_SHAPE = PREFIXES +
            "pob:ObservedPropertyShape\n" +
            "  a sh:NodeShape ;\n" +
            "  sh:targetClass sosa:Observation ;\n" +
            "  sh:property [ sh:path sosa:observedProperty ; sh:or (" +
            "       [sh:class pob:StepProperty] " +
            "       [sh:class pob:SleepProperty] " +
            "       [sh:class pob:HeartRateProperty]); " +
            "  ].";


    private final SailRepositoryConnection connection;
    private boolean validation;
    private Model validationReportModel;

    public ShaclRepo(){
        ShaclSail shaclSail = new ShaclSail(new MemoryStore());
        SailRepository sailRepository = new SailRepository(shaclSail);
        sailRepository.init();
        validation = true;
        connection = sailRepository.getConnection();
    }

    public void loadShaclRules() throws IOException {
        // add shacl shape
        connection.begin();
        StringReader shaclRules = new StringReader(String.join(DATE_SHAPE, PATIENT_SHAPE, OBS_SHAPE));
        connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
        connection.commit();
    }

    public void executeShaclValidation(Model model){
        //load data
        connection.begin();
        connection.add(model);
        validation = true;

        try {
            connection.commit();
        } catch (RepositoryException exception) {
            validation = false;
            Throwable cause = exception.getCause();
            if (cause instanceof ValidationException) {
                validationReportModel = ((ValidationException) cause).validationReportAsModel();
                Rio.write(validationReportModel, System.out, RDFFormat.TURTLE);
            }
        }
    }

    public void closeConnection() { connection.close(); }
    public boolean getValidation(){ return validation; }
    public Model getValidationReportModel() { return validationReportModel; }
    public SailRepositoryConnection getConnection (){ return connection; }

}
