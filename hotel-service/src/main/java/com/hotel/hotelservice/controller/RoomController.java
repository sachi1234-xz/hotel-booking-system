package com.hotel.hotelservice.controller;

import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Management", description = "Endpoints to manage rooms across hotels")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available rooms across hotels", description = "Returns every room with availability set to true, regardless of hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of available rooms returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by ID", description = "Returns a single room with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room returned successfully"),
            @ApiResponse(responseCode = "404", description = "No room found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room", description = "Updates room number, type, price and availability of the room with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "No room found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room roomDetails) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Deletes the room with the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No room found with the given id"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
