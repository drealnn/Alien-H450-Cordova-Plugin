package com.alienPlugin.app.mybarcode;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle; 
import android.os.PowerManager;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alien.barcode.BarcodeCallback;
import com.alien.barcode.BarcodeReader;


public class Barcode extends CordovaPlugin {

	private static final String TAG = "Alien Native"; 
	private Vibrator mVibrator;
	private CallbackContext onScan_callback = null;
	BarcodeReader mScanner = null;
	//Context context=this.cordova.getActivity().getApplicationContext();

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
		result.setKeepCallback(true);

		if (action.equalsIgnoreCase("isRunning")){
			if (mScanner == null){
				callbackContext.error("Device not initialized");
			}
			else {
				Log.i(TAG, "++Checking if scanner is running");
				callbackContext.success(mScanner.isRunning());
				Log.i(TAG, "--Checking if scanner is running");
			}
			return true;
		}
		else if (action.equalsIgnoreCase("start_scan")){
			if (mScanner == null){
				callbackContext.error("Scanner is not instantiated");
				Log.e(TAG, "Scanner is not instantiated");
				return true;
			}
			else {
				mScanner.start(new BarcodeCallback(){
					@Override
					public void onBarcodeRead(String barcode){
						PluginResult result = new PluginResult(PluginResult.Status.OK, barcode);
						result.setKeepCallback(true);
						this.onScan_callback.sendPluginResult(result);
						return;
					}
				});
			}
			
			return true;
		}
		else if (action.equalsIgnoreCase("stop_scan")){
			if (mScanner == null){
				callbackContext.error("Scanner is not instantiated");
				Log.e(TAG, "Scanner is not instantiated");
				return true;
			}
			
			mScanner.stop();
			return true;
		}
		else if (action.equalsIgnoreCase("onScan")){
			this.onScan_callback = callbackContext;
		}
		return false;
	}
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Context ctx = cordova.getActivity().getApplicationContext();
		if ((this.mScanner = new BarcodeReader(ctx)) == null) {
				Log.e(TAG, "Failure to find scanning device. Aborting...");
				return;
		}

		Log.i(TAG, "Scanning device initialized");
	}

}

