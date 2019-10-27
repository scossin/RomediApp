#!/bin/bash
echo "downloading the Romedi terminology..."
wget https://www.romedi.fr/download -O romedi.zip

echo "trying to unzip it"
unzip romedi.zip -d romedi

echo "removing previous ttl files in the ttl folder and placing new ttl files"
rm ttl/*.ttl
mv romedi/Romedi-*/*.ttl ttl/

echo "cleaning"
rm -R romedi/
rm romedi.zip

echo "Done"
