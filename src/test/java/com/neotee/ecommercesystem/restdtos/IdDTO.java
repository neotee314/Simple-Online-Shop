package com.neotee.ecommercesystem.restdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

/**
 * A DTO containing just the id of an entity, used in testing.
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdDTO {
    private UUID id;

    public UUID getId() {
        return id;
    }
}

