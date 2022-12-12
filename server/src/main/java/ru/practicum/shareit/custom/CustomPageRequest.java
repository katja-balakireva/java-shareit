package ru.practicum.shareit.custom;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    private final int offset;

    public CustomPageRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    public static CustomPageRequest of(int offset, int size) {
        return new CustomPageRequest(offset, size, Sort.unsorted());
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
