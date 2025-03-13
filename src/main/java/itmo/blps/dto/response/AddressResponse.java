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
    private String building;
    private String entrance;
    private String floor;
    private String flat;

}
