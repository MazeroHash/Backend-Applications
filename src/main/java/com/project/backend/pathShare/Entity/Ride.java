package com.project.backend.pathShare.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String driverName;
    private String driverLocation;
    private String preRequestedPickupLocation;
    private String preRequestedDropLocation;
    private String pickupLocation;
    private String dropLocation;
    private double price;  // Added field for ride pricing
    private boolean isDriverAvailable;  // Added field to track driver availability

    @ElementCollection
    private List<String> passengers;
}
