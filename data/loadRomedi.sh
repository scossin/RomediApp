#!/bin/bash
JETTY_PORT=8889

LOAD_PROP_FILE=/tmp/$$.properties

## See /usr/local/properties.xml file too

cat <<EOT >> $LOAD_PROP_FILE
quiet=false
verbose=0
closure=false
durableQueues=true
#Needed for quads
#defaultGraph=
com.bigdata.rdf.store.DataLoader.flush=false
com.bigdata.rdf.store.DataLoader.bufferCapacity=100000
com.bigdata.rdf.store.DataLoader.queueCapacity=10
#Namespace to load
namespace=ROMEDI
#Files to load
fileOrDirs=/data/ttl
#Property file (if creating a new namespace)
propertyFile=/data/properties.xml
EOT

echo "Loading RDF files in Blazegraph with the following properties :"

cat $LOAD_PROP_FILE

echo "
 Trying to delete a previous Romedi Terminology at : http://localhost:${JETTY_PORT}/bigdata/namespace/ROMEDI/
 "


## Clear previous terminology if it exists:
curl -X POST http://localhost:${JETTY_PORT}/bigdata/namespace/ROMEDI/update --data-urlencode 'update=CLEAR DEFAULT' -H 'Accept:appliation/sparql-results+json'

echo "
 The first time, a DataSetNotFoundException appears because the script tries to delete previous data in the ROMEDI namespace, this is normal 
 "


echo "
 Trying to load the terminology : http://localhost:${JETTY_PORT}/bigdata/dataloader 
 "

## Load the property file (the romedi terminology is expected in the "/data/ttl" folder 
curl -X POST --data-binary @${LOAD_PROP_FILE} --header 'Content-Type:text/plain' http://localhost:${JETTY_PORT}/bigdata/dataloader
