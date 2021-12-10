package gui.controllers;

import gui.Comparator;
import gui.EmptyDatabaseException;
import gui.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.TurnstileEvent;
import models.Users;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
            stage.setOnCloseRequest(e->Users.getInstace().endTransaction());

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
