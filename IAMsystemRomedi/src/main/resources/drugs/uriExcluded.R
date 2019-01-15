#########################  Excluded some Romedi instances that have an ambiguous label

## retrieve all : 
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

localURL <- "127.0.0.1:8889/bigdata/namespace/ROMEDI/sparql"
romediEndpoint <- "http://www.romedi.fr:8890/sparql"

results <- SPARQL::SPARQL(url=romediEndpoint,
               query = sparqlQueryAllLabels)
resultsAllLabels <- results$results

### word to exclude : 
bool <- grepl("huile",resultsAllLabels$label,ignore.case = T)
sum(bool)

getURItoExcludeContain <- function(resultsAllLabels, word){
  bool <- grepl(word,resultsAllLabels$label,ignore.case = T)
  cat(sum(bool), " ", word, " found\n")
  uriToExclude <- subset(resultsAllLabels, bool)
  return(uriToExclude)
}

getURItoExcludeExact <- function(resultsAllLabels, word){
  bool <- resultsAllLabels$label %in% c(tolower(word), toupper(word))
  cat(sum(bool), " ", word, " found\n")
  uriToExclude <- subset(resultsAllLabels, bool)
  return(uriToExclude)
}
wordExcludedContain <- c("huile","baume","feuille","avoine","cuivre","soja","nickel","eau",
                  "myrtille","vanille","olivier","iris","angélique","artichaut","argent","fruit","levure",
                  " vert", "vigne","menthe poi","morue","opium")

wordExcludedExact <- c("or","art","iris","L107","para","quietude","soufre","zinc",
                       "A 313","talc","malt","fructose","alcool","OMÉGA-3",
                       "tisane","romarin","avocat","avoine","alli","chêne","sapin",
                       "raisin","mag","urée","choléstérol","progestérone","magnésium",
                       "thais","diane","oxygène","thym","any","oranger","fer",
                       "progesterone","glucose","magnesium","lavande","réglisse","cassis",
                       "mauve","daily","item","aluminium","gingembre","muscade","chrome","fluor",
                       "pin","résine","rhubarbe","cristal","sodium","eucalyptus","capillaire","muse",
                       "tilleul","ginseng","selenium","sélénium","prêle","evra","belladone","girofle",
                       "camphre","phosphore","manganèse","manganese","titane","hec","ortie","aubépine",
                       "silicium","paraffine","milieu de culture","ether","excipient","ultralevure",
                       "bicarbonate de sodium","frêne","houblon","thuya","ringer","boldo","graphite","opium")

## examples : 
getURItoExcludeContain(resultsAllLabels, "morue")
getURItoExcludeExact(resultsAllLabels, "opium")

uriContainExcluded <- NULL
for (word in wordExcludedContain){
  result <- getURItoExcludeContain(resultsAllLabels,word)
  uriContainExcluded <- rbind(uriContainExcluded,result)
}

uriExactExcluded <- NULL 
for (word in wordExcludedExact){
  result <- getURItoExcludeExact(resultsAllLabels,word)
  uriExactExcluded <- rbind(uriExactExcluded,result)
}
allExcluded <- rbind(uriContainExcluded, uriExactExcluded)
allExcluded <- unique(allExcluded)
allExcluded$instance <- gsub("<|>","",allExcluded$instance)
writeLines(text = allExcluded$instance,con = "uriExcluded.txt")
