package com.project.backend.pathShare.Repository;

import com.project.backend.pathShare.Entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    @Query("SELECT d FROM Driver d WHERE d.available = true")
    List<Driver> findAvailableDrivers();
}
