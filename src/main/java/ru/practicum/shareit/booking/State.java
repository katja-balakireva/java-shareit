package ru.practicum.shareit.booking;

public enum State {
    WAITING,
    APPROVED,
    REJECTED,
    //CANCELLED;
    CURRENT,
    PAST,
    FUTURE,
    ALL;

    static State from(String state) {
        for (State value: State.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        return null;
    }
}
