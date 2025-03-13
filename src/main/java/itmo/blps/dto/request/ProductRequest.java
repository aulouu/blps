package itmo.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private Double amount;
    private String restaurant;
}
