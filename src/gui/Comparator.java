package gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import models.EventType;
import models.TurnstileEvent;
import models.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Comparator{
    private static final Comparator instance = new Comparator();
    private final Turnstile turnstile = new Turnstile();
    private Thread turnstileWork;

    private Comparator(){
    }

    public static Comparator getInstance(){
        return instance;
    }

    public void beginTurnstileWork() throws EmptyDatabaseException{
        if (Users.getInstace().getUsers().size()==0)
            throw new EmptyDatabaseException();
        turnstileWork = new Thread(() -> {
                    try {
                        Users.getInstace().beginTransaction();
                        Map<LongProperty, StringProperty> users = Users.getInstace().getUsers();
                        while (!turnstileWork.isInterrupted()) {
                            int randomTime = (int) (Math.random() * 10000);
                            LongProperty key;
                            Thread.sleep(randomTime);
                            if(randomTime%5!=0){
                                List<LongProperty> keyList = new ArrayList<>(users.keySet());
                                int index = (int)(Math.random() * users.size());
                                key = keyList.get(index);
                            }else{
                                key = new SimpleLongProperty((long) (Math.random() * 100000000));
                            }
                            checkUser(key);

                        }
                    } catch (InterruptedException exception) {

                    }

        });
        turnstileWork.start();
    }

    public void stopTurnstileWork(){
        if (turnstileWork!=null)
            turnstileWork.interrupt();
    }

    public void checkUser (LongProperty password){
        Map<LongProperty, StringProperty> users = Users.getInstace().getUsers();
        if(users.containsKey(password)){
            StringProperty username = users.get(password);
            EventType eventType = getEventType(username.getValue());
            Main.getTurnstileEvents().add(new TurnstileEvent(username.getValue(), eventType));
            turnstile.open();
        }else{
            Main.getTurnstileEvents().add(new TurnstileEvent("Невідома людина", EventType.BLOCKED));
            turnstile.block();
        }
    }

    private EventType getEventType(String userName){
        int foundEvents = Main.getTurnstileEvents().filtered(e->e.getUsername().equals(userName)).size();
        if(foundEvents%2==0)
            return EventType.IN;
        else
            return EventType.OUT;
    }
}
