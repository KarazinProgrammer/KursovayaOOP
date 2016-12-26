package front;

import back.User;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.File;

public class ContactCell extends ListCell<User> {
    HBox hBox = new HBox();
    ImageView avatar = new ImageView(new Image("images"+ File.separator+"img0.png"));
    Pane pane = new Pane();
    Label nick = new Label("nick");
    User lastUser;
    Controller controller;
    EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    controller.listAction(lastUser);
                }
            }
        }
    };

    public ContactCell(Controller controller){
        super();
        this.controller=controller;
        avatar.setFitWidth(30.0);
        avatar.setFitHeight(30.0);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(avatar, nick);
        HBox.setHgrow(pane, Priority.ALWAYS);
        hBox.setOnMouseClicked(eventHandler);
    }

    @Override
    protected void updateItem(User user, boolean empty){
        super.updateItem(user, empty);
        setText(null);
        if (empty) {
            lastUser = null;
            setGraphic(null);
        } else {
            lastUser = user;
            nick.setText(user.getNickName()!=null ? " "+user.getNickName() : "<null>");
            avatar.setImage(user.getImageId()!=0 ? new Image("images"+File.separator+"img"+user.getImageId()+".png") : new Image("images"+File.separator+"img0.png"));
            setGraphic(hBox);
        }
    }

}