package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    private String userName;
    private Long userId;
    private Double totalPrice;
    private Map<Long, FinalOffer> items;

}
