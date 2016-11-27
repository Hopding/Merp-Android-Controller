package com.hopding.merp.android;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Represents a connection to the Raspberry Pi. Enforces the Singleton Design Pattern.
 * Contains methods to: <br>
 * * Connect to the Pi. <br>
 * * Disconnect from the Pi. <br>
 * * Send PWM signals to the Pi. <br>
 */
public class RPiConnection {

/////////////////////////////// Singleton Design Pattern Setup: //////////////////////////////////////////
    /**
     * Singleton instance
     */
    private static RPiConnection rPiConnection = null;

    /**
     * Private constructor to enforce singleton design pattern.
     */
    private RPiConnection() {}

    /**
     * Returns the singleton instance of {@code RPiConnection}.
     *
     * @return static instance of {@code RPiConnection}.
     */
    public static RPiConnection getRPiConnection() {
        if(rPiConnection == null)
            rPiConnection = new RPiConnection();
        return rPiConnection;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Socket rpiSocket;
    private PrintWriter outStream;
    private InputStreamReader inStream;

    /**
     * Attempt to establish a connect with the Raspberry Pi over Internet/LAN.
     *
     * @param ip the IP of the Raspberry Pi
     * @param port the port to connect with
     * @throws ConnectException thrown if connection attempt fails
     */
    public void attemptConnection(String ip, int port) throws ConnectException {
        try {
            rpiSocket = new Socket(ip, port);
            outStream = new PrintWriter(rpiSocket.getOutputStream(), true);
            inStream = new InputStreamReader(rpiSocket.getInputStream());
        } catch(IOException e) {
            throw new ConnectException("Failed to connect to Rpi.");
        }
    }

    /**
     * Sends a PWM signal to the Raspberry Pi. The signal contains a PWM value for
     * both the left and right motors, ranging from 0 to 180 (inclusive of both). An
     * Exception will be thrown if this requirement is violated.
     *
     * @param left PWM value for left motor
     * @param right PWM value for right motor
     * @throws IllegalArgumentException if either the left or right motor PWM values are not in
     *                                  the range of 0 to 180 (inclusive of both)
     */
    public void sendPWM(int left, int right) {
        if(left < 0 || left > 180 || right < 0 || right > 180)
            throw new IllegalArgumentException(
                    "Motor PWMs must each be between 0 and 180. left == " + left + ", right == " + right);
        outStream.printf("[%d,%d]\n", left, right);
    }

    /**
     * Sends the stop signal to the Raspberry Pi. This signal sets both the left and right
     * motor's PWMs to 90.
     */
    public void sendStopPWM() {
        outStream.println("[90,90]");
    }

    /**
     * Sends the disconnect signal to the Raspberry Pi, then disconnects from the Raspberry Pi.
     */
    public void disconnect() {
        try {
            outStream.println("disconnect");
            rpiSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
