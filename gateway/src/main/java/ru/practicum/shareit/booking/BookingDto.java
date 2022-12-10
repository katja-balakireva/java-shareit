package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long itemId;
    @FutureOrPresent(message = "Дата начала не может быть в прошлом", groups = {Create.class})
    private LocalDateTime start;
    @Future(message = "Дата окончания не может быть в прошлом", groups = {Create.class})
    private LocalDateTime end;
}
