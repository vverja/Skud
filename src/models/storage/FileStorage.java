package models.storage;

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

/**
 * Утилитарный класс, обеспечивающий доступ и взаимодействие с текстовым файлом для записи и
 * чтения данных о пользователях
 */
public class FileStorage {
    private static final String USERSFILENAME = "./users.txt";

    public static HashMap<Long, String> readAllData(){
        try(BufferedReader reader = Files.newBufferedReader(Path.of(USERSFILENAME),Charset.defaultCharset())){
            return new HashMap<>(reader.lines().map(line->line.split(":"))
                    .collect(Collectors.toMap(p->Long.parseLong(p[0]),p->p[1])));
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
    public static void writeAllData(Map<Long, String> data){
        try(BufferedWriter writer = Files.newBufferedWriter(Path.of(USERSFILENAME), Charset.defaultCharset())){
            for (Map.Entry<Long, String> entry :data.entrySet()
                 ) {
                writer.append(String.format("%d:%s\n", entry.getKey(), entry.getValue()));
            }

        }catch (IOException e){
            System.out.println("Ошибка записи в файл! " + USERSFILENAME);

        }
    }
}
