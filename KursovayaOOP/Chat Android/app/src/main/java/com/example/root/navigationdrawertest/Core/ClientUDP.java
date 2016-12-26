package com.example.root.navigationdrawertest.Core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientUDP{

    private static final int PORT = 4242;
    private DatagramSocket datagramSocket;

    public ClientUDP() throws SocketException {
        datagramSocket = new DatagramSocket();
    }

    public void send(byte[] data, InetAddress inetAddr) {
        try {
            byte[] buf = data;
            DatagramPacket datagramPacket = new DatagramPacket(
                    buf, buf.length, inetAddr, PORT);
            datagramSocket.send(datagramPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        datagramSocket.close();
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
}
