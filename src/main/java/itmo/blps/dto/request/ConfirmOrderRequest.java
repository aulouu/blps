package itmo.blps.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrderRequest {
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Delivery time must be in HH:mm format")
    private String deliveryTime;
    private Integer utensilsCount;
}
