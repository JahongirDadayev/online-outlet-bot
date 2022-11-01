package com.example.onlineshopbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DbBasket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "db_user_id")
    @OneToOne
    private DbUser dbUser;

    @JoinTable(
            name = "db_baskets_db_products",
            joinColumns = {@JoinColumn(name = "db_basket_id")},
            inverseJoinColumns = {@JoinColumn(name = "db_product_id")}
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private List<DbProduct> dbProducts;
}
