package back;

import front.Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerUDP extends Thread{
    public static final int PORT = 4242;
    private ClientUDP client;
    private Translator translator;

    public ServerUDP(ClientUDP client, Translator translator){
        this.client=client;
        this.translator=translator;
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

                byte[] data = datagramPacket.getData();
                InetAddress ipAddress = datagramPacket.getAddress();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        translator.convert(data,ipAddress);
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
        try {
            client.send(new byte[]{0}, InetAddress.getByName("127.0.0.1"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Server interrupted");
    }
}
