package com.project.backend.pathShare.Service;

import com.project.backend.pathShare.Dto.FareCalculationResultDto;
import com.project.backend.pathShare.Dto.RideRequestDTO;
import com.project.backend.pathShare.Entity.Ride;
import com.project.backend.pathShare.Repository.RideRepository;
import com.project.backend.pathShare.Utility.FareCalculation;
import com.project.backend.pathShare.Utility.OpenRouteService;
import com.project.backend.pathShare.Utility.OpenStreetMapService;
import com.project.backend.pathShare.Utility.RideConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RideMatchingService {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    private OpenStreetMapService openStreetMapService; // OSM for coordinates

    @Autowired
    private FareCalculation fareCalculation;

    @Autowired
    private OpenRouteService openRouteService; // ORS for route validation

    @Autowired
    private RideNotificationHandler rideNotificationHandler;

    @Autowired
    FareEngineService fareEngineService;


    public Ride matchRide(String pickupLocation, String dropLocation, String riderName) {
        double[] newPickupCoords = openStreetMapService.getCoordinates(pickupLocation);
        double[] newDropCoords = openStreetMapService.getCoordinates(dropLocation);

        // Find drivers
        List<Ride> allRides = rideRepository.findAll();
        List<Ride> eligibleDrivers = FindAllTheAvailableEligibleRides(allRides, newPickupCoords, newDropCoords);

        Ride bookedRide = assignDriverForRide(eligibleDrivers, newPickupCoords, newDropCoords, riderName, pickupLocation, dropLocation);
        if (bookedRide != null) {
            return bookedRide;
        } else {
            return bookRideThroughAPI(riderName, pickupLocation, dropLocation);
        }
    }

    private List<Ride> FindAllTheAvailableEligibleRides(List<Ride> allRides, double[] newPickupCoords, double[] newDropCoords) {
        List<Ride> availableDrivers = new ArrayList<>();
        for (Ride ride : allRides) {
            double[] driverCoords = openStreetMapService.getCoordinates(ride.getDriverLocation()); // Convert driver location to lat/lon
            System.out.println("driverCoords: " + driverCoords[0] + ", " + driverCoords[1]);
            double[] rideCoordsPreRequestedPickupCoords = openStreetMapService.getCoordinates(ride.getPreRequestedPickupLocation());
            double[] rideCoordsPreRequestedDropCoords = openStreetMapService.getCoordinates(ride.getPreRequestedDropLocation());
            System.out.println("rideCoordsPickup: " + rideCoordsPreRequestedPickupCoords[0] + ", " + rideCoordsPreRequestedPickupCoords[1]);
            if (Math.abs(newPickupCoords[0] - driverCoords[0]) <= 2 &&
                    Math.abs(newPickupCoords[1] - driverCoords[1]) <= 2 &&
                    (newDropCoords[0] < rideCoordsPreRequestedPickupCoords[0] || newDropCoords[1] < rideCoordsPreRequestedPickupCoords[1]) &&
                    openRouteService.isValidRoute(driverCoords[0], driverCoords[1], newPickupCoords[0], newPickupCoords[1], newDropCoords[0], newDropCoords[1], rideCoordsPreRequestedPickupCoords[0], rideCoordsPreRequestedPickupCoords[1])) {

                availableDrivers.add(ride);
            }

        }
        List<Ride> eligibleDrivers = availableDrivers.stream()
                .sorted(Comparator.comparingDouble(d -> fareCalculation.calculateDistance(
                        openStreetMapService.getCoordinates(d.getDriverLocation())[0],
                        openStreetMapService.getCoordinates(d.getDriverLocation())[1],
                        openStreetMapService.getCoordinates(d.getPickupLocation())[0],
                        openStreetMapService.getCoordinates(d.getPickupLocation())[1]
                ))) // ✅ Sort by proximity to pickup
                .toList();


        System.out.println("Drivers en route: " + availableDrivers.size());
        for (Ride ride : eligibleDrivers) {
            System.out.println(ride.getDriverName());
        }
        return availableDrivers;
    }

    private Ride assignDriverForRide(List<Ride> eligibleDrivers, double[] newPickupCoords, double[] newDropCoords, String riderName, String pickupLocation, String dropLocation) {

        for (Ride ride : eligibleDrivers) {
            // 🔍 Convert driver's location (place name) into lat/lon using OpenStreetMap
            double[] driverCoords = openStreetMapService.getCoordinates(ride.getDriverLocation());
            double[] rideCoordsPickup = openStreetMapService.getCoordinates(ride.getPreRequestedPickupLocation());
            if (ride.getPassengers().size() < RideConstants.MAX_PASSENGERS && ride.isDriverAvailable()) {
                System.out.println("ride.getPassengers().size() < RideConstants.MAX_PASSENGERS && ride.isDriverAvailable() true");
                if (openRouteService.isValidRoute(
                        driverCoords[0], driverCoords[1],
                        rideCoordsPickup[0], rideCoordsPickup[1],
                        newPickupCoords[0], newPickupCoords[1],
                        newDropCoords[0], newDropCoords[1]
                )) {
                    // Calculate Shared Distance & Adjust Price

                    FareCalculationResultDto result = fareEngineService.calculateSharedFareMetrics(ride, newPickupCoords, newDropCoords);
                    // Assign User 2 to the driver
                    System.out.println("Assigning Bob to driver: " + ride.getDriverName()); // Debug log
                    ride.getPassengers().add(riderName);
                    ride.setPrice(result.getFinalFare());
                    ride.setPickupLocation(pickupLocation);
                    ride.setDropLocation(dropLocation);
                    rideRepository.save(ride);
                    return ride;
                }
            }
        }
        return null;
    }

    public Ride bookRideThroughAPI(String riderName, String pickupLocation, String dropLocation) {
        RestTemplate restTemplate = new RestTemplate();

        //  Create the ride request payload
        RideRequestDTO request = new RideRequestDTO();
        request.setRiderName(riderName);
        request.setPickupLocation(pickupLocation);
        request.setDropLocation(dropLocation);

        // Call the ride request API
        ResponseEntity<Ride> response = restTemplate.postForEntity(
                "http://localhost:8081/rides/request",
                request,
                Ride.class
        );

        return response.getBody(); //  Return the booked ride
    }


}

