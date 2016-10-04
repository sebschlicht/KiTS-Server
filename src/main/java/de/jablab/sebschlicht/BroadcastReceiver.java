package de.jablab.sebschlicht;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class BroadcastReceiver implements Runnable {

    private DatagramSocket socket;

    private ReceiverCallback callback;

    private int port;

    public BroadcastReceiver(
            ReceiverCallback callback,
            int port) {
        this.callback = callback;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.socket = new DatagramSocket(this.port,
                    InetAddress.getByName("0.0.0.0"));
            this.socket.setBroadcast(true);

            byte[] receiveBuffer = new byte[4096];
            DatagramPacket packet;
            while (true) {
                packet = new DatagramPacket(receiveBuffer,
                        receiveBuffer.length);
                System.out.println("listening on "
                        + this.socket.getLocalAddress().getHostAddress() + ":"
                        + this.socket.getLocalPort() + "...");

                this.socket.receive(packet);
                System.out.println("UDP message received");

                this.callback.handleRequest(packet);
                Arrays.fill(receiveBuffer, 0, receiveBuffer.length, (byte) 0);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean
        sendResponse(InetAddress address, int targetPort, byte[] data) {
        DatagramPacket response =
                new DatagramPacket(data, data.length, address, targetPort);
        try {
            this.socket.send(response);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface ReceiverCallback {

        void handleRequest(DatagramPacket request);

    }
}
