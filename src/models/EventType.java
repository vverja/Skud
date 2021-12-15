package models;

/**
 * Перечисление описывающее разновидности событий на турникете
 */
public enum EventType {
    IN("Увійшов"),
    OUT("Вийшов"),
    BLOCKED("Доступ заборонено");

    private String name;

    EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
