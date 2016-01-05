package com.demo.develop.twiliotest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.twilio.client.Connection;
import com.twilio.client.Device;

public class HelloMonkeyActivity extends Activity implements View.OnClickListener
{
    private MonkeyPhone phone;
    private EditText numberField;

    @Override
    public void onCreate(Bundle bundle)
    {
//                try {
//            Thread.sleep(8000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        phone = new MonkeyPhone(this);

        ImageButton dialButton = (ImageButton)findViewById(R.id.dialButton);
        dialButton.setOnClickListener(this);

        ImageButton hangupButton = (ImageButton)findViewById(R.id.hangupButton);
        hangupButton.setOnClickListener(this);

        numberField = (EditText)findViewById(R.id.numberField);
    }

    @Override
    public void onClick(View view)
    {


        if (view.getId() == R.id.dialButton)
//            phone.connect(numberField.getText().toString());
            phone.connect(numberField.getText().toString());
        else if (view.getId() == R.id.hangupButton)
            phone.disconnect();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(Device.EXTRA_DEVICE);
        Connection connection = intent.getParcelableExtra(Device.EXTRA_CONNECTION);
        if (device != null && connection != null) {
            intent.removeExtra(Device.EXTRA_DEVICE);
            intent.removeExtra(Device.EXTRA_CONNECTION);
            phone.handleIncomingConnection(device, connection);
        }
    }
}