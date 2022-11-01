package com.example.onlineshopbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(value = AuditingEntityListener.class)
@Entity
public class DbProduct extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "db_company_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbCompany dbCompany;

    @JoinColumn(name = "db_catalog_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbCatalog dbCatalog;

    @JoinTable(
            name = "db_products_db_attachment",
            joinColumns = {@JoinColumn(name = "db_product_id")},
            inverseJoinColumns = {@JoinColumn(name = "db_attachment_id")}
    )
    @OneToMany(fetch = FetchType.LAZY)
    private List<DbAttachment> dbAttachments;
}
