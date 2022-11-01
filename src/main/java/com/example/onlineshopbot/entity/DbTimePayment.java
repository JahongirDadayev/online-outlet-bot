package com.example.onlineshopbot.entity;

import com.example.onlineshopbot.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DbTimePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "db_user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbUser dbUser;

    @Column(name = "user_company_name")
    private String userCompanyName;

    @Column(name = "user_position")
    private String userPosition;

    @Column(name = "other_phone_number")
    private String otherPhoneNumber;

    @Column(name = "payment_type")
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_validity_period")
    private String cardValidityPeriod;

    @Column(name = "card_phone_number")
    private String cardPhoneNumber;

    @JoinTable(
            name = "db_time_payments_db_attachment",
            joinColumns = {@JoinColumn(name = "db_time_payment_id")},
            inverseJoinColumns = {@JoinColumn(name = "db_attachment_id")}
    )
    @OneToMany(fetch = FetchType.EAGER)
    private List<DbProduct> dbProducts;

    @JoinColumn(name = "db_product_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbProduct dbProduct;
}
