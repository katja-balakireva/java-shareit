package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long itemId;
    @NotNull(message = "Дата начала не может быть null", groups = {Create.class})
    @FutureOrPresent(message = "Дата начала не может быть в прошлом", groups = {Create.class})
    private LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть null", groups = {Create.class})
    @Future(message = "Дата окончания не может быть в прошлом", groups = {Create.class})
    private LocalDateTime end;
}
