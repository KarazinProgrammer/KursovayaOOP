package front;

import back.*;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {

    private String nickname = "User";
    private int imageId= (new Random(System.currentTimeMillis())).nextInt(30)+1;
    private final int imageNumber=30; //к-во картинок
    public User user = new User(User.receiveMacAddress() ,nickname, imageId );
    private UserSenderTimerTask ustt;

    @FXML
    private Circle circleCog;
    @FXML
    private ImageView imageCog;

    @FXML
    private AnchorPane contactsPane;
    @FXML
    public AnchorPane anchorPane;

    @FXML
    private TitledPane titledPane;
    @FXML
    private Button button;
    @FXML
    private Pane paneGlass;
    @FXML
    public TabPane tabPane;
    @FXML
    private ListView listViewContacts;
    @FXML
    private Label nickLabel;
    @FXML
    private ImageView avatarImage;
    @FXML
    private AnchorPane panel;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private TextField nickTextField;
    @FXML
    private Label errorLabel;
    @FXML
    public TextArea textArea;

    DoubleBinding widthBind;


    @FXML
    public void initialize(){
        if (FileWorker.isUserExists()){
            user=FileWorker.loadUser();
            setNickname(user.getNickName());
            setImageId(user.getImageId());
        } else {
            setNickname("User");
            setImageId(imageId);
        }

        //textArea.setStyle("-fx-focus-color: #dcdcdc");
        //listViewContacts.setStyle("-fx-focus-color: #dcdcdc; -fx-accent: #dcdcdc");

        ObservableDoubleValue obsDouble = anchorPane.widthProperty();

        widthBind = new DoubleBinding() {
            {
                super.bind(obsDouble);
            }

            @Override
            protected double computeValue() {
                return obsDouble.getValue().doubleValue()-80.0;
            }
        };

        avatarImage.setImage(new Image("images"+File.separator+"img"+imageId+".png"));
        nickLabel.setText(nickname);
        nickTextField.setText(nickname);
        Main.controller=this;

        ListView lv = new ListView();
        tabPane.getTabs().get(0).setContent(lv);

        titledPane.setBorder(new Border(new BorderStroke(Color.DODGERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2.0, 2.0, 2.0, 2.0))));
        contactsPane.setBorder(new Border(new BorderStroke(Color.DODGERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.0, 0.0, 0.0, 0.5))));
        contactsPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        tabPane.setBorder(new Border(new BorderStroke(Color.DODGERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.0, 0.0, 0.5, 0.0))));

        textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    sendMessage();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            textArea.setText("");
                        }
                    });
                }
            }
        });

        showChange();

        OnlineCleanerTimerTask onlineCleanerTimerTask = new OnlineCleanerTimerTask(Translator.users.getUsers(), this);
        Main.timer.schedule(onlineCleanerTimerTask, 3000, 1000);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nickTextField.requestFocus();
            }
        });

