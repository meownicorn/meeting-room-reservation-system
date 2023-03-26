package com.jolyn.meetingroomreservationsystem.controller;

import com.jolyn.meetingroomreservationsystem.domain.Reservation;
import com.jolyn.meetingroomreservationsystem.domain.dto.CreateReservationDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.ReservationDto;
import com.jolyn.meetingroomreservationsystem.exception.*;
import com.jolyn.meetingroomreservationsystem.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController extends ExceptionHandling {
    @Autowired
    private ReservationService reservationService;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Reservation> createReservation (@RequestBody CreateReservationDto dto) throws OverlappingReservationException {
        log.info("Create New Reservation");
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ReservationDto>> findAllReservations() {
        log.info("Get All Reservations");
        return ResponseEntity.ok(reservationService.allReservation());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Reservation> updateReservation(@RequestBody ReservationDto reservation) throws OverlappingReservationException {
        log.info("Update Reservation ID: " + reservation.getId());
        return ResponseEntity.ok(reservationService.updateReservation(reservation));
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        log.info("Delete Reservation ID: " + id);
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Reservation> getReservationById(@PathVariable String id) {
        log.info("Find Reservation ID: " + id);
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @GetMapping("/find-by-email")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<Reservation>> getReservationByEmail(@RequestParam String email) {
        log.info("Find Reservation By User Email: " + email);
        return ResponseEntity.ok(reservationService.findByUser(email));
    }

    @GetMapping("/today-reservations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ReservationDto>> findAllTodayReservations() throws ParseException {
        log.info("Get All Reservations of {}", new Date());
        return ResponseEntity.ok(reservationService.todayReservation());
    }
}
