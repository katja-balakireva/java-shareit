package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private Long requestId;
    @NotBlank(message = "Имя не может быть пустым", groups = {Create.class})
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = {Create.class})
    private String description;
    @NotNull(message = "Не задано значение доступности", groups = {Create.class})
    private Boolean available;
}
