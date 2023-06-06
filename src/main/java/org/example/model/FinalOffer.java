package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinalOffer {
    private String dealApplied;
    private Integer quantity;
    private Double price;

}
