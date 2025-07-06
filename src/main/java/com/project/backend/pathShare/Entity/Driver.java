package com.project.backend.pathShare.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DRIVER")
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String startLocation;
    private boolean available;
    private boolean transitToPickup;     // ✅ Track if driver is moving toward P1

    public void updateLocation(double latitude, double longitude) {

    }
}
