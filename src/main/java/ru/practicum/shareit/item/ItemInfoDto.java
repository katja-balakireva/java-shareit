package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentInfoDto> comments;


//    public static class BookingDto {
//        Long id;
//        //LocalDateTime start;
//        //LocalDateTime end;
//        Long bookerId;
//
//        public Long getId() {
//            return id;
//        }
//
//        public Long getBookerId() {
//            return bookerId;
//        }
//    }


}
