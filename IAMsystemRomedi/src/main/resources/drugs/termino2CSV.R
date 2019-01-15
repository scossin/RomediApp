########################### Objective : RomediTerminology to CSV files

###################### BN and IN to CSV file :
### Retrieve BN and IN :
sparqlQueryAllLabels <- "PREFIX romedi:<http://www.romedi.fr/romedi/>\n
SELECT ?instance ?type ?label\n 
WHERE { \n 
    # type\n 
         ?instance a ?type ;\n 
                     rdfs:label ?label.\n
      Values ?type {romedi:BN romedi:IN} \n
     }"
cat(sparqlQueryAllLabels)
library(SPARQL)
## blazegraph url:
localURL <- "127.0.0.1:8889/bigdata/namespace/ROMEDI/sparql"
romediEndpoint <- "http://www.romedi.fr:8890/sparql"
results <- SPARQL::SPARQL(url=romediEndpoint,
                          query = sparqlQueryAllLabels)
resultsAllLabels <- results$results

## exclud uri excluded : 
## source("uriExcluded.R")
uriExcluded <- readLines("uriExcluded.txt")
resultsAllLabels$instance <- gsub("<|>","",resultsAllLabels$instance)
bool <- resultsAllLabels$instance %in% uriExcluded
sum(bool)
resultsAllLabels2 <- subset(resultsAllLabels, !bool)

### if BN and IN have the same label, keep the IN : 
resultsAllLabels2 <- resultsAllLabels2[rev(order(resultsAllLabels2$type)),]
library(dplyr)
## remove BN if IN exists :
resultsAllLabels3 <- resultsAllLabels2 %>% group_by(label) %>% mutate(n = n())
bool <- resultsAllLabels3$n == 2 & resultsAllLabels3$type == "<http://www.romedi.fr/romedi/BN>"
sum(bool)
resultsAllLabels3 <- subset(resultsAllLabels3, !bool)
resultsAllLabels3$n <- NULL
resultsAllLabels3 <- as.data.frame(resultsAllLabels3)
resultsAllLabels3$type <- as.factor(resultsAllLabels3$type)
levels(resultsAllLabels3$type)
levels(resultsAllLabels3$type) <- c("BN","IN")
write.table(resultsAllLabels3, "RomediBNINselection.csv",sep="\t",col.names = T, row.names = F, quote = F)

###### add typos : 
## hidden labels
hiddenLabels <- "PREFIX romedi:<http://www.romedi.fr/romedi/>\n
PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n
SELECT ?instance ?type ?label\n 
WHERE { \n 
    # type\n 
         ?instance a ?type ;\n 
                     skos:hiddenLabel ?label.\n
      Values ?type {romedi:BN romedi:IN} \n
}"
results <- SPARQL::SPARQL(url=romediEndpoint,
                          query = hiddenLabels)
hiddenResults <- results$results
hiddenResults$hidden <- T

## alt labels
altLabels <- "PREFIX romedi:<http://www.romedi.fr/romedi/>\n
PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n
SELECT ?instance ?type ?label\n
WHERE { \n
    # type\n
         ?instance a ?type ;\n
                     skos:altLabel ?label.\n
      Values ?type {romedi:BN romedi:IN} \n
     }"
results <- SPARQL::SPARQL(url=romediEndpoint,
                          query = altLabels)
altResults <- results$results
altResults$hidden <- F
hiddenAltLabels <- rbind(hiddenResults, altResults)
hiddenAltLabels$instance <- gsub("<|>","",hiddenAltLabels$instance)
hiddenAltLabels$type <- as.factor(hiddenAltLabels$type)
levels(hiddenAltLabels$type)
levels(hiddenAltLabels$type) <- c("BN","IN")
write.table(hiddenAltLabels, "hiddenAltLabels.csv",sep="\t",col.names = T, row.names = F, quote = F)
