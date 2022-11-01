package com.example.onlineshopbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DbAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_number")
    private Integer homeNumber;

    @Column(name = "street_name")
    private String streetName;

    @JoinColumn(name = "db_district_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DbDistrict dbDistrict;
}
