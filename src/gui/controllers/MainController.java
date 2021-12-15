package gui.controllers;

import gui.Comparator;
import gui.EmptyDatabaseException;
import gui.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.TurnstileEvent;
import models.Users;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Класс обеспечивающий работу окна событий, выводит окно с таблицей событий, позволяет запускать
 * эмулятор турникета, проверять пароли пользователей и запускать окно менеджера пользователей
 **/
public class MainController {
    private boolean isAdminMode;
    private Stage mainStage;
    private boolean isTurnstileWorked;

    @FXML
    private TableView<TurnstileEvent> eventsTable;
    @FXML
    private TableColumn<TurnstileEvent, String> timeColumn;
    @FXML
    private TableColumn<TurnstileEvent, String> usernameColumn;
    @FXML
    private TableColumn<TurnstileEvent, String> eventColumn;
   @FXML
    private Button exitButton;

    @FXML
    private void initialize(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        timeColumn.setCellValueFactory(
                cellData->new SimpleStringProperty(cellData.getValue().getDateTime().format(formatter)));
        usernameColumn.setCellValueFactory(cellData->cellData.getValue().usernameProperty());
        eventColumn.setCellValueFactory(
                cellData->new SimpleStringProperty(cellData.getValue().getEventType().getName()));

        eventsTable.setItems(Main.getTurnstileEvents());

        exitButton.setOnAction(e->{
            Comparator.getInstance().stopTurnstileWork();
            mainStage.close();
        });
    }
    @FXML
    private void passCheckButtonHandle(){
        Stage dialog = new Stage();
        dialog.setTitle("Пароль турникета");

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

            if (pb.getText().length()> 0 && Comparator.getInstance().checkUser(Long.parseLong(pb.getText()))) {
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

    @FXML
    private void managerButtonHandle(){
        if (!isAdminMode){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка доступу");
            alert.setContentText("У вас недостатньо прав!");
            alert.initOwner(mainStage);
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/makets/userManager.fxml"));
        try {
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Вікно користувачів");

            stage.initOwner(mainStage);
            stage.initModality(Modality.WINDOW_MODAL);

            UserManager controller = loader.getController();
            controller.setUserManagerStage(stage);
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setOnCloseRequest(e->Users.getInstace().writeData());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void turnstileTurnOnOffButtonHandle(){
        if(!isTurnstileWorked) {
            try {
                Comparator.getInstance().beginTurnstileWork();
                isTurnstileWorked = true;
                turniketMessage("Турникет увімкнуто!");
            }catch (EmptyDatabaseException e){
                turniketMessage("Немае жодного користувача у базі");
            }
        }else{
            isTurnstileWorked = false;
            Comparator.getInstance().stopTurnstileWork();
            turniketMessage("Турникет вимкнуто!");
        }
    }


    public void setAdminMode(boolean adminMode) {
        isAdminMode = adminMode;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private void turniketMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Турникет");
        alert.setContentText(message);
        alert.show();
    }
}
