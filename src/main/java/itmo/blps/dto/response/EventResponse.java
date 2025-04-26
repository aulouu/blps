package itmo.blps.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String correlationId;
    private boolean success;
}
