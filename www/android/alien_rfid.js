var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	open_reader : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'open_reader', []);
	},
	close_reader : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'close_reader', []);
	},
	getPower : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'getPower', []);
	},
	setPower : function(powerInt, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'setPower', [powerInt]);
	},
	onReaderReadTag : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'onReaderReadTag', []);
	},
	start_readTagSingle :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_readSingle', []);
	},
	start_readTagContinuous :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_readContinuous', []);
	},
	start_writeTagMemory : function(format, dataString, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_writeMemory', [format, dataString]);
	},
	stop_scan :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'stop_read', []);
	},
	isRunning :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'isRunning', []);
	}


};



