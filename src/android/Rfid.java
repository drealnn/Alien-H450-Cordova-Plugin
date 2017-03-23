package com.alienPlugin.app.myRfid;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.util.Log;
import android.view.KeyEvent;

import android.view.View;

import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alien.common.KeyCode;
import com.alien.rfid.Bank;
import com.alien.rfid.InvalidParamException;
import com.alien.rfid.Mask;
import com.alien.rfid.RFID;
import com.alien.rfid.RFIDCallback;
import com.alien.rfid.RFIDReader;
import com.alien.rfid.RFIDResult;
import com.alien.rfid.ReaderException;
import com.alien.rfid.Tag;

import java.util.ArrayList;
import java.util.HashMap;


public class Rfid extends CordovaPlugin {


    private CallbackContext keyup_callback = null;
	private CallbackContext keydown_callback = null;
	private CallbackContext onReaderReadTag_callback = null;
    private SoundPool mSound;
    int mSuccess, mFail, mBeep;

    private View currentView = null;
    private String TAG = "ALIEN Native";
	
	protected RFIDReader mReader;
//Context context=this.cordova.getActivity().getApplicationContext();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);

        if(action.equalsIgnoreCase("open_reader")){
            try {
			// initialize RFID interface and obtain a global RFID Reader instance
				mReader = RFID.open();
				callbackContext.success("successfully initialized RFID device");
			}
			catch(ReaderException e) {
				Log.e(TAG, "RFID init failed: " + e);
				callbackContext.error("RFID init failed: " + e);
			}
            return true;
        }
		else if (action.equalsIgnoreCase("close_reader")){		
			// initialize RFID interface and obtain a global RFID Reader instance
			if (mReader != null){
				mReader.close();
			}
			callbackContext.success("successfully closed RFID device");
			
            return true;
		}
		
		// getters and setters //
		
		 else if (action.equals("getPower")){
			Log.d(TAG, "++Get Power");
			try {
				callbackContext.success("" + mReader.getPower());
			}catch (ReaderException e)
			{
				callbackContext.error("failed to get power");
			}
			Log.d(TAG, "--Get Power");

			return true;
		}
		
		 else if (action.equals("setPower")){
			Log.d(TAG, "++set power level");
			try {
				mReader.setPower(args.getInt(0));
				callbackContext.success("successfully set power level");
			} catch (ReaderException e) {
				Log.e(TAG, "ERROR: " + e);
				callbackContext.error("ERROR: " + e);
			}

			Log.d(TAG, "--set power level");

			return true;
		}
		
		// Reading and Writing //
		else if (action.equals("start_readContinuous")){
			if (mReader == null){ 
				callbackContext.error("Reader is not instantiated");
				Log.e(TAG, "Reader is not instantiated");
				return true;
			}
			try {
				// start continuous inventory
				// create a callback to receive tag data
				mReader.inventory(new RFIDCallback() {
					@Override
					public void onTagRead(Tag tag) {
						Tag newtag = tag;
						String str = "{\'tag\':\'" + newtag.getEPC() + "\' , \'rssi\': \'" + newtag.getRSSI() + "\'}";
						PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(str));
						result.setKeepCallback(true);
						this.onReaderReadTag_callback.sendPluginResult(result);
						return;
					}
				});
			}
			catch (ReaderException e) {
				Log.e(TAG, "ERROR: " + e);
				callbackContext.error("ERROR: " + e);
			}
			
			return true;
		}
		else if (action.equals("start_readSingle"))
		{	
			if (mReader == null){ 
				callbackContext.error("Reader is not instantiated");
				Log.e(TAG, "Reader is not instantiated");
				return true;
			}
			try {
				// Read a single tag and add it to the list
				RFIDResult readResult = mReader.read();
				if (!readResult.isSuccess()) {
					callbackContext.error("No tags found");
					Log.e(TAG, "No tags found");
					return true;
				}
				Tag newtag = ((Tag)readResult.getData());
				String str = "{\'tag\':\'" + newtag.getEPC() + "\' , \'rssi\': \'" + String.valueOf(newtag.getRSSI()) + "\'}";
				callbackContext.success(new JSONObject(str));
			}
			catch (ReaderException e) {
				Log.e(TAG, "ERROR: " + e);
				callbackContext.error("ERROR: " + e);
			}

			return true;
		}
		else if (action.equals("stop_read"))
		{
			if (mReader == null || !mReader.isRunning()){
				Log.e(TAG, "Reader isn't instantiated or isn't running");
				callbackContext.error("Reader isn't instantiated or isn't running");
			}
			else {
				try {
					mReader.stop();
					callbackContext.success("Reader successfully stopped reading");
				}
				catch(ReaderException e) {
					Log.e(TAG, "ERROR: " + e);
					callbackContext.error("ERROR: " + e);
				}
			}
			
			return true;
		}
		else if (action.equals("start_writeMemory")){
			if (mReader == null){ 
				callbackContext.error("Reader is not instantiated");
				Log.e(TAG, "Reader is not instantiated");
				return true;
			}
			try {
				String data = "";
				String format = args.getString(0).toLowerCase();
				String toWrite = args.getString(1);
				switch (format){
					case("hex"):
						data = toWrite;
						break;
					case("ascii"):
						int i = 0;
						int stringLength = toWrite.length();
						for (i = 0; i < stringLength; i++){
							int asciiChar =  (int) toWrite.charAt(i);
							data += ""+String.valueOf(Integer.valueOf(asciiChar, 16));
						}
						break;
					default:
						data = null;
						break;
				}
				
				if (data != null)
					mReader.write(Bank.EPC, 2, data);
				else {
					callbackContext.error("Improper format to write function");
					Log.e(TAG, "Improper format to write function");
				}
			} catch(ReaderException e){
				Log.e(TAG, "ERROR: " + e);
				callbackContext.error("ERROR: " + e);
			}
			catch(Exception e){
				Log.e(TAG, "ERROR: " + e);
				callbackContext.error("ERROR: " + e);
			}
			
			return true;
		}
		else if (action.equalsIgnoreCase("is_running")){
			if (mReader!=null){
				callbackContext.success(mReader.isRunning());
			}
			else{
				callbackContext.success(false);
			}
			return true;
		}
		
		// Events //

		else if(action.equalsIgnoreCase("register_keyDown")){
				this.keydown_callback = callbackContext;
				return true;
		}
		else if(action.equalsIgnoreCase("register_keyUp")){
				this.keyup_callback = callbackContext;
				return true;
		}
		else if(action.equalsIgnoreCase("onReaderReadTag")){
				this.onReaderReadTag_callback = callbackContext;
				return true;
		}
		
        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView); 
    }

}

