

public class Main {
    public static void main(String[] args){
        RepositoryHandler handler = new RepositoryHandler();







//
//        // Instantiate a repository graph model
//        TreeModel graph = new TreeModel();
//
//        // Read repository configuration file
//        InputStream config = EmbeddedGraphDB.class.getResourceAsStream("/repo-defaults.ttl");
//        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
//        rdfParser.setRDFHandler(new StatementCollector(graph));
//        rdfParser.parse(config, RepositoryConfigSchema.NAMESPACE);
//        config.close();
//
//
//
//        // Retrieve the repository node as a resource
//        Resource repositoryNode = GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);
//
//        // Create a repository configuration object and add it to the repositoryManager
//        RepositoryConfig repositoryConfig = RepositoryConfig.create(graph, repositoryNode);
//        repositoryManager.addRepositoryConfig(repositoryConfig);
//
//        // Get the repository from repository manager, note the repository id set in configuration .ttl file
//        Repository repository = repositoryManager.getRepository("graphdb-repo");
//
//        // Open a connection to this repository
//        RepositoryConnection repositoryConnection = repository.getConnection();
//
//        // ... use the repository
//
//        // Shutdown connection, repository and manager
//        repositoryConnection.close();
//        repository.shutDown();
//        repositoryManager.shutDown();
    }
}
