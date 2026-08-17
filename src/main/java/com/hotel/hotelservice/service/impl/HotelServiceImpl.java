package com.hotel.hotelservice.service.impl;

import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.exception.HotelNotFoundException;
import com.hotel.hotelservice.repository.HotelRepository;
import com.hotel.hotelservice.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public Hotel createHotel(Hotel hotel) {
        Hotel saved = hotelRepository.save(hotel);
        log.info("Created hotel with id {}: {}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        log.debug("Returning {} hotels", hotels.size());
        return hotels;
    }

    @Override
    @Transactional(readOnly = true)
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    @Override
    @Transactional
    public Hotel updateHotel(Long id, Hotel hotelDetails) {
        Hotel hotel = getHotelById(id);
        hotel.setName(hotelDetails.getName());
        hotel.setLocation(hotelDetails.getLocation());
        hotel.setDescription(hotelDetails.getDescription());
        Hotel updated = hotelRepository.save(hotel);
        log.info("Updated hotel with id {}", id);
        return updated;
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        getHotelById(id);
        hotelRepository.deleteById(id);
        log.info("Deleted hotel with id {}", id);
    }
}
