package gui.controllers;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Users;

import java.util.Map;

public class UserEdit {
    private Stage userEditStage;
    private boolean isNewUser;
    private LongProperty oldKey;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwrdTextField;

    @FXML
    private void initialize() {

    }

    @FXML
    private void saveButtonHandle() {
        if (userValidation()) {
            LongProperty key = new SimpleLongProperty(Long.parseLong(passwrdTextField.getText()));
            StringProperty username = new SimpleStringProperty(usernameTextField.getText());
            if (!isNewUser) {
                Users.getInstace().changeUser(oldKey, key, username);
            }else {
                Users.getInstace().addUser(key, username);
            }
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

    public void setUser(Map.Entry<LongProperty, StringProperty> user) {
        if (user != null) {
            usernameTextField.setText(user.getValue().getValue());
            passwrdTextField.setText(user.getKey().getValue().toString());
            oldKey = user.getKey();
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
}
