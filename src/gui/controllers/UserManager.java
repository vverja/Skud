package gui.controllers;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;

import java.io.IOException;
import java.util.Map;

public class UserManager {
    private final Users users = Users.getInstace();
    private Stage userManagerStage;
    private ObservableList<Map.Entry<LongProperty, StringProperty>> usersList;

    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonDelete;
    @FXML
    public Button buttonPasswordChange;
    @FXML
    public Button buttonExit;
    @FXML
    public TableView<Map.Entry<LongProperty, StringProperty>> userTable;
    @FXML
    public TableColumn<Map.Entry<LongProperty, StringProperty>, String> nameColumn;
    @FXML
    public TableColumn<Map.Entry<LongProperty, StringProperty>, Long> passColumn;

    @FXML
    private void initialize(){
        Users.getInstace().beginTransaction();
        nameColumn.setCellValueFactory(cellData->cellData.getValue().getValue());
        passColumn.setCellValueFactory(cellData->cellData.getValue().getKey().asObject());
        userTable.setItems(users.getObservableList());
    }

    @FXML
    public void deleteButtonHandle(){
        users.deleteUser(userTable.getSelectionModel().getSelectedItem().getKey());
    }

    @FXML
    public void addButtonHandle(){
        showEditDialog(null);
    }

    @FXML
    public void editButtonHandle(){
        showEditDialog(userTable.getSelectionModel().getSelectedItem());

    }
    @FXML
    private void buttonExitHandle(){
        userManagerStage.close();
        Users.getInstace().endTransaction();
    }

    private void showEditDialog(Map.Entry<LongProperty, StringProperty> user){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/makets/userEdit.fxml"));
        try {
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Вікно редагування користувачів");

            stage.initOwner(userManagerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            UserEdit controller = loader.getController();
            controller.setUserEditStage(stage);
            controller.setUser(user);

            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setOnCloseRequest(e->Users.getInstace().endTransaction());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUserManagerStage(Stage userManagerStage) {
        this.userManagerStage = userManagerStage;
    }
}
