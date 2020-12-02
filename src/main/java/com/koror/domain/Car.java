package com.koror.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table
public class Car {

    @Id
    private Long id;

    private String model;

    private int horsepower;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Person owner;
}
