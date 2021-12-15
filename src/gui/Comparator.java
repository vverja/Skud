package gui;

import models.EventType;
import models.TurnstileEvent;
import models.Users;

import java.util.List;
import java.util.Map;

/**
 * Класс для проверки паролей пользователей и эмулирования работы турникета
 */
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
        Users.getInstace().readData();
        if (Users.getInstace().getUsers().size()==0)
            throw new EmptyDatabaseException();
        turnstileWork = new Thread(() -> {
                    try {
                        Map<Long, String> users = Users.getInstace().getUsers();
                        while (!turnstileWork.isInterrupted()) {
                            int randomTime = (int) (Math.random() * 10000);
                            Long key;
                            Thread.sleep(randomTime);
                            if(randomTime%5!=0){
                                List<Long> keyList = Users.getInstace().getObservableList();
                                int index = (int)(Math.random() * users.size());
                                key = keyList.get(index);
                            }else{
                                key = (long) (Math.random() * 100000000);
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

    public boolean checkUser (Long password){
        Map<Long, String> users = Users.getInstace().getUsers();
        if(users.containsKey(password)){
            String username = users.get(password);
            EventType eventType = getEventType(username);
            Main.getTurnstileEvents().add(new TurnstileEvent(username, eventType));
            turnstile.open();
            return true;
        }else{
            Main.getTurnstileEvents().add(new TurnstileEvent("Невідома людина", EventType.BLOCKED));
            turnstile.block();
            return false;
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
