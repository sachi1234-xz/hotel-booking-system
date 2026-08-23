package com.hotel.hotelservice.service.impl;

import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.exception.HotelNotFoundException;
import com.hotel.hotelservice.exception.RoomNotFoundException;
import com.hotel.hotelservice.repository.HotelRepository;
import com.hotel.hotelservice.repository.RoomRepository;
import com.hotel.hotelservice.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public Room addRoomToHotel(Long hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
        room.setHotel(hotel);
        Room saved = roomRepository.save(room);
        log.info("Added room {} to hotel {}", saved.getRoomNumber(), hotelId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHotelId(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        log.debug("Returning {} rooms for hotel {}", rooms.size(), hotelId);
        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        List<Room> rooms = roomRepository.findByIsAvailableTrue();
        log.debug("Returning {} available rooms", rooms.size());
        return rooms;
    }

    @Override
    @Transactional
    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setType(roomDetails.getType());
        room.setPricePerNight(roomDetails.getPricePerNight());
        room.setAvailable(roomDetails.isAvailable());
        Room updated = roomRepository.save(room);
        log.info("Updated room with id {}", id);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
        log.info("Deleted room with id {}", id);
    }
}
