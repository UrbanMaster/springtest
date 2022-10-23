package com.booking.recruitment.hotel.repository;

import com.booking.recruitment.hotel.model.Hotel;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Where(clause = "deleted=false")
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Modifying
    @Query("update Hotel h set h.deleted = true where h.id = :id")
    Optional<Hotel> deleteByIdLogically(Long id);
}
