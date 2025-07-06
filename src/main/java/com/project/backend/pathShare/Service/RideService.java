package com.project.backend.pathShare.Service;


import com.project.backend.pathShare.Dto.RideRequestDTO;
import com.project.backend.pathShare.Entity.Driver;
import com.project.backend.pathShare.Entity.Ride;
import com.project.backend.pathShare.Repository.DriverRepository;
import com.project.backend.pathShare.Repository.RideRepository;
import com.project.backend.pathShare.Utility.FareCalculation;
import com.project.backend.pathShare.Utility.OpenStreetMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RideService {
    @Autowired
    RideRepository rideRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    private OpenStreetMapService openStreetMapService;

    @Autowired
    FareCalculation fareCalculation;

    @Autowired
    RideMatchingService rideMatchingService;

    public Ride createRide(RideRequestDTO rideRequest) {
        Ride ride = new Ride();
        ride.setPreRequestedDropLocation(rideRequest.getDropLocation());
        ride.setPreRequestedPickupLocation(rideRequest.getPickupLocation());
        ride.setPassengers(Collections.singletonList(rideRequest.getRiderName()));

        double[] NormalPickupCoords = openStreetMapService.getCoordinates(rideRequest.getPickupLocation());
        double[] NormaalDropCoords = openStreetMapService.getCoordinates(rideRequest.getDropLocation());


        // 🔍 Fetch coordinates


        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        List<Driver> eligibleDrivers = availableDrivers.stream()
                .filter(d -> d.isAvailable() && !d.isTransitToPickup()) // ✅ Ensure driver is free
                .sorted(Comparator.comparingDouble(d ->fareCalculation.calculateDistance(
                        openStreetMapService.getCoordinates(d.getStartLocation())[0],
                        openStreetMapService.getCoordinates(d.getStartLocation())[1],
                        NormalPickupCoords[0],
                        NormaalDropCoords[1]
                ))) // ✅ Sort by proximity to pickup
                .toList();

        if (!eligibleDrivers.isEmpty()) {

            Driver assignedDriver = eligibleDrivers.get(0);
            assignedDriver.setTransitToPickup(true); // ✅ Driver is moving toward pickup
            driverRepository.save(assignedDriver);

            ride.setDriverName(assignedDriver.getName());
            ride.setDriverLocation(assignedDriver.getStartLocation());
            ride.setDriverAvailable(true); // ✅ Driver can pick up User 2 (L → M)

        }
        double[] rideCoordsPickup = openStreetMapService.getCoordinates(rideRequest.getPickupLocation());
        double[] rideCoordsDrop = openStreetMapService.getCoordinates(rideRequest.getDropLocation());
        ride.setPrice(fareCalculation.calculateRidePrice(rideCoordsPickup[0], rideCoordsPickup[1], rideCoordsDrop[0], rideCoordsDrop[1]));

        return rideRepository.save(ride);

    }


}
