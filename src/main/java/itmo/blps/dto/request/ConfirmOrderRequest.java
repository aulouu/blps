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
    private String deliveryTime;
    private Integer utensilsCount;
}
