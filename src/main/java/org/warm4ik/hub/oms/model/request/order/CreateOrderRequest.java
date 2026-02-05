package org.warm4ik.hub.oms.model.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest implements Serializable {

    @NotNull
    private UUID userId; // TODO: после добавления авторизации исправить
    @NotBlank
    private String description;

}
