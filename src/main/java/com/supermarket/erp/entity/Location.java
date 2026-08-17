package com.supermarket.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name is required")
    @Size(max = 100)
    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @NotBlank(message = "Address is required")
    @Size(max = 255)
    @Column(nullable = false)
    private String address;

    public Location() {
    }

    public Location(String locationName, String address) {
        this.locationName = locationName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
