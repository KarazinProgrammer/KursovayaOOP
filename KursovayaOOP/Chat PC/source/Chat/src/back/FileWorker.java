package back;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class FileWorker {
    private static final File userFile = new File("src"+File.separator+"data"+File.separator+"user.user");

    public static boolean isDialogExists(User user){
        File dialog = new File("src"+File.separator+"data"+File.separator+ Arrays.toString(user.getMac()));
        return dialog.exists();
    }

    public static MessageList loadDialog(File dialog){
        MessageList messageList = new MessageList();
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dialog));
            messageList = (MessageList) ois.readObject();
            ois.close();
        } catch (IOException e){
        } catch (ClassNotFoundException e) {
        }
        return messageList;
    }

    public static MessageList loadDialog(User user){
        File dialog = new File("src"+File.separator+"data"+File.separator+ Arrays.toString(user.getMac()));
        MessageList messageList = new MessageList();
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dialog));
            messageList = (MessageList) ois.readObject();
            ois.close();
        } catch (IOException e){
        } catch (ClassNotFoundException e) {
        }
        return messageList;
    }

    public static void updateDialog(HashMap<User, MessageList> dislogsMap, User user){
        File dialog = new File("src"+File.separator+"data"+File.separator+ Arrays.toString(user.getMac()));
        MessageList messageList = dislogsMap.get(user);
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dialog));
            oos.writeObject(messageList);
            oos.close();
        } catch (IOException e){
        }
    }

    public static boolean isUserExists(){
        return userFile.exists();
    }

    public static User loadUser(){
        long length = userFile.length();
        byte[] bytes = new byte[(int)length];
        try {
            InputStream is = new FileInputStream(userFile);
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file "+userFile.getName());
            }
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return User.valueOf(new String(bytes));
    }

    public static void deleteExcessMessages(){
        File file = new File("src"+File.separator+"data");
        File[] fileArray = file.listFiles();
        for(File f: fileArray){
            loadDialog(f).removeExcess();
        }
    }

    public static void updateUserFile(User user){
         try {
             if (!userFile.exists())
                 userFile.createNewFile();
             OutputStream outputStream = new FileOutputStream(userFile);
             outputStream.write(user.toByte());
             outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}