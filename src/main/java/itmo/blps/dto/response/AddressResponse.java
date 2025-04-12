package itmo.blps.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String city;
    private String street;
    private Integer building;
    private Integer entrance;
    private Integer floor;
    private Integer flat;
}
