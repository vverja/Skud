package models;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.storage.FileStorage;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private Map<LongProperty, StringProperty> users = new HashMap<>();
    private ObservableList<Map.Entry<LongProperty, StringProperty>> observableList
                                                    = FXCollections.observableArrayList();

    private static final Users instance = new Users();

    private boolean transactionStatus = false;
    private final String TRANSACTION_ERROR_MESSAGE = "Помилка! Транакція запису данних не відкрита!";

    public static Users getInstace() {
        return instance;
    }

    private Users(){

    }

    public void beginTransaction(){
        readDataFromFileStorage();
        transactionStatus = true;
    }

    public void endTransaction(){
        writeDataToFileStorage();
        transactionStatus = false;
    }

    public Map<LongProperty, StringProperty> getUsers() {
        return users;
    }
    public ObservableList<Map.Entry<LongProperty, StringProperty>> getObservableList(){return observableList;}


    public void addUser(LongProperty id, StringProperty name){
        if (transactionStatus) {
            users.put(id, name);
            observableList.add(Map.entry(id,name));
        }
        else
            System.out.println(TRANSACTION_ERROR_MESSAGE);

    }

    public void deleteUser(LongProperty id){
        if (transactionStatus) {
            users.remove(id);
            observableList.stream().filter(e->e.getKey().equals(id))
                    .findFirst().ifPresent(p->observableList.remove(p));
        }
        else
            System.out.println(TRANSACTION_ERROR_MESSAGE);
    }

    public void changeUser(LongProperty oldId, LongProperty newId, StringProperty name){
        if (transactionStatus){
            deleteUser(oldId);
            addUser(newId, name);
        } else
            System.out.println(TRANSACTION_ERROR_MESSAGE);
    }

    private void readDataFromFileStorage(){
        users = FileStorage.readAllData();
        observableList = FXCollections.observableArrayList(users.entrySet());
    }

    private void writeDataToFileStorage(){
        FileStorage.writeAllData(users);
    }
}
