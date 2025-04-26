package itmo.blps.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String correlationId;
    private String number;
    private Double money;
}
