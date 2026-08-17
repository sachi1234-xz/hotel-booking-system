package com.hotel.hotelservice.repository;

import com.hotel.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Override
    @EntityGraph(attributePaths = "rooms")
    List<Hotel> findAll();

    @Override
    @EntityGraph(attributePaths = "rooms")
    Optional<Hotel> findById(Long id);
}
