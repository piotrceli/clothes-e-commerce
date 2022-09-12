package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.model.Coordinate;

public interface GeocodingService {

    Coordinate getCoordinates(String city, String country);
}
