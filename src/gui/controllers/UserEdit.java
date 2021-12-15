package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Users;

/**
 * Класс обеспечивающий работу окна добавления и редактирования пользователей
 */
public class UserEdit {
    private Stage userEditStage;
    private boolean isNewUser;
    private Long oldKey;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwrdTextField;

    @FXML
    private void initialize() {

        passwrdTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length()>7)
                    passwrdTextField.setText(newValue.substring(0,7));
            }
        });
    }

    @FXML
    private void saveButtonHandle() {
        if (userValidation()) {
            Long key = Long.parseLong(passwrdTextField.getText());
            String username = usernameTextField.getText();
            if (!isNewUser) {
                Users.getInstace().changeUser(oldKey, key, username);
            }else {
                Users.getInstace().addUser(key, username);
            }
            Users.getInstace().writeData();
            userEditStage.close();
        }
    }

    @FXML
    private void cancelButtonHandle() {
        userEditStage.close();
    }

    @FXML
    private void passGenerateButtonHandle() {
        passwrdTextField.setText(getRandomId().toString());
    }

    public void setUser(Long key) {
        if (key != null) {
            usernameTextField.setText(Users.getInstace().getUsers().get(key));
            passwrdTextField.setText(key.toString());
            oldKey = key;
        } else {
            isNewUser = true;
        }
    }

    public void setUserEditStage(Stage userEditStage) {
        this.userEditStage = userEditStage;
    }

    private boolean userValidation() {
        String allertMessage = "";
        if (usernameTextField.getText().length() < 5)
            allertMessage += "в ПІБ користувача занадто мало символів\n";
        if (passwrdTextField.getText().length() < 7)
            allertMessage += "Пароль не відповідае вимогам безпеки,\nмінімум 7 цифр\n";
        else {
            try {
                Long.parseLong(passwrdTextField.getText());
            } catch (NumberFormatException e) {
                allertMessage += "Пароль не відповідае вимогам безпеки,\nмінімум 7 цифр\n";
            }
        }
        if (allertMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Пароль");
            alert.setContentText(allertMessage);
            alert.show();
            return false;
        }
        return true;
    }

    private Long getRandomId() {
        int max = 9999999;
        int min = 1000000;
        return (long) (min + Math.random() * ((max-min)+1));
    }
    public void usernameFieldAvailability(boolean available){
        usernameTextField.editableProperty().setValue(available);
    }
}
