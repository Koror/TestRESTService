package com.koror.repo;

import com.koror.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CarRepo extends JpaRepository<Car, Long> {

    @Query("SELECT DISTINCT COUNT(c.model) FROM Car c")
    Long uniqueVendorCount();
}
