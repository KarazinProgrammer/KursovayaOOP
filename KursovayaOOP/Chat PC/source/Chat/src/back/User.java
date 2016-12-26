package back;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class User implements Serializable{
	public static final String regex = "|";
    private byte[] mac;
    private String nickName;
    private int imageId;
    private byte[] ipAddress;
    private long time;

    public User(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Arrays.equals(mac, user.mac);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mac);
    }

    public User(byte[] mac, String nickName, int imageId) {
        this.nickName = nickName;
        this.imageId = imageId;
        this.mac=mac;
    }

    public String toString(){
    	String result = Arrays.toString(this.mac)+regex+this.nickName+regex
    		+this.imageId;
		return result;
    }
    
    public byte[] toByte(){
    	return this.toString().getBytes();
    }

    public static User valueOf(String s){
        String[] st = s.split("\\|");
        String strArr = st[0];
        strArr = strArr.substring(1, strArr.length()-1);
        String[] strMac = strArr.split(",");
        byte[] mac = new byte[strMac.length];
        for(int i = 0; i<strMac.length; i++){
            mac[i] = Byte.valueOf(strMac[i].trim());
        }
        User result = new User(mac,st[1],Integer.valueOf(st[2]));
        return result;
    }

    public static boolean checkNickname(String nick){
        if(nick.length()<1 || nick.length()>16) return  false;
        for(int i=0;i<nick.length();i++){
            int u = (int) nick.charAt(i);
            boolean check = (u>=48 && u<=57) || (u>=65 && u<=90) || (u==95) || (u>=97 && u<=122); //0..9 A..Z a..z _
            if (!check) return false; //не подходит
        }
        return true; // все нормально
    }

    public static User createFromByte(byte[] bytes){
        String str = new String(bytes);
        return User.valueOf(str);
    }

    public static byte[] receiveMacAddress(){
        Enumeration<NetworkInterface> nets;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface netint : Collections.list(nets)){
                if(!netint.getDisplayName().equals("lo")){
                    return netint.getHardwareAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void macToByte(String mac){
        String[] strArr = mac.split(":");
        byte[] byteArr = new byte[strArr.length];
        for(int i = 0; i<strArr.length; i++){
            byteArr[i] = (byte)(Integer.parseInt(strArr[i], 16));
        }
        this.mac = byteArr;
    }
    
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
