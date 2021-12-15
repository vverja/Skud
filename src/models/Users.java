package models;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import models.storage.FileStorage;

import java.util.Map;

/**
 * Класс хранит в себе данные о пользователях и паролях, а так же взаимодействует с утилитарным классом
 * Filestorage для записи и чтения данных.
 * Поле users содежит HashMap где ключем является пароль, а значением имя пользователя
 * Поле keys содержит список ключей (паролей) для взаимодействия с таблицей в форме менеджера пользователей
 * Класс создан по шаблону синглтон, т.к. в программе может существовать только один список пользователей
 */
public class Users {

    private final ObservableMap<Long, String> users = FXCollections.observableHashMap();
    private final ObservableList<Long> keys = FXCollections.observableArrayList();

    private static final Users instance = new Users();


    public static Users getInstace() {
        return instance;
    }

    private Users(){
        users.addListener((MapChangeListener.Change<? extends Long, ? extends String> change)->{
            if (change.wasRemoved())
                keys.remove(change.getKey());
            if(change.wasAdded())
                keys.add(change.getKey());
        });
    }

    public void readData(){
        readDataFromFileStorage();
    }

    public void writeData(){
        writeDataToFileStorage();
    }

    public ObservableMap<Long, String> getUsers() {
        return users;
    }
    public ObservableList<Long> getObservableList(){return keys;}


    public void addUser(Long id, String name){
        users.put(id, name);
    }

    public void deleteUser(Long id){
            users.remove(id);
    }

    public void changeUser(Long oldId, Long newId, String name){
        deleteUser(oldId);
        addUser(newId, name);
    }

    private void readDataFromFileStorage(){
        Map<Long, String> data =  FileStorage.readAllData();
        data.forEach((key, value)-> users.put(key, value));
    }

    private void writeDataToFileStorage(){
        FileStorage.writeAllData(users);
    }
}
