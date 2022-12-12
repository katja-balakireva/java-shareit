package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым", groups = {Create.class})
    private String name;
    @NotBlank(message = "email не может быть пустым", groups = {Create.class})
    @Email(message = "Введён неправильный email", groups = {Create.class, Update.class})
    private String email;
}
