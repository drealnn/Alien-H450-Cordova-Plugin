var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	scanner_handle_keycode : 139,
	onKeyUp : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'register_keyUp', []);
	},
	onKeyDown : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'register_keyDown', []);
	},
	onTriggerDown : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'onTriggerDown', []);
	},
	onTriggerUp : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'onTriggerUp', []);
	},
	getTriggerKeyCode : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'getTriggerKeyCode', []);
	},
	
	playSound : function(soundName, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Alien", 'playSound', [soundName]);
	}
}