var request = require('request');
var DrugDetection = function (drugsDetectionURL) {
    this.description = "detect drugs in text";
    this.options = {
        uri: drugsDetectionURL,
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    };
    this.getHTMLdrugsDetected = function (sentence, response) {
        var options = this.options;
        options.body = sentence;
        request.post(options, function (err, res, body) {
            if (err) {
                console.log(err);
                return;
            }
            response.send(body);
        });
    }
};


module.exports = DrugDetection;
