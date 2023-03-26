package com.jolyn.meetingroomreservationsystem.repository;

import com.jolyn.meetingroomreservationsystem.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUserInfoEmail(String email);

    @Query(value = "SELECT * FROM reservation r WHERE (r.start_datetime <= :startDateTime AND r.end_datetime > :startDateTime) "
            + "OR (r.start_datetime < :endDateTime AND r.end_datetime >= :endDateTime)", nativeQuery = true)
    List<Reservation> clashedReservations(@Param("startDateTime") Date startDateTime, @Param("endDateTime") Date endDateTime);

    @Query(value = "SELECT * FROM reservation r WHERE r.end_datetime  <= :endDateTime AND r.start_datetime >= :startDateTime", nativeQuery = true)
    List<Reservation> findByToday(@Param("startDateTime") Date startDatetime, @Param("endDateTime") Date endDatetime);
}
