package com.jolyn.meetingroomreservationsystem.service;

import com.jolyn.meetingroomreservationsystem.domain.Reservation;
import com.jolyn.meetingroomreservationsystem.domain.dto.CreateReservationDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.ReservationDto;
import com.jolyn.meetingroomreservationsystem.exception.OverlappingReservationException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface ReservationService {
    Reservation findById(String id);
    List<Reservation> findByUser(String email);
    List<ReservationDto> allReservation();
    Reservation createReservation(CreateReservationDto dto) throws OverlappingReservationException;
    Reservation updateReservation(ReservationDto reservation) throws OverlappingReservationException;
    void deleteReservation(String id);
    List<ReservationDto> todayReservation() throws ParseException;
}
