package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата начала не может быть null", groups = {Create.class})
    @FutureOrPresent(message = "Дата начала не может быть в прошлом", groups = {Create.class})
    private LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть null", groups = {Create.class})
    @Future(message = "Дата окончания не может быть в прошлом", groups = {Create.class})
    private LocalDateTime end;
    @NotNull(message = "id вещи не может быть null", groups = {Create.class})
    private Long itemId;
    private Long bookerId;
    private State status;
}
