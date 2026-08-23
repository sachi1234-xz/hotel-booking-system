package com.hotel.hotelservice.controller;

import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.service.HotelService;
import com.hotel.hotelservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hotel Management", description = "Endpoints to manage hotels")
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;

    public HotelController(HotelService hotelService, RoomService roomService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Create a new hotel", description = "Creates a hotel and returns the persisted entity with its generated id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody Hotel hotel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(hotel));
    }

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Returns the full list of hotels")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of hotels returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by id", description = "Returns a single hotel matching the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel found"),
            @ApiResponse(responseCode = "404", description = "No hotel found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hotel", description = "Updates name, location and description of the hotel with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "No hotel found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @Valid @RequestBody Hotel hotelDetails) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotelDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hotel", description = "Deletes the hotel with the given id together with its rooms")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No hotel found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{hotelId}/rooms")
    @Operation(summary = "Add a room to a hotel", description = "Creates a new room and associates it with the hotel with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "No hotel found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Room> addRoomToHotel(@PathVariable Long hotelId, @Valid @RequestBody Room room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoomToHotel(hotelId, room));
    }

    @GetMapping("/{hotelId}/rooms")
    @Operation(summary = "Get all rooms of a hotel", description = "Returns the list of rooms belonging to the hotel with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of rooms returned successfully"),
            @ApiResponse(responseCode = "404", description = "No hotel found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<List<Room>> getRoomsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }
}
