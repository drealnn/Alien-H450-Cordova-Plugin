var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	isRunning : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'isrunning', []);
	},
	start_scan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'start_scan', []);
	},
	stop_scan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'stop_scan', []);
	},
	onScan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'onscan', []);
	}
};



