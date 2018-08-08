package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MessageHandler {
	private HBox hbox;
	private VBox vbox;
	private Label message;
	private Button action1;
	private Button action2;
	private Stage stage;
	private Scene scene;

	public MessageHandler(String Header, String Body, String ActionName) {
		vbox = new VBox();
		vbox.setPrefSize(400, 100);
		vbox.setAlignment(Pos.CENTER);
		vbox.setStyle("-fx-background-color: #00ACC1;");
		hbox = new HBox();
		hbox.setPrefSize(300, 60);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle("-fx-background-color: #00ACC1;");
		DropShadow ds = new DropShadow(10, Color.WHITE);
		hbox.setEffect(ds);
		message = new Label(Body);
		message.setStyle("-fx-text-fill: white;");
		hbox.getChildren().add(message);
		action1 = new Button(ActionName);
		action1.setPrefSize(140, 25);
		action1.setCursor(Cursor.HAND);
		vbox.getChildren().addAll(hbox, action1);
		vbox.setMargin(action1, new Insets(10, 0, 5, 0));
		scene = new Scene(vbox);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		action1.getStyleClass().add("messagebtn");
		stage = new Stage(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();
		action1.setOnAction(e -> {
			stage.close();
		});
	}
	
	

}
