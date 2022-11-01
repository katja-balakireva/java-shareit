package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.UnsupportedStateException;

public enum State {
    WAITING,
    APPROVED,
    REJECTED,
    CURRENT,
    PAST,
    FUTURE,
    ALL;

    static State from(String state) {
        for (State value : State.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
    }
}
