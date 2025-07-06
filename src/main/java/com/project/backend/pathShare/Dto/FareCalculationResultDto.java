package com.project.backend.pathShare.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareCalculationResultDto {
    private double baseFare;
    private double finalFare;
    private double totalDistance;
    private double estimatedTime;
}
