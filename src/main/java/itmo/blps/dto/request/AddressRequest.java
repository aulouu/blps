package itmo.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class AddressRequest {
    private String city;
    private String street;
    private String building;
    private String entrance;
    private String floor;
    private String flat;

}
