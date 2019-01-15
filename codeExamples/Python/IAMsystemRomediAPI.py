import requests
import json
class IAMsystemRomediAPI:
    """
    An API to detect drug with an endpoint
    
    Attributes:
        url_detection (str): The url of the GetJSONdrugsDetected method
    """
    def __init__(self, url_detection, url_detection_by_type):
        """
        Constructor
        
        Parameters:
            url_detection (str): The url of the GetJSONdrugsDetected method
            url_detection_by_type (str): The url of the GetJSONdrugsDetectedByType method
        """
        self.url_detection = url_detection
        self.url_detection_by_type = url_detection_by_type
    
    def detect_drug(self, content):
        """
        
        Parameters:
            content (str): the label of a drug / a sentence 
        
        Returns:
            A Romedi instance detected with offset positions
        """
        r = requests.post(url=self.url_detection, data=content)
        content = r.content.decode()
        if (r.status_code != 200):
           print(content)
           raise ValueError("detect__drug method returned an error. Status code is {status_cde}".format(r.status_code))
        json_result = json.loads(content)
        return(json_result)
    
    def get_uri_info(self, uri, urlAPI):
        """
        
        Parameters:
            uri: a Romedi uri like http://www.romedi.fr/romedi/BNqojc85n788lv66jj23g3jfvcdhqkrogn
            urlAPI: http://www.romedi.fr:8892/RomediSPARQLAPI-0.0.1-SNAPSHOT/GetJSONbyIRI
        Returns:
            Links to this URI
        """
        getParams={"IRI":uri}
        r = requests.get(url=urlAPI,params=getParams)
        content = r.content.decode()
        if (r.status_code != 200):
           print(content)
           raise ValueError("detect__drug method returned an error. Status code is {status_cde}".format(r.status_code))
        json_result = json.loads(content)
        return(json_result)
    
    def detect_drug_by_type(self,drug_name, romedi_type):
        """
        
        Parameters:
            drug_name: the name of drug, we expect to find a BN
            romedi_type: the romedi type of the drug (BN, IN, PIN, INdosage, Dosage ...)
        Returns:
            A tuple (IRI, ) IRI detected
        """
        query_params = {"drugname":drug_name,"romeditype":romedi_type}
        r = requests.get(url=self.url_detection_by_type, params=query_params)
        content = r.content.decode()
        if (r.status_code != 200):
           print(content)
           raise ValueError("detect__drug method returned an error. Status code is {status_cde}".format(r.status_code))
        json_result = json.loads(content)
        if len(json_result) == 0:
            return ("", "")
        first_result = json_result["0"]
        return (first_result["code"],first_result["terminoLabel"])
