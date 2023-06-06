package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "name"})
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int inventory;

    @OneToOne
    private Deal deal;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private List<BasketItem> items;

}
