package com.booking.recruitment.hotel.repository;

import com.booking.recruitment.hotel.model.Hotel;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Where(clause = "deleted=false")
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Modifying
    @Query("update Hotel h set h.deleted = true where h.id = :id")
    void deleteByIdLogically(Long id);

    @Query(value = "SELECT *, ( 3959 * acos( cos( radians(:cityCentreLatitude) ) * cos( radians( h.latitude ) ) * cos( radians(h.longitude) - radians(:cityCentreLongitude) ) + sin( radians(:cityCentreLatitude) ) * sin( radians(h.latitude)))) AS distance " +
            "FROM hotel h " +
            "ORDER BY distance ASC " +
            "LIMIT 3;",
            nativeQuery = true)
    List<Hotel> searchNearestTo(double cityCentreLatitude, double cityCentreLongitude);
}
