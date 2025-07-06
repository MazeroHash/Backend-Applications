package com.project.backend.pathShare.Utility;

import com.project.backend.pathShare.Entity.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public  class FareCalculation {
    @Autowired
    private OpenStreetMapService openStreetMapService; // OSM for coordinates

    public  double calculateDynamicFare(double baseFare, double sharedDistance, double totalDistance, double estimatedTime) {
        double sharedPercentage = sharedDistance / totalDistance;
        double discountFactor = (sharedPercentage > 0.75) ? 0.4 : (sharedPercentage > 0.5) ? 0.3 : 0.2;


        double distanceCost = (totalDistance - (sharedDistance * discountFactor)) * RideConstants.COST_PER_KM;
        double timeCost = estimatedTime * RideConstants.COST_PER_MINUTE;

        return baseFare + distanceCost + timeCost;
    }



    public  double calculateSharedDistance(Ride driverRide, double userPickupLat, double userPickupLon, double userDropLat, double userDropLon) {
        double[] driverCoords = openStreetMapService.getCoordinates(driverRide.getDriverLocation());
        double[] driverPreRequestedUserDropCoords = openStreetMapService.getCoordinates(driverRide.getPreRequestedDropLocation());
        double driverStartLat =driverCoords[0];
        double driverStartLon = driverCoords[1];
        double driverDropLat = driverPreRequestedUserDropCoords[0];
        double driverDropLon = driverPreRequestedUserDropCoords[1];

        double userDistance = calculateDistance(userPickupLat, userPickupLon, userDropLat, userDropLon); // Uses Haversine formula
        double sharedDistance = Math.min(
                calculateDistance(driverStartLat, driverStartLon, userPickupLat, userPickupLon) +
                        calculateDistance(userPickupLat, userPickupLon, driverDropLat, driverDropLon),
                userDistance
        );

        return sharedDistance;
    }


    public  double calculateRidePrice(double pickupLat,double pickupLon,double dropLat,double dropLon){
        double distance=calculateDistance(pickupLat,pickupLon,dropLat,dropLon);
        return (distance*RideConstants.COST_PER_KM);
    }

    public  double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
