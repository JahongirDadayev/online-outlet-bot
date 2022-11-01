package com.example.onlineshopbot.entity;

import com.example.onlineshopbot.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(value = AuditingEntityListener.class)
@Entity
public class DbOrder extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "db_user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbUser dbUser;

    @JoinTable(
            name = "db_orders_db_products",
            joinColumns = {@JoinColumn(name = "db_order_id")},
            inverseJoinColumns = {@JoinColumn(name = "db_product_id")}
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private List<DbProduct> dbProducts;

    @JoinColumn(name = "db_address_id")
    @OneToOne(fetch = FetchType.EAGER)
    private DbAddress dbAddress;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "date_of_delivery")
    private LocalDateTime dateOfDelivery;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
}
