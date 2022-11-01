package com.example.onlineshopbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DbAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_original_name")
    private String fileOriginalName;

    @Column(name = "file_name")
    private String file_name;

    @Column(name = "size")
    private Double size;

    @Column(name = "content_type")
    private String contentType;
}
