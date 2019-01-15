from SPARQLWrapper import SPARQLWrapper, JSON

class RomediSPARQL:
    """
    An API to query the Romedi SPARQL Endpoint
    
    Attributes:
        sparql_endpoint (str): The endpoint
        sparql (SPARQLWrapper): an instance of SPARQLWrapper
    """
    def __init__(self, sparql_endpoint):
        """
        The constructor for RomediSPARQL class
        
        Parameters:
            sparqlEndpoint (str): The endpoint
        """
        self.sparql_endpoint = sparql_endpoint
        self.sparql = SPARQLWrapper(sparql_endpoint)
        self.sparql.setReturnFormat(JSON)
        self.__try_connection()
    
    def __try_connection(self):
        """
        Send a query to test the connection
        """
        self.sparql.setQuery("ASK {?s ?p ?o }")
        try:
            results = self.sparql.query().convert()
            print("Connection successful to " + self.sparql_endpoint)
        except Exception as e:
            print("Impossible to connect to the endpoint: ")
            print(e)
    
    def send_query(self, sparql_query):
        """
        Send a sparql query to the endpoint 
        
        Parameters:
            sparql_query (str): A sparql query
        Returns:
            A JSON result (a dictionary)
        """
        self.sparql.setQuery(sparql_query)
        results = self.sparql.query().convert()
        return results
    
    def get_cis_set(self):
        """
        Get a set of all the current Romedi CIS
        
        Returns:
            A set of string
        """
        results = self.send_query("""
        PREFIX romedi:<http://www.romedi.fr/romedi/>\n
        SELECT distinct ?cis where {    \n
        ?cis  a romedi:CIS. \n }
        """)
        listeCISromedi = results['results']['bindings']
        listeCISromedi = [x['cis']['value'] for x in listeCISromedi]
        listeCISromedi = [x.replace("http://www.romedi.fr/romedi/CIS","") for x in listeCISromedi]
        return set(listeCISromedi)