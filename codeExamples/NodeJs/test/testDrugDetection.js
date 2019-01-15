// local installation : var drugsDetectionURL = "http://127.0.0.1:8892/IAMsystemRomediAPI-0.0.1/GetJSONdrugsDetected";
var drugsDetectionJsonURL = "http://www.romedi.fr:8892/IAMsystemRomediAPI-0.0.1/GetJSONdrugsDetected";
var drugsDetectionHTMLURL = "http://www.romedi.fr:8892/IAMsystemRomediAPI-0.0.1/GetHTMLdrugsDetected";
var resJSON = {
    send: function(data){
		console.log("\n--------------------------- JSON output... ----------------------- \n"); 
        console.log(data);
    }
}

var resHTML = {
    send: function(data){
		console.log("\n--------------------------- HTML output... ----------------------- \n"); 
        console.log(data);
    }
}

var DrugDetection = require("../DrugDetection");
var drugDetectionJSON = new DrugDetection(drugsDetectionJsonURL);
var drugDetectionHTML = new DrugDetection(drugsDetectionHTMLURL);
var sentence =  "Traitement à l'admission: MTX, dompéridone et escitalopram et donormil";
drugDetectionJSON.getHTMLdrugsDetected(sentence,resJSON);
drugDetectionHTML.getHTMLdrugsDetected(sentence,resHTML);
