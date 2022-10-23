package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
  private final HotelService hotelService;

  @Autowired
  public SearchController(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @GetMapping("/{cityId}?sortBy={distance}")
  @ResponseStatus(HttpStatus.OK)
  public List<Hotel> searchNearestHotels(@PathVariable Long cityId,
                              @RequestParam(name = "sortBy") String sortParam) {
    if(!sortParam.equals("distance")){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    return hotelService.searchNearestToCityCenter(cityId);
  }

}
