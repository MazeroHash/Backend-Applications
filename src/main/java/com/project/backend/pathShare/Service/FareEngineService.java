package com.project.backend.pathShare.Service;

import com.project.backend.pathShare.Dto.FareCalculationResultDto;
import com.project.backend.pathShare.Entity.Ride;
import com.project.backend.pathShare.Utility.FareCalculation;
import com.project.backend.pathShare.Utility.OpenRouteService;
import com.project.backend.pathShare.Utility.OpenStreetMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FareEngineService {

    @Autowired
    private OpenStreetMapService openStreetMapService;

    @Autowired
    private OpenRouteService openRouteService;

    @Autowired
    private FareCalculation fareCalculation;

    public FareCalculationResultDto calculateFareMetrics(String pickupLocation, String dropLocation) {
        double[] pickupCoords = openStreetMapService.getCoordinates(pickupLocation);
        double[] dropCoords = openStreetMapService.getCoordinates(dropLocation);

        double sharedDistance = 0; // Default if not in shared context
        double totalDistance = fareCalculation.calculateDistance(pickupCoords[0], pickupCoords[1], dropCoords[0], dropCoords[1]);
        double estimatedTime = openRouteService.getEstimatedTravelTime(pickupCoords[0], pickupCoords[1], dropCoords[0], dropCoords[1]);
        double baseFare = fareCalculation.calculateRidePrice(pickupCoords[0], pickupCoords[1], dropCoords[0], dropCoords[1]);
        double finalFare = fareCalculation.calculateDynamicFare(baseFare, sharedDistance, totalDistance, estimatedTime);

        return new FareCalculationResultDto(baseFare, finalFare, totalDistance, estimatedTime);
    }

    public FareCalculationResultDto calculateSharedFareMetrics(Ride sharedRide, double[] newPickupCoords, double[] newDropCoords) {
        double sharedDistance = fareCalculation.calculateSharedDistance(sharedRide,
                newPickupCoords[0], newPickupCoords[1],
                newDropCoords[0], newDropCoords[1]);

        double totalDistance = fareCalculation.calculateDistance(
                newPickupCoords[0], newPickupCoords[1],
                newDropCoords[0], newDropCoords[1]);

        double estimatedTime = openRouteService.getEstimatedTravelTime(
                newPickupCoords[0], newPickupCoords[1],
                newDropCoords[0], newDropCoords[1]);

        double baseFare = fareCalculation.calculateRidePrice(
                newPickupCoords[0], newPickupCoords[1],
                newDropCoords[0], newDropCoords[1]);

        double finalFare = fareCalculation.calculateDynamicFare(baseFare, sharedDistance, totalDistance, estimatedTime);

        return new FareCalculationResultDto(baseFare, finalFare, totalDistance, estimatedTime);
    }
}

