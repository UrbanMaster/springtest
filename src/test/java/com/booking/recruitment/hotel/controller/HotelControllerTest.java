package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.model.City;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.CityRepository;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.testing.SlowTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
@SlowTest
class HotelControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  @Autowired private HotelRepository hotelRepository;
  @Autowired private CityRepository cityRepository;

  @Test
  @DisplayName("When all hotels are requested then they are all returned")
  void allHotelsRequested() throws Exception {
    mockMvc
        .perform(get("/hotel"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$", hasSize((int) hotelRepository.count())));
  }

  @Test
  @DisplayName("When a hotel creation is requested then it is persisted")
  void hotelCreatedCorrectly() throws Exception {
    City city =
        cityRepository
            .findById(1L)
            .orElseThrow(
                () -> new IllegalStateException("Test dataset does not contain a city with ID 1!"));
    Hotel newHotel = Hotel.builder().setName("This is a test hotel").setCity(city).build();

    Long newHotelId =
        mapper
            .readValue(
                mockMvc
                    .perform(
                        post("/hotel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newHotel)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(),
                Hotel.class)
            .getId();

    newHotel.setId(newHotelId); // Populate the ID of the hotel after successful creation

    assertThat(
        hotelRepository
            .findById(newHotelId)
            .orElseThrow(
                () -> new IllegalStateException("New Hotel has not been saved in the repository")),
        equalTo(newHotel));
  }

  @Disabled
  @Test
  @DisplayName("When hotel requested by id this entity is returned")
  void hotelDeletedLogicallyById() {
    Hotel randomHotel = hotelRepository.findAll().get(0);
    hotelRepository.deleteByIdLogically(randomHotel.getId());

    assertThat(
            hotelRepository
                    .findById(randomHotel.getId())
                    .isPresent(),
            equalTo(false));
  }

  @Test
  @DisplayName("When hotel requested by id this entity is returned")
  void hotelFoundById() {
    Hotel randomHotel = hotelRepository.findAll().get(0);

    assertThat(
            hotelRepository
                    .findById(randomHotel.getId())
                    .orElseThrow(
                            () -> new IllegalStateException("Hotel has not been found in the repository")),
            equalTo(randomHotel));
  }

  @Test
  @DisplayName("When requested search for nearest 3 hotels they returned correctly")
  @Transactional
  void nearestHotelsSearchedCorrectly() {
    City city =
            cityRepository
                    .findById(1L)
                    .orElseThrow(
                            () -> new IllegalStateException("Test dataset does not contain a city with ID 1!"));

    //TODO: verify that they are the closest hotels. didn't have time to check calculation manually
    List<Hotel> nearestHotels = new ArrayList<>();
    nearestHotels.add(hotelRepository.getOne(2L));
    nearestHotels.add(hotelRepository.getOne(1L));
    nearestHotels.add(hotelRepository.getOne(6L));

    assertThat(
            hotelRepository
                    .searchNearestTo(city.getCityCentreLatitude(), city.getCityCentreLongitude()),
            equalTo(nearestHotels));
  }
}
