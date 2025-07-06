package com.project.backend.pathShare.Controller;

import com.project.backend.pathShare.Dto.FareCalculationResultDto;
import com.project.backend.pathShare.Dto.RideRequestDTO;
import com.project.backend.pathShare.Entity.Ride;
import com.project.backend.pathShare.Service.FareEngineService;
import com.project.backend.pathShare.Service.RideMatchingService;
import com.project.backend.pathShare.Service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/rides")
public class RideController {

    @Autowired
    RideService rideService;

    @Autowired
    RideMatchingService rideMatchingService;

    @Autowired
    FareEngineService fareEngineService;

    @PostMapping(path="/request")
    public ResponseEntity<Ride>requestRide(@RequestBody RideRequestDTO rideRequestDTO){
       Ride savedRide= rideService.createRide(rideRequestDTO);
       return new  ResponseEntity<>(savedRide, HttpStatus.CREATED);
    }

    @PostMapping("/matchRide")
    public ResponseEntity<Ride> matchRide(@RequestBody RideRequestDTO rideRequest) {
        Ride matchedRide = rideMatchingService.matchRide(
                rideRequest.getPickupLocation(),
                rideRequest.getDropLocation(),
                rideRequest.getRiderName()
        );

        return new ResponseEntity<>(matchedRide, HttpStatus.OK);
    }
    @PostMapping("/fareEstimate")
    public ResponseEntity<FareCalculationResultDto>getFareEstimate(@RequestBody RideRequestDTO rideRequestDTO){
        FareCalculationResultDto estimatedFare= fareEngineService.calculateFareMetrics(rideRequestDTO.getPickupLocation(),rideRequestDTO.getDropLocation());
        return ResponseEntity.ok(estimatedFare);
    }
}
