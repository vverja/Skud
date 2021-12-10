package models.storage;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FileStorage {
    private static final String USERSFILENAME = "./users.txt";

    public static Map<LongProperty, StringProperty> readAllData(){
        try(BufferedReader reader = Files.newBufferedReader(Path.of(USERSFILENAME),Charset.defaultCharset())){
            return reader.lines().map(line->line.split(":"))
                    .collect(Collectors.toMap(
                                p->new SimpleLongProperty(Long.parseLong(p[0])),
                                p->new SimpleStringProperty(p[1])
                    ));
        }catch (IOException e){
            try {
                if(e instanceof NoSuchFileException) {
                    Files.createFile(Path.of(USERSFILENAME));
                    System.out.println("Создан файл " + USERSFILENAME);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return new HashMap<>();
    }
    public static void writeAllData(Map<LongProperty, StringProperty> data){
        try(BufferedWriter writer = Files.newBufferedWriter(Path.of(USERSFILENAME), Charset.defaultCharset())){
            for (Map.Entry<LongProperty, StringProperty> entry :data.entrySet()
                 ) {
                writer.append(String.format("%d:%s\n", entry.getKey().getValue(), entry.getValue().getValue()));
            }

        }catch (IOException e){
            System.out.println("Ошибка записи в файл! " + USERSFILENAME);

        }
    }
    private static Map.Entry<LongProperty, StringProperty> convertStringToMap(String line){
        String[] keyValue = line.split(":");
        try {
            return Map.entry(new SimpleLongProperty(Long.parseLong(keyValue[0])),
                                new SimpleStringProperty(keyValue[1]));
        }catch (IndexOutOfBoundsException
               |NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }
}
