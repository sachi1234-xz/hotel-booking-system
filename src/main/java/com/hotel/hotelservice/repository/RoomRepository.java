package com.hotel.hotelservice.repository;

import com.hotel.hotelservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @NonNull
    List<Room> findByHotelId(@NonNull Long hotelId);

    @NonNull
    List<Room> findByIsAvailableTrue();

    boolean existsByHotelId(@NonNull Long hotelId);
}
