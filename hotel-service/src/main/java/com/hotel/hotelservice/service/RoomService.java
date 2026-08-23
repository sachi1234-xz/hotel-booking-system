package com.hotel.hotelservice.service;

import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.entity.Room;

import java.util.List;

public interface RoomService {

    Room addRoomToHotel(Long hotelId, Room room);

    Room getRoomById(Long id);

    List<Room> getRoomsByHotelId(Long hotelId);

    List<Room> getAvailableRooms();

    Room updateRoom(Long id, Room roomDetails);

    void deleteRoom(Long id);
}
