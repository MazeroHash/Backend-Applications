package com.project.backend.pathShare.Dto;

import lombok.Data;

@Data
public class RideRequestDTO {
    private String riderName;
    private String pickupLocation;
    private String dropLocation;
}
