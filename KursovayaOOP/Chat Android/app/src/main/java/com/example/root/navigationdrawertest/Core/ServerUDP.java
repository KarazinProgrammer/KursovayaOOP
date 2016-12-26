package com.example.root.navigationdrawertest.Core;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerUDP extends Thread{
    public static final int PORT = 4242;
    private ClientUDP clientUDP;
    private Translator translator;

    public ServerUDP(ClientUDP clientUDP, Translator translator){
        this.clientUDP = clientUDP;
        this.translator = translator;
    }
    
    public void run(){
        byte[] receiveData = new byte[1024];

        try (DatagramSocket serverUDP = new DatagramSocket(PORT)){
            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(receiveData, receiveData.length);
                serverUDP.receive(datagramPacket);

                if(isInterrupted()){
                    serverUDP.close();
                    break;
                }

                final byte[] data = datagramPacket.getData();
                final InetAddress ipAddress = datagramPacket.getAddress();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        translator.convert(data, ipAddress);
                    }
                }).start();
                
                receiveData = new byte[1024];
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientUDP.send(new byte[]{0}, InetAddress.getLocalHost());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
