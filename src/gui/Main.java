package gui;

import gui.controllers.MainController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.TurnstileEvent;
import models.Users;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Класс с которого начинается работа с графическим интерфейсом
 * Здесь осуществляется выбор профиля работы и авторизация администратора,
 * после чего запускается окно событий
 */

public class Main extends Application {
    private boolean admin = false;
    private final String password = "1234";
    private final static ObservableList<TurnstileEvent> events = FXCollections.observableArrayList();

    public static ObservableList<TurnstileEvent> getTurnstileEvents() {
        return events;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Users.getInstace().readData();
        showProfileSelectorDialog();
        showMain();
     }

    public void showMain(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/makets/main.fxml"));
        try {
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Вікно подій");
            Scene scene = new Scene(pane);
            stage.setScene(scene);

            MainController controller = loader.getController();
            controller.setAdminMode(admin);
            controller.setMainStage(stage);
            stage.setOnCloseRequest(e->Comparator.getInstance().stopTurnstileWork());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProfileSelectorDialog(){
        List<String> arrayList = Arrays.asList("Адміністратор", "Вахтер");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(arrayList.get(1),arrayList);
        dialog.setTitle("Вибір профілю");
        dialog.setHeaderText("Виберіть профіль роботи:");
        dialog.setContentText("Профіль:");

        Optional<String> chosenProfile =  dialog.showAndWait();
        if (chosenProfile.isPresent()){
            if (chosenProfile.get().equals("Вахтер")) {
                admin = false;
            }else {
                showPasswordDialog();
            }
        }
    }

    private void showPasswordDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("Пароль адміністратора");

        final Label message = new Label("");
        final PasswordField pb = new PasswordField();

        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 0, 0, 10));
        vb.setSpacing(10);

        HBox hb1 = new HBox();
        hb1.setSpacing(10);
        hb1.setAlignment(Pos.CENTER_LEFT);
        HBox hb2 = new HBox();
        hb2.setSpacing(10);
        hb2.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Пароль:");
        Button buttonOK = new Button("OK");
        buttonOK.setOnAction(e->{
            if (pb.getText().equals(password)) {
                admin = true;
                dialog.close();
            }else {
                message.setText("Пароль невірний!");
                message.setTextFill(Color.rgb(210, 39, 30));
            }
        });

        Button buttonCancel = new Button("Відміна");
        buttonCancel.setOnAction(e->{
            dialog.close();
        });
        hb1.getChildren().addAll(label, pb);
        hb2.getChildren().addAll(buttonOK, buttonCancel);
        vb.getChildren().addAll(hb1, message, hb2);
        Scene scene = new Scene(vb, 250, 100);
        dialog.setScene(scene);
        dialog.showAndWait();

    }

    public static void main(String[] args) {
        launch(args);
    }

}


