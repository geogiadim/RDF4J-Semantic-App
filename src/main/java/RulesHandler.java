import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class RulesHandler {
    private final RepositoryConnection con;
    private final String ONTOLOGY_URI;

    public RulesHandler(RepositoryConnection con, String ONTOLOGY_URI){
        this.con = con;
        this.ONTOLOGY_URI = ONTOLOGY_URI;
    }

    void generateLackOfMovementRule(){
        ArrayList<Value> patients = new ArrayList<>();
        ArrayList<Value> dates = new ArrayList<>();
        ArrayList<Value> rates = new ArrayList<>();
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX pob1: <http://www.semanticweb.org/patient-observations/1.0.0#> \n" +
                "PREFIX pob:<http://www.semanticweb.org/patient-observations#>\n" +
                "PREFIX sosa: <http://www.w3.org/ns/sosa/> \n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX xml: <http://www.w3.org/XML/1998/namespace> \n" +
                "\n" +
                "SELECT *\n" +
                "WHERE{\n" +
                "    ?dailyMeasurement a pob:DailyStepsMeasurement;\n" +
                "                      pob:refersToPatient ?patient;\n" +
                "                      sosa:resultTime ?date;\n" +
                "                      pob:rate ?rate;\n" +
                "  \n" +
                "    FILTER (?rate < \"500\"^^xsd:double && ?rate >\"0\"^^xsd:double )\n" +
                "}";

        TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {  // iterate over the result
                BindingSet bindingSet = result.next();
                Value valueOfX = bindingSet.getValue("patient");
                Value valueOfY = bindingSet.getValue("date");
                Value valueOfZ = bindingSet.getValue("rate");
                patients.add(valueOfX);
                dates.add(valueOfY);
                rates.add(valueOfZ);
            }
        }
        String patientName;
        String date;
        con.begin();
        for (int i=0; i < patients.toArray().length; i++) {
            patientName = patients.get(i).toString().replace(ONTOLOGY_URI, "");
            date = dates.get(i).toString().replace("^^<http://www.w3.org/2001/XMLSchema#dateTime>", "");
            date = date.substring(1, date.length() - 1);
            IRI problem = iri(ONTOLOGY_URI + "LackOfMovementProblem_" + date + "_for_" + patientName);
            String queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX pob1: <http://www.semanticweb.org/patient-observations/1.0.0#> \n" +
                    "PREFIX pob:<http://www.semanticweb.org/patient-observations#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/> \n" +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                    "PREFIX xml: <http://www.w3.org/XML/1998/namespace> \n" +
                    "\n" +
                    "CONSTRUCT{\n" +
                    "    <"+problem+"> a pob:LackOfMovement;\n" +
                    "                        pob:isMovementProblemOf <"+patients.get(i)+">;\n" +
                    "                        sosa:resultTime "+dates.get(i)+";\n" +
                    "                        pob:rate "+rates.get(i)+".\n" +
                    "}\n" +
                    "WHERE{\n" +
                    "    ?dailyMeasurement a pob:DailyStepsMeasurement;\n" +
                    "                      pob:refersToPatient <"+patients.get(i)+">;\n" +
                    "                      sosa:resultTime "+dates.get(i)+";\n" +
                    "                      pob:rate "+rates.get(i)+".\n" +
                    "  \n" +
                    "}";

            GraphQueryResult graphResult = con.prepareGraphQuery(queryString2).evaluate();
            Model resultModel = QueryResults.asModel(graphResult);
            for (Statement st : resultModel) {
                System.out.println(st.getSubject() + ", " + st.getPredicate() + ", " + st.getObject());
            }
            con.add(resultModel);
        }
        con.commit();
    }

    void generateLowHRRule(){
        ArrayList<Value> patients = new ArrayList<>();
        ArrayList<Value> dates = new ArrayList<>();
        ArrayList<Value> rates = new ArrayList<>();
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX pob1: <http://www.semanticweb.org/patient-observations/1.0.0#> \n" +
                "PREFIX pob:<http://www.semanticweb.org/patient-observations#>\n" +
                "PREFIX sosa: <http://www.w3.org/ns/sosa/> \n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX xml: <http://www.w3.org/XML/1998/namespace> \n" +
                "\n" +
                "SELECT *\n" +
                "WHERE{\n" +
                "    ?dailyMeasurement a pob:DailyHeartRateMeasurement;\n" +
                "                      pob:refersToPatient ?patient;\n" +
                "                      sosa:resultTime ?date;\n" +
                "                      pob:rate ?rate.\n" +
                "    \n" +
                "    FILTER (?rate < \"60\"^^xsd:double) .            \n" +
                "}";

        TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {  // iterate over the result
                BindingSet bindingSet = result.next();
                Value valueOfX = bindingSet.getValue("patient");
                Value valueOfY = bindingSet.getValue("date");
                Value valueOfZ = bindingSet.getValue("rate");
                patients.add(valueOfX);
                dates.add(valueOfY);
                rates.add(valueOfZ);
            }
        }
        String patientName;
        String date;
        con.begin();
        for (int i=0; i < patients.toArray().length; i++) {
            patientName = patients.get(i).toString().replace(ONTOLOGY_URI, "");
            date = dates.get(i).toString().replace("^^<http://www.w3.org/2001/XMLSchema#dateTime>", "");
            date = date.substring(1, date.length()-1);
            IRI problem = iri(ONTOLOGY_URI+"LowHeartRateProblem_"+ date +"_for_"+ patientName);
            String queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX pob1: <http://www.semanticweb.org/patient-observations/1.0.0#> \n" +
                    "PREFIX pob:<http://www.semanticweb.org/patient-observations#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/> \n" +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                    "PREFIX xml: <http://www.w3.org/XML/1998/namespace> \n" +
                    "\n" +
                    "CONSTRUCT{\n" +
                    "    <"+problem+"> a pob:LowHeartRate;\n" +
                    "                        pob:isHeartRateProblemOf <"+patients.get(i)+">;\n" +
                    "                        sosa:resultTime "+dates.get(i)+";\n" +
                    "                        pob:rate "+rates.get(i)+".\n" +
                    "}\n" +
                    "WHERE{\n" +
                    "    ?dailyMeasurement a pob:DailyHeartRateMeasurement;\n" +
                    "                      pob:refersToPatient <"+patients.get(i)+">;\n" +
                    "                      sosa:resultTime "+dates.get(i)+";\n" +
                    "                      pob:rate "+rates.get(i)+".\n" +
                    "  \n" +
                    "}";

            GraphQueryResult graphResult = con.prepareGraphQuery(queryString2).evaluate();
            Model resultModel = QueryResults.asModel(graphResult);
            for (Statement st : resultModel) {
                System.out.println(st.getSubject() + ", " + st.getPredicate() + ", " + st.getObject());
            }
            con.add(resultModel);
        }
        con.commit();
    }
}