//        nickTextField.setText("123");
//        acceptChanges();
    }

    @FXML
    private void deleteExcess(){
        textArea.setText(textArea.getText().replaceAll("\\|",""));
        textArea.setText(textArea.getText().replaceAll("\\n",""));
        //textArea.selectPositionCaret(textArea.getText().length()-1);
        System.out.println("text replaced");
        if (textArea.getText().length()<100) return;
        textArea.setText(textArea.getText().substring(0,99));
    }

    @FXML
    private void sendMessage(){
        String text = textArea.getText();
        text=text.replaceAll("\\|","");
        text=text.replaceAll("\\n","");
        text.trim();
        if(text.length()<=0 || text.length()>=100) return;
        textArea.setText("");
        Tab tab = tabPane.getSelectionModel().getSelectedItem();

        if(tabPane.getTabs().indexOf(tab)==0){
            Message message = new Message(user, true, text);
            Translator.localChat.add(message);
            try {
                Main.client.send(message.toByte(), InetAddress.getByName("255.255.255.255"));
            } catch (UnknownHostException e) {
            }
            updateMessages();
            textArea.setText("");
            return;
        }

        Message message = new Message(user,false,text);
        User receiver = Translator.users.findByName(tab.getText());


        if(!Translator.users.getUsers().contains(tab.getUserData())) return;
        if(!Translator.dialogs.containsKey(receiver)){
            MessageList messageList = new MessageList();
            messageList.add(message);
            Translator.dialogs.put(receiver, messageList);
            //update file
        } else {
            Translator.dialogs.get(receiver).add(message);
        }

        try {
            Main.client.send(message.toByte(), InetAddress.getByAddress(receiver.getIpAddress()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
       FileWorker.updateDialog(Translator.dialogs, (User) tab.getUserData());
        updateMessages();
    }

    @FXML
    public void updateMessages(){

        List<Tab> tabs = tabPane.getTabs();

        ListView listView = (ListView) tabs.get(0).getContent();
        ArrayList<Message> localChat = Translator.localChat.getMessages();
        ObservableList<Message> obsList = FXCollections.observableList(localChat);
        listView.setItems(obsList);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                MessageCell messageCell = new MessageCell(Main.controller);
                return messageCell;
            }
        });
        tabs.get(0).setContent(listView);

        for(Tab tab:tabs){
            if (tabs.indexOf(tab)==0) continue;
            User user = Translator.users.findByName(tab.getText());
            if(Translator.dialogs.containsKey(user)) {
                ListView lv = (ListView)tab.getContent();

                ArrayList<Message> messages = Translator.dialogs.get(user).getMessages();
                ObservableList<Message> list = FXCollections.observableList(messages);
                lv.setItems(list);
                lv.setCellFactory(new Callback<ListView, ListCell>() {
                    @Override
                    public ListCell call(ListView param) {
                        MessageCell messageCell = new MessageCell(Main.controller);
                        messageCell.setFocusTraversable(false);
                        return messageCell;
                    }
                });
                tab.setContent(lv);
            }
        }
    }

    @FXML
    public void showChange(){
        nickTextField.setFocusTraversable(true);
        paneGlass.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        paneGlass.setOpacity(1.0);
        paneGlass.setEffect(new Shadow(0.0, new Color(0.5,0.5,0.5,0.8)));
        paneGlass.setVisible(true);
        panel.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        panel.setEffect(new DropShadow(15.0, Color.BLACK));
        panel.setVisible(true);
        avatarImageView.setImage(new Image("images"+File.separator+"img"+imageId+".png"));
        nickTextField.requestFocus();
    }

    @FXML
    private void hideChange(){
        panel.setVisible(false);
        paneGlass.setVisible(false);
        textArea.requestFocus();
    }


    @FXML
    private void increasedCog(){
        circleCog.setRadius(11.0);

        imageCog.setFitHeight(22.0);
        imageCog.setFitWidth(22.0);

    }

    @FXML
    private void normalCog(){
        circleCog.setRadius(10.0);

        imageCog.setFitHeight(20.0);
        imageCog.setFitWidth(20.0);
    }

    @FXML
    public void setNextImage(){
        if(imageId==imageNumber) imageId=1;
            else imageId++;
        avatarImageView.setImage(new Image("images"+File.separator+"img"+imageId+".png"));
    }

    @FXML
    public void setPreImage(){
        if(imageId==1) imageId=imageNumber;
        else imageId--;
        avatarImageView.setImage(new Image("images"+File.separator+"img"+imageId+".png"));
    }

    @FXML
    public void acceptChanges(){
        user=null;
        if(ustt!=null) ustt.cancel();
        errorLabel.setVisible(false);
        nickname=nickTextField.getText();
        boolean nickValidity = User.checkNickname(nickname);
        boolean nickUniq = Translator.users.isUniqueNick(nickname);
        if(!User.checkNickname(nickname)) {
            showAlert(true);
            return;
        }
        if (!Translator.users.isUniqueNick(nickname)){
            showAlert(false);
            return;
        }

        changeAvatar();
        changeNickname();
        user=new User(User.receiveMacAddress(), nickname, imageId);
        user.setImageId(imageId);
        user.setNickName(nickname);
        hideChange();
        ustt=new UserSenderTimerTask(user, this, Main.client);
        Main.timer.schedule(ustt, 250, 250);
        FileWorker.updateUserFile(user);
    }

    @FXML
    public void showAlert(boolean flag){
        if(flag)
            errorLabel.setText("0..9, A..Z, a..z, _, от 1 до 16 символов");
        else errorLabel.setText("Это имя уже занято");
        errorLabel.setVisible(true);
    }

    @FXML
    public void changeNickname(){
        nickLabel.setText(nickname);
    }

    @FXML
    public void changeAvatar(){
        avatarImage.setImage(new Image("images"+File.separator+"img"+imageId+".png"));
    }

    @FXML
    public void updateList() throws  IllegalStateException{
        ArrayList<User> users = Translator.users.getUsers();

        ObservableList<User> list = FXCollections.observableList(users);

        listViewContacts.setItems(list);

        listViewContacts.setCellFactory(new Callback<ListView<ContactCell>, ListCell<ContactCell>>() {
            @Override
            public ListCell call(ListView param) {
                return new ContactCell(Main.controller);
            }
        });
    }

    @FXML
    public void updateTabs(){
        List<Tab> tabList = tabPane.getTabs();
        for(Tab tab:tabList){
            if(Translator.users.getUsers().contains((User) tab.getUserData())){

            }
        }
    }

    public boolean isTabOpened(User user){
        List<Tab> tabList = tabPane.getTabs();
        for(Tab tab:tabList)
            if(user.equals((User) tab.getUserData()))
                return true;
        return false;
    }


    @FXML
    public void openTab(User user, boolean flag){
        List<Tab> tabList = tabPane.getTabs();
        for(Tab tab: tabList)
            if(user.equals((User)tab.getUserData())){
                if(flag) tabPane.getSelectionModel().select(tab);
                return;
            }
        Tab tab = new Tab(user.getNickName());
        tab.setUserData(user);
        tab.setContent(new ListView());
        tabPane.getTabs().addAll(tab);
        if(flag) tabPane.getSelectionModel().select(tab);
    }

    @FXML
    public void listAction(User user){
        String s = user.getNickName();
        List<Tab> tabList = tabPane.getTabs();
        for(Tab tab:tabList){
            if (tab==tabList.get(0)) continue;
            if(((User)tab.getUserData()).equals(user)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        Tab tab = new Tab(s);
        tab.setUserData(user);
        tab.setContent(new ListView());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        updateMessages();
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

}