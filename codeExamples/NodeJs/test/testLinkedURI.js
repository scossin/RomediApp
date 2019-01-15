// local installation : var drugsDetectionURL = "http://127.0.0.1:8892/IAMsystemRomediAPI-0.0.1/GetJSONdrugsDetected";
var webserverRomediURL = "http://127.0.0.1:8892/RomediSPARQLAPI-0.0.1-SNAPSHOT/";
var webserverRomediURL = "http://www.romedi.fr:8892/RomediSPARQLAPI-0.0.1-SNAPSHOT/";

var res = {
    send: function(linkedURI){
		for (categoryName in linkedURI) {
			// sort every category:
			oneCategory = linkedURI[categoryName];
			oneCategory.sort(function(a,b){
				a.sort(function(c,d){
					return(c.label > d.label);
				});
				b.sort(function(c,d){
					return(c.label > d.label);
				});
				return(a[0].label > b[0].label);
			});
			for (lineNumber in oneCategory) {		
				for (var i = 0; i < linkedURI[categoryName][lineNumber].length; i++) {
					var currentLabel = linkedURI[categoryName][lineNumber][i].label;
					var currentName = linkedURI[categoryName][lineNumber][i].label + ' (' + categoryName + ')';
					console.log(currentName);
				}
			}
		}
    }
}
var LinkedURI = require("../LinkedURI");
var linkedURI = new LinkedURI(webserverRomediURL);
linkedURI.getJSON("http://www.romedi.fr/romedi/BNvsuba5bn60ta5t3qog3ar1i11bo3n81f",res);

