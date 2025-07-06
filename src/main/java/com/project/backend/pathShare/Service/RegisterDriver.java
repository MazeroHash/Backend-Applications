package com.project.backend.pathShare.Service;

import com.project.backend.pathShare.Entity.Driver;
import com.project.backend.pathShare.Repository.DriverRepository;
import com.project.backend.pathShare.Utility.OpenStreetMapService;
import org.springframework.beans.factory.annotation.Autowired;

public class RegisterDriver {
    @Autowired
    private OpenStreetMapService openStreetMapService; // OSM service
    @Autowired
    DriverRepository driverRepository;

    public Driver registerDriver(Driver driverRequest) {
        Driver driver = new Driver();
        driver.setName(driverRequest.getName());
        driver.setStartLocation(driverRequest.getStartLocation()); // Store place name

        // Convert place name to coordinates
        double[] coords = openStreetMapService.getCoordinates(driverRequest.getStartLocation());
        double startLat = coords[0];
        double startLon = coords[1];

        System.out.println("Driver Location: " + driver.getStartLocation() + " → Lat: " + startLat + ", Lon: " + startLon);

        driverRepository.save(driver);
        return driver;
    }

}
