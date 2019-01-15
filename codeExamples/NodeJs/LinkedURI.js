/**
 * Retrieve linked URI for a given URI
 * Or retrieve the labels for a given URI
 */
var request = require('request');
var LinkedURI = function (webserverRomediURL) {
  this.optionsLinkURI = {
    url: webserverRomediURL + "GetJSONbyIRI",
    getServiceLabel: "GetJSONlabelsByIRI",
    qs: {}
  }

  this.optionsGetLabels = {
    url: webserverRomediURL + "GetJSONlabelsByIRI",
    qs: {}
  }

  this.getJSON = function (uri, response) {
    var options = this.optionsLinkURI;
    options.qs.IRI = uri;
    // console.log(options);
    request(options, function (err, res, body) {
      if (err) {
        console.log("an error occurend with getJSON");
        console.log(err);
        return;
      }
      var jsonResult = JSON.parse(body);
      //console.log(jsonResult);
      response.send(jsonResult);
    });
  }

  this.getLabels = function (uri, response) {
    var options = this.optionsGetLabels;
    options.qs.IRI = uri;
    request(options, function (err, res, body) {
      if (err) {
        console.log("an error occurend with getLabels");
        console.log(err);
        return;
      }
      var jsonResult = JSON.parse(body);
      //console.log(jsonResult);
      response.send(jsonResult);
    });
  };
}
module.exports = LinkedURI;
