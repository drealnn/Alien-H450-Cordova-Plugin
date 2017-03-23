var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	isRunning : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'isRunning', []);
	},
	start_scan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'start_scan', []);
	},
	stop_scan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'stop_scan', []);
	},
	onScan : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'onScan', []);
	}
};



