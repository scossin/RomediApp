library(httr) ## send HTTP queries
library(SPARQL) ## send sparql query
library(jsonlite) ## parse JSON

## change these url for a local installation:
triplestore = "http://127.0.0.1:8889/bigdata/namespace/ROMEDI/sparql" ## blazegraph container
tomcat = "http://127.0.0.1:8892/" ## tomcat container

## online:
triplestore = "http://127.0.0.1:8889" ## blazegraph container
tomcat = "http://www.romedi.fr:8892" ## tomcat container

## iamsystem version: 
iamsystem <- "IAMsystemRomediAPI-0.0.1"
## RomediSPARQL version: 
romedisparql <- "RomediSPARQLAPI-0.0.1-SNAPSHOT"

## IAMsystemRomedi API : 
urlJSONdetection <- paste(tomcat, iamsystem, "GetJSONdrugsDetected",sep="/")

########################################### Drug Detection #####################
text <- "Traitement à l'admission: MTX, dompéridone et escitalopram et donormil"

resultsAPI <- httr::POST(url = urlJSONdetection,
           body = text)
if (resultsAPI$status_code != 200){
  cat("Error ! something went wrong, the HTTP response status code is 200")
}
resultsAPI$request
resultsJson <- fromJSON(rawToChar(resultsAPI$content))

is.list(resultsJson) ## result is a list
length(resultsJson) ## 4 drugs detected

## convert the list to a data.frame:
resultsJson <- lapply(resultsJson, data.frame)
drugsdetected <- do.call(what = "rbind",resultsJson)

colnames(drugsdetected)
# dictLabel : the label matched in the Romedy dictionary (not always the pref label)
# term : the term in the text
# terminoLabel : the pref label in the Romedi terminology
# start/end : offset position in the text
# type: romediType. BN = Brand Name ; IN = Ingredient. 
# In the current configuration, only BN and IN are available. 


## For RShiny apps, we may want to have HTML output : 
urlHTMLdetect <- paste(tomcat, iamsystem, "GetHTMLdrugsDetected",sep="/")
resultsAPI <- httr::POST(url = urlHTMLdetect,
                         body = text)
if (resultsAPI$status_code != 200){
  cat("Error ! something went wrong, the HTTP response status code is 200")
}
# to visualize the output: 
writeLines(rawToChar(resultsAPI$content),con="output.html")


##################################### Drug Info ######################
## RomediSPARQL API : 
urlRomediSPARQL <- paste(tomcat, romedisparql, "GetJSONbyIRI",sep="/")

# let's say we want information about the drugs detected:
firstLine <- drugsdetected[1,]
print(firstLine)
oneUri <- firstLine$code

#' @description Retrieve other romedi instances given a Romedi instance (uri)
#' @param uri: a RomediURI. Ex: http://www.romedi.fr/romedi/INqo4upphk692g5p56eplbjoc5j9hkkdj0
#' @param type: an available type c("IN","BN","BNdosage","CIS","ATC5","ATC4","ATC7")
getLinks <- function(uri, type){
  availableTypes <- c("IN","BN","BNdosage","CIS","ATC5","ATC4","ATC7")
  if (!type %in% availableTypes){
    stop("bad type choice, available choices are:", paste(availableTypes, collapse=","))
  }
  query = list (IRI = uri)
  resultsAPI <- httr::GET(url = urlRomediSPARQL,
                          query = query)
  resultsJson <- fromJSON(rawToChar(resultsAPI$content))
  typeSubset <- resultsJson[[type]]
  typeSubset <- lapply(typeSubset, data.frame)
  typeSubset <- do.call(what = "rbind",typeSubset)
  typeSubset$from <- uri
  return(typeSubset)
}

getLinks(uri = oneUri,type = "IN") ## ingredient
getLinks(uri = oneUri,type = "BNdosage") ## BN strength
getLinks(uri = oneUri,type = "CIS") ## drug products
getLinks(uri = oneUri,type = "ATC7") ## drug products

##################################### SPARQL endpoint   ##############################################
### 
romediEndpoint <- "http://www.romedi.fr:8890/sparql" 
romediEndpoint <- "127.0.0.1:8889/bigdata/namespace/ROMEDI/sparql"

# links to drugbank :
linksDrugbank <- "PREFIX romedi:<http://www.romedi.fr/romedi/>
prefix owl: <http://www.w3.org/2002/07/owl#>
SELECT ?drug ?drugbank ?labelDrug
WHERE { 
  ?drug  owl:sameAs   ?drugbank .  
  ?drugbank a <http://bio2rdf.org/drugbank_vocabulary:Drug> . 
  ?drug rdfs:label ?labelDrug .
}"
cat(linksDrugbank)
library(SPARQL)
resultsEndpoint <- SPARQL::SPARQL(url = romediEndpoint,query = linksDrugbank)$results

## retrieves information with the drugbank SPARQL endpoint:
drugbankEndpoint <- "http://drugbank.bio2rdf.org/sparql"
cat("Retrieve data on the SPARQL endpoint :", drugbankEndpoint, "\n")
library(SPARQL)
resultsEndpoint$drugbank[1] ## <http://bio2rdf.org/drugbank:DB00315>
query <- "select ?p ?o where {
<http://bio2rdf.org/drugbank:DB00315> ?p ?o }"
resultsDBpedia <- SPARQL::SPARQL(url = drugbankEndpoint,query = query)$results

