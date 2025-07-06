package com.project.backend.pathShare.Controller;

import com.project.backend.pathShare.Dto.DriverAvailabilityDto;
import com.project.backend.pathShare.Dto.DriverLocationDTO;
import com.project.backend.pathShare.Entity.Driver;
import com.project.backend.pathShare.Repository.DriverRepository;
import com.project.backend.pathShare.Utility.OpenStreetMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/drivers")
public class DriverController {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    OpenStreetMapService openStreetMapService;

    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver) {
        driver.setAvailable(true); // ✅ Mark driver as available
        return new ResponseEntity<>(driverRepository.save(driver), HttpStatus.CREATED);
    }

    @PostMapping("/driver/update-location")
    public ResponseEntity<?> updateDriverLocation(@RequestBody DriverLocationDTO location) {
        Driver driver = driverRepository.findById(location.getDriverId()).orElseThrow();
        driver.updateLocation(location.getLatitude(), location.getLongitude());
        //  Convert lat/lon back to an address & update startLocation
        String updatedAddress = openStreetMapService.getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
        driver.setStartLocation(updatedAddress);
        driverRepository.save(driver);
        return ResponseEntity.ok("Driver location updated!");
    }
    @PostMapping("/driver/{id}/availability")
    public ResponseEntity<String>updateAvailability(@PathVariable Long id,@RequestBody DriverAvailabilityDto dto){
        Driver driver=driverRepository.findById(id).orElseThrow(()->new RuntimeException("Driver not found with id: "+id));

        driver.setAvailable(dto.isAvailable());
        driverRepository.save(driver);
        return ResponseEntity.ok("Driver availability set to: "+dto.isAvailable());
    }

}
