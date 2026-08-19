package com.hotel.hotelservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "rooms")
@Schema(description = "A hotel that can host bookings")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the hotel", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank(message = "Hotel name is required")
    @Column(nullable = false, length = 150)
    @Schema(description = "Name of the hotel", example = "Grand Plaza Hotel")
    private String name;

    @NotBlank(message = "Hotel location is required")
    @Column(nullable = false, length = 200)
    @Schema(description = "City or address where the hotel is located", example = "New York")
    private String location;

    @Column(length = 1000)
    @Schema(description = "Short description of the hotel", example = "A luxury 5-star hotel in the heart of the city.")
    private String description;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    @Schema(description = "Rooms belonging to this hotel")
    private List<Room> rooms = new ArrayList<>();
}
