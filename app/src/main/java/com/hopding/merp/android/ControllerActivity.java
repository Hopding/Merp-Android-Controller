package com.hopding.merp.android;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This Activity is displayed after the user connects to the Raspberry Pi via the ConnectActivity.
 * This Activity allows the user to control the direction and speed of the MERP robot, as well as
 * end the connection to it. Upon ending the connection by pressing the "Disconnect" button, the
 * user is returned to the ConnectActivity screen.
 */
public class ControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        final RPiConnection rPiConnection = RPiConnection.getRPiConnection();

        // The AtomicInteger class is used here for its mutability, not its "atomicity"
        AtomicInteger speed = new AtomicInteger(45);

        // Initialize the speedBar SeekBar and the speedText EditText
        SeekBar speedBar = (SeekBar) findViewById(R.id.speedBar);
        speedBar.setProgress(speed.get());
        EditText speedText = (EditText) findViewById(R.id.speedText);
        speedText.setText(String.valueOf(speed.get()));

        // When the text/value in speedText is changed
        speedText.addTextChangedListener(new TextWatcher() {

            private boolean textChangedByApp = false; // Flag to prevent infinite calls to this
                                                      // listener when the below logic sets the
                                                      // value of speedText, as opposed to the user

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!textChangedByApp) {
                    // If user removes all text from the field, then "pretend" they entered a 0
                    if(editable.toString().equals(""))
                        speed.set(0);
                    try {
                        // Update speed
                        speed.set(Integer.parseInt(editable.toString()));
                    } catch (Exception e) {
                        // Swallow any exceptions that might occur while parsing
                        // If there is a problem, we just don't change the speed value
                    }

                    // Bounds checking
                    if (speed.get() > 90)
                        speed.set(90);
                    else if (speed.get() < 0)
                        speed.set(0);

                    // Update speedBar and speedText to reflect new speed value
                    textChangedByApp = true;
                    speedText.setText(String.valueOf(speed.get()));
                    speedBar.setProgress(speed.get());

                    // Keep the cursor at the end of the field (important for when the speedBar
                    // changes the value of speedText)
                    speedText.setSelection(speedText.getText().length());
                }
                else {
                    textChangedByApp = false;
                }
            }
        });

        // When the "progress"/position of the speedBar's slider is changed
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Update speed and speedText to reflect new value
                speed.set(seekBar.getProgress());
                speedText.setText(String.valueOf(speed.get()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // When the "Disconnect" button is pressed
        Button disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener((view) -> {
            // Make sure the user really wants to disconnect, and didn't just accidentally
            // tap the button.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Disconnect?");
            alertDialogBuilder
                    .setMessage("Are you sure you want to disconnect?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        rPiConnection.disconnect(); // Disconnect from the Raspberry Pi
                        this.finish(); // Return to the Connect Activity
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // Do nothing, so will just return to the controller Activity
                    });
            alertDialogBuilder.create().show();
        });

    //////////////////////// When A Directional Control Button Is Pressed: /////////////////////////
        ImageButton upLeftBtn = (ImageButton) findViewById(R.id.upLeftBtn);
        upLeftBtn.setOnTouchListener((view, event) -> {
            upLeftBtn.requestFocus();
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(94, 180);
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton upBtn = (ImageButton) findViewById(R.id.upBtn);
        upBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                    rPiConnection.sendPWM(90 + speed.get(), 90 + speed.get());
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton upRightBtn = (ImageButton) findViewById(R.id.upRightBtn);
        upRightBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(180, 94);
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton rightBtn = (ImageButton) findViewById(R.id.rightBtn);
        rightBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(90 + speed.get(), 90 - speed.get());
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton downRightBtn = (ImageButton) findViewById(R.id.downRightBtn);
        downRightBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(0, 86);
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton downBtn = (ImageButton) findViewById(R.id.downBtn);
        downBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(90 - speed.get(), 90 - speed.get());
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });

        ImageButton downLeftBtn = (ImageButton) findViewById(R.id.downLeftBtn);
        downLeftBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(86, 0);
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });


        ImageButton leftBtn = (ImageButton) findViewById(R.id.leftBtn);
        leftBtn.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                rPiConnection.sendPWM(90 - speed.get(), 90 + speed.get());
            else if(event.getAction() == MotionEvent.ACTION_UP)
                rPiConnection.sendStopPWM();
            return false;
        });
    ////////////////////////////////////////////////////////////////////////////////////////////////

////        android:focusableInTouchMode="true"
//        speedText.clearFocus();
//        speedText.setEnabled(false);
//        speedBar.setEnabled(false);
//        disconnectBtn.setEnabled(false);
    }

    /**
     * Override the back-button/back-icon method, thus disabling it.
     * This ensures user must press "Disconnect" button to leave the
     * Controller Activity, and thus a proper disconnect with the RPi.
     */
    @Override
    public void onBackPressed() {}
}
