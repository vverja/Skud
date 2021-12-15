package gui.controllers;

import javafx.beans.binding.Bindings;
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

/**
 * Класс обеспечивающий работу окна менеджера пользователей
 * Выводит окно с таблицей пользователей, кнопки добавления, удаления и редактирования пользователей
 */
public class UserManager {
    private final Users users = Users.getInstace();
    private Stage userManagerStage;

    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonDelete;
    @FXML
    public Button buttonPasswordChange;
    @FXML
    public Button buttonExit;
    @FXML
    public TableView<Long> userTable;
    @FXML
    public TableColumn<Long, String> nameColumn;
    @FXML
    public TableColumn<Long, String> passColumn;

    @FXML
    private void initialize(){
        ObservableList<Long> keys = Users.getInstace().getObservableList();
        userTable.setItems(keys);

        nameColumn.setCellValueFactory(cellData->Bindings.valueAt(users.getUsers(),cellData.getValue()));
        passColumn.setCellValueFactory(cellData->Bindings.createStringBinding(()->cellData.getValue().toString()));
        //userTable.setItems(users.getObservableList());
    }

    @FXML
    public void deleteButtonHandle(){
        users.deleteUser(userTable.getSelectionModel().getSelectedItem());
        Users.getInstace().writeData();
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
        Users.getInstace().writeData();
    }

    private void showEditDialog(Long key){
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
            controller.setUser(key);
            controller.usernameFieldAvailability(key==null);
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setOnCloseRequest(e->Users.getInstace().writeData());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUserManagerStage(Stage userManagerStage) {
        this.userManagerStage = userManagerStage;
    }
}
