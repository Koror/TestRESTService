package com.koror.pojo;

import java.time.LocalDate;
import java.util.List;

public class PersonWithCars {
    private Long id;
    private String name;
    private LocalDate birthdate;
    private List<CarPojo> cars;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public List<CarPojo> getCars() {
        return cars;
    }

    public void setCars(List<CarPojo> cars) {
        this.cars = cars;
    }
}
