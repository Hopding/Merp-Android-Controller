package com.hopding.merp.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.ConnectException;

/**
 * This activity is displayed immediately upon opening the app. It allows the user to specify an
 * IP to which they wish to connect (the IP of the robot). If a connection to the IP is successfully
 * established, then the user is brought to the ControllerActivity screen. If the user ends the
 * connection from the ControllerActivity screen, then they are brought back to this Activity.
 */
public class ConnectActivity extends AppCompatActivity {

    private EditText ipAddrField;
    private RPiConnection rPiConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        ipAddrField = (EditText) findViewById(R.id.ipAddrField);

        Button connectBtn = (Button) findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener((view) -> {
            // Input IP is valid
            if(IPAddressValidator.validate(ipAddrField.getText().toString())) {
                new ConnectToRPi().execute(ipAddrField.getText().toString());
                Toast.makeText(getApplicationContext(),
                        "Trying to connect to " + ipAddrField.getText().toString(),
                        Toast.LENGTH_LONG
                ).show();
            }
            // Input IP is NOT valid
            else {
                Context context = getApplicationContext();
                CharSequence text = "IP Address is Invalid! Please try again.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    /**
     * Inner Class used to establish a connection with the Raspberry Pi in a non-ui thread.
     * Depending upon whether or not a connection was successfully established, the
     * ControllerActivity will be shown or an error Toast will appear.
     */
    private class ConnectToRPi extends AsyncTask<String, Void, Void> {

        /**
         * Attempts to form a connection with the specified IP address.
         *
         * @param params the IP address to connect with.
         * @return
         */
        @Override
        protected Void doInBackground(String... params) {
            Looper.prepare(); // Need this to use Toast in the catch{} block
            rPiConnection = RPiConnection.getRPiConnection();
            try {
                rPiConnection.attemptConnection(params[0], 12345);
                Intent intent = new Intent(ConnectActivity.this, ControllerActivity.class);
                ConnectActivity.this.startActivity(intent);
            }
            catch(ConnectException e) { // Connection was unsuccessful
                Context context = getApplicationContext();
                CharSequence text = "Failed to Connect with Raspberry Pi";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            return null;
        }

    }
}
