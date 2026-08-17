package com.hotel.hotelservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "room_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "hotel")
@Schema(description = "A bookable room inside a hotel")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the room", accessMode = Schema.AccessMode.READ_ONLY, example = "101")
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false, length = 20)
    @Schema(description = "Room number as displayed on the door", example = "101")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Type of the room", example = "DOUBLE")
    private RoomType type;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", message = "Price per night cannot be negative")
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Price per night in the hotel's currency", example = "189.99")
    private BigDecimal pricePerNight;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the room is currently available", example = "true")
    private boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    private Hotel hotel;
}
