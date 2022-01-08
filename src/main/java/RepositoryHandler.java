import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
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

    static void initRepo(){
        // initiate a remote repo manager
        manager = new RemoteRepositoryManager(SERVER_URL);
        manager.init();

        // instantiate repo
        repo = manager.getRepository(REPO_ID);

        // connect to the repo
        con = repo.getConnection();

        con.begin();
        IRI bob = iri("urn:bob3");
        con.add(bob, RDF.TYPE, FOAF.PERSON);
        con.add(bob, RDFS.LABEL, literal("person named Bob"));
        con.commit();
    }
    static void closeConn(){
        con.close();
    }

    static String getSERVER_URL() { return SERVER_URL; }
    String getREPO_ID() { return  REPO_ID; }
    static RemoteRepositoryManager getManager() { return manager; }
    static Repository getRepo() { return repo; }
    static RepositoryConnection getRepoConnection() { return con; }

}
