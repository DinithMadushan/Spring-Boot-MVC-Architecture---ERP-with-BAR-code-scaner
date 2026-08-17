package com.supermarket.erp.service;

import com.supermarket.erp.entity.Location;

import java.util.List;

public interface LocationService {

    List<Location> getAllLocations();

    Location getLocationById(Long id);

    Location saveLocation(Location location);

    void deleteLocation(Long id);

}
