package front;

import back.Message;
import back.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * Created by andrey on 24.12.16.
 */
public class MessageCell extends ListCell<Message>{
    HBox hBox = new HBox();
    ImageView avatar = new ImageView(new Image("images"+ File.separator+"img0.png"));
    VBox vBox = new VBox();
    HBox infoBox = new HBox();
    Label nick = new Label("nick");
    Label time = new Label("00:00");
    Label text = new Label("text");
    AnchorPane anchorPane = new AnchorPane();
    Controller controller;

    public MessageCell(Controller controller){
        super();
        this.controller=controller;
        avatar.setFitHeight(40.0);
        avatar.setFitWidth(40.0);
        text.setWrapText(true);
        nick.setStyle("-fx-font-weight: bold; -fx-font-size:14");
        time.setStyle("-fx-font-size: 10; -fx-font-color:lightgrey");

        text.maxWidthProperty().bind(controller.widthBind);

        infoBox.setSpacing(5.0);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.getChildren().addAll(nick,time);
        vBox.setSpacing(5.0);
        vBox.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(anchorPane,Priority.ALWAYS);
        vBox.getChildren().addAll(infoBox,text);
        hBox.setPadding(new Insets(5.0, 0.0, 5.0, 0.0));
        hBox.setSpacing(5.0);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(avatar, vBox);
        hBox.setBorder(new Border(new BorderStroke(Color.GAINSBORO, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.0, 0.0, 0.3, 0.0))));
        HBox.setHgrow(vBox, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if(empty){
            setGraphic(null);
        } else {
            User user = message.getUser();
            nick.setText(user.getNickName()!=null ? user.getNickName() : "<null>");
            avatar.setImage(user.getImageId()!=0 ? new Image("images"+File.separator+"img"+user.getImageId()+".png") : new Image("images"+File.separator+"img0.png"));
            time.setText(message.getTime()!=0 ? message.getFormatTime() : "<null>");
            text.setText(message.getText()!=null ? message.getText() : "<null>");
            //anchorPane.getChildren().add(text);
            setGraphic(hBox);
        }
    }
}
