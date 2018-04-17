package com.alienPlugin.app.aliengen;

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
import com.filelabel.pro.ALR_H40_CordovaWebViewImpl;
import com.filelabel.pro.CustomWebViewKeyListener;

import java.util.ArrayList;
import java.util.HashMap;


public class Alien extends CordovaPlugin {


    private CallbackContext keyup_callback = null;
    private CallbackContext keydown_callback = null;
	private CallbackContext triggerup_callback = null;
	private CallbackContext triggerdown_callback = null;
    private SoundPool mSound;
    int mSuccess, mFail, mBeep;

    private View currentView = null;
    private String TAG = "ALIEN Native";

//Context context=this.cordova.getActivity().getApplicationContext();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);

        // Events //

        if(action.equalsIgnoreCase("register_keyDown")){
            this.keydown_callback = callbackContext;
            return true;
        }
        else if(action.equalsIgnoreCase("register_keyUp")){
            this.keyup_callback = callbackContext;
            return true;
        }
		else if(action.equalsIgnoreCase("onTriggerDown")){
			this.triggerdown_callback = callbackContext;
			return true;
		}
		else if(action.equalsIgnoreCase("onTriggerUp")){
			this.triggerup_callback = callbackContext;
			return true;
		}
		else if (action.equalsIgnoreCase("getTriggerKeyCode")){
			callbackContext.success(KeyCode.ALR_H450.SCAN);
			return true;
		}
        else if(action.equalsIgnoreCase("playSound"))
        {
            this.playSound(args.getString(0), callbackContext);
            return true;
        }
        return false;
    }

 @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.i(TAG, "Alien general Initialized");
        //Context ctx = cordova.getActivity().getApplicationContext();
        mSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        Context ctx  = cordova.getContext();
        int successResource = ctx.getResources().getIdentifier("success", "raw", ctx.getPackageName());
        int failResource =  ctx.getResources().getIdentifier("fail", "raw", ctx.getPackageName());
        int beepResource = ctx.getResources().getIdentifier("beep", "raw", ctx.getPackageName());

        mSuccess = mSound.load(ctx, successResource, 1);
        mFail = mSound.load(ctx, failResource, 1);
        mBeep = mSound.load(ctx, beepResource, 1);
		
		try {
			Log.i(TAG, "getting view from webview");
			if (webView instanceof ALR_H40_CordovaWebViewImpl) {
                ((ALR_H40_CordovaWebViewImpl) webView).setCustomKeyListener(new CustomWebViewKeyListener() {
                          @Override
                          public void onKey(KeyEvent event) {
                              Log.i(TAG, "Calling the onkey event");
                              doKey(null, -1, event);
                          }
                      }
                );
            } else {
                this.currentView = webView.getView();
                this.currentView.setOnKeyListener(
                        new View.OnKeyListener(){
                            @Override
                            public boolean onKey(View view, int keyCode, KeyEvent event){
                                //boolean val = super.onKey(view, keyCode, event);
                                Log.i(TAG, "Calling the onkey event");
                                return doKey(view, keyCode, event);
                            }
                        }
                );
            }
		} catch(Exception e){
			Log.e(TAG, "Error: "+e);
		}

    }



// ####### Key event functions

    public boolean doKey(View v, int keyCode, KeyEvent event) {
        int keycode = keyCode > -1 ? keyCode : event.getKeyCode();
        Log.i(TAG, "triggering key event");
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return KeyUp(keycode, event);
        }
        else if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return KeyDown(keycode, event);
        }
        return false;
    }

    private boolean KeyDown(int keyCode, KeyEvent event){
        //if(keydown_callback == null){
        //  return true;
        //}
        //PluginResult result = new PluginResult(PluginResult.Status.OK, Integer.toString(keyCode));
        //result.setKeepCallback(true);
        //keydown_callback.sendPluginResult(result);

        Log.e(TAG, "Key down in native");
        if (this.keydown_callback == null && this.triggerdown_callback == null)
            return false;

        try {
			if (this.keydown_callback != null){
				String str = String.format("{\'keyCode\': \'%s\', \'repeatCount\' : \'%s\' }", keyCode + "", event.getRepeatCount() + "");
				PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(str));
				result.setKeepCallback(true);
				this.keydown_callback.sendPluginResult(result);
			}
			if (this.triggerdown_callback != null){
				PluginResult triggerResult;
				if (keyCode == KeyCode.ALR_H450.SCAN && event.getRepeatCount() == 0){
					triggerResult = new PluginResult(PluginResult.Status.OK, true);
					triggerResult.setKeepCallback(true);
					this.triggerdown_callback.sendPluginResult(triggerResult);
				}
			}
        } catch(Exception e)
        {
            e.printStackTrace();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling key event");
            result.setKeepCallback(true);
			
			if (this.keydown_callback != null)
				this.keydown_callback.sendPluginResult(result);
			if (this.triggerdown_callback != null)
				this.triggerdown_callback.sendPluginResult(result);
			
           
        }
		return false;
    }

    private boolean KeyUp(int keyCode, KeyEvent event){
        //if(keyup_callback == null){
        //  return true;
        //}
        //PluginResult result = new PluginResult(PluginResult.Status.OK, Integer.toString(keyCode));
        //result.setKeepCallback(true);
        //keyup_callback.sendPluginResult(result);
        if (this.keyup_callback == null && this.triggerup_callback == null)
            return false;

        try {
			if (this.keyup_callback != null){
				String str = String.format("{\'keyCode\': \'%s\', \'repeatCount\' : \'%s\' }", keyCode + "", event.getRepeatCount() + "");
				PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(str));
				result.setKeepCallback(true);
				this.keyup_callback.sendPluginResult(result);
			}
			if (this.triggerup_callback != null){
				PluginResult triggerResult;
				if (keyCode == KeyCode.ALR_H450.SCAN && event.getRepeatCount() == 0){
					triggerResult = new PluginResult(PluginResult.Status.OK, true);
					triggerResult.setKeepCallback(true);
					this.triggerup_callback.sendPluginResult(triggerResult);
				}
			}
			
   
        } catch(Exception e)
        {
            e.printStackTrace();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling key event");
            result.setKeepCallback(true);
            
			if (this.keyup_callback != null)
				this.keyup_callback.sendPluginResult(result);
			if (this.triggerup_callback != null)
				this.triggerup_callback.sendPluginResult(result);
            
        }
		
		return false;

    }

    private void playSound(String soundName, CallbackContext callbackContext)
    {
        switch(soundName){
            case("success"):
                playSuccess();
                break;
            case("fail"):
                playFail();
                break;
            case("beep"):
                playBeep();
                break;
            default:
                callbackContext.error("Can't find provided sound for playSound");
                return;
                

        }
        callbackContext.success("Initiating sound");
    }

    protected void playSuccess() {
        //mSound.playSuccess();
        mSound.play(mSuccess, 1, 1, 0, 0, 1);
    }

    protected void playFail() {
        //mSound.playFail();
        mSound.play(mFail, 1, 1, 0, 0, 1);
    }

    protected void playBeep() {
        mSound.play(mBeep, 1, 1, 0, 0, 1);
    }

}

