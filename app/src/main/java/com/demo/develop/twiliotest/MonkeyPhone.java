package com.demo.develop.twiliotest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.twilio.client.Connection;
import com.twilio.client.Device;
import com.twilio.client.DeviceListener;
import com.twilio.client.PresenceEvent;
import com.twilio.client.Twilio;

import java.util.HashMap;
import java.util.Map;

public class MonkeyPhone implements Twilio.InitListener, DeviceListener
{
    private static final String TAG = "MonkeyPhone";

    private Context context;

    private Device device;

    private Connection connection;

    public MonkeyPhone(Context context)
    {
        this.context = context;
        Twilio.initialize(context, this /* Twilio.InitListener */);
    }

    /* Twilio.InitListener method */
    @Override
    public void onInitialized()
    {
        Log.d(TAG, "Twilio SDK is ready");

        new RetrieveCapabilityToken().execute("https://callapp.herokuapp.com/token?client=");

    }

    private void fooMethod() {
        Intent intent = new Intent(context, HelloMonkeyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        device.setIncomingIntent(pendingIntent);
    }

    private class RetrieveCapabilityToken extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try{
                boolean isEmulator = Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("google_sdk");
                String clientName = isEmulator ? "tommy" : "jenny";

                String capabilityToken = HttpHelper.httpGet(params[0] + clientName);
                return capabilityToken;
            } catch( Exception e ){
                Log.e(TAG, "Failed to obtain capability token: " + e.getLocalizedMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String capabilityToken ){
            MonkeyPhone.this.setCapabilityToken(capabilityToken);
        }
    }

    protected void setCapabilityToken(String capabilityToken){
        device = Twilio.createDevice(capabilityToken, null /* DeviceListener */);
        fooMethod();
    }

    /* Twilio.InitListener method */
    @Override
    public void onError(Exception e)
    {
        Log.e(TAG, "Twilio SDK couldn't start: " + e.getLocalizedMessage());
    }

    @Override
    protected void finalize()
    {
        if (device != null)
            device.release();
    }

    public void connect(String phoneNumber)
    {
//        Map<String, String> parameters = new HashMap<>();
//        parameters.put("To", phoneNumber);
//        connection = device.connect(parameters, null /* ConnectionListener */);
//        if (connection == null)
//            Log.w(TAG, "Failed to create new connection");
    }

    public void disconnect()
    {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    @Override
    public void onStartListening(Device device) {
        Log.i(TAG, "Device is now listening for incoming connections");
    }

    @Override
    public void onStopListening(Device device) {
        Log.i(TAG, "Device is no longer listening for incoming connections");
    }

    @Override
    public void onStopListening(Device device, int inErrorCode, String inErrorMessage) {
        Log.i(TAG, "Device is no longer listening for incoming connections due to error " +
                inErrorCode + ": " + inErrorMessage);
    }

    @Override
    public boolean receivePresenceEvents(Device device) {
        return false;
    }

    @Override
    public void onPresenceChanged(Device device, PresenceEvent presenceEvent) {

    }

    public void handleIncomingConnection(Device inDevice, Connection inConnection)
    {
        Log.i(TAG, "Device received incoming connection");
        if (connection != null)
            connection.disconnect();
        connection = inConnection;
        connection.accept();
    }

}