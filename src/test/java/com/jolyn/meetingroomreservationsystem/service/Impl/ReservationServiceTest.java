package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.domain.Reservation;
import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.CreateReservationDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.ReservationDto;
import com.jolyn.meetingroomreservationsystem.exception.OverlappingReservationException;
import com.jolyn.meetingroomreservationsystem.repository.ReservationRepository;
import com.jolyn.meetingroomreservationsystem.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @InjectMocks
    private ReservationServiceImpl reservationService;

    Reservation testReservation;
    private ReservationDto testReservationDto;
    private CreateReservationDto testCreateReservationDto;
    List<ReservationDto> dtos = new ArrayList<>();
    List<Reservation> reservations = new ArrayList<>();
    private UserInfo testUser;
    Date currentDate;
    Date laterDate;

    @BeforeEach
    void setUp() {
        testUser = new UserInfo();
        testUser.setEmail("test@example.com");

        testReservation = new Reservation();
        testReservation.setId("1");
        testReservation.setStartDatetime(new Date());
        testReservation.setEndDatetime(new Date(System.currentTimeMillis() + 1000));
        testReservation.setReason("test reservation");
        testReservation.setUserInfo(testUser);

        testReservationDto = new ReservationDto();
        testReservationDto.setStartDatetime(new Date());
        testReservationDto.setEndDatetime(new Date(System.currentTimeMillis() + 1000));
        testReservationDto.setReason("test reservation");
        testReservationDto.setUserEmail(testUser.getEmail());

        testCreateReservationDto = new CreateReservationDto();
        testCreateReservationDto.setUserEmail("test@example.com");
        testCreateReservationDto.setReason("new meeting");
        testCreateReservationDto.setStartDatetime(new Date(System.currentTimeMillis() + 7000));
        testCreateReservationDto.setEndDatetime(new Date(System.currentTimeMillis() + 9000));

        dtos.add(testReservationDto);
        reservations.add(testReservation);

        currentDate = new Date();
        laterDate = new Date(System.currentTimeMillis() + 1000);
    }

    @Test
    public void findByIdTest() {
        when(reservationRepository.findById("1")).thenReturn(Optional.ofNullable(testReservation));

        Reservation reservation = reservationService.findById("1");
        assertNotNull(reservation);
        assertEquals("test reservation", reservation.getReason());
    }

    @Test
    public void findByUserTest() {
        when(reservationRepository.findByUserInfoEmail(testUser.getEmail())).thenReturn(reservations);

        List<Reservation> reservations = reservationService.findByUser(testUser.getEmail());
        assertEquals(1, reservations.size());
        assertEquals("test reservation", reservations.get(0).getReason());
    }

    @Test
    public void allReservationTest() {
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ReservationDto> reservationDtos = reservationService.allReservation();
        assertEquals(1, reservationDtos.size());
        assertEquals("test reservation", reservationDtos.get(0).getReason());
    }

    @Test
    public void createReservationTest() throws OverlappingReservationException {
        Reservation reservation = reservationService.createReservation(testCreateReservationDto);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    public void deleteReservationTest() {
        reservationService.deleteReservation(testReservation.getId());
        verify(reservationRepository).deleteById(testReservation.getId());
    }

    @Test
    public void todayReservationTest() throws ParseException {
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        String TIME_ZONE = "Asia/Singapore";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(currentDate, LocalTime.MIN);
        String formattedStartDate = formatter.format(startOfDay.atZone(ZoneId.of(TIME_ZONE)).toLocalDateTime());
        Date startDatetime = sdf.parse(formattedStartDate);

        LocalDateTime endOfDay = LocalDateTime.of(currentDate, LocalTime.MAX);
        String formattedEndDate = formatter.format(endOfDay.atZone(ZoneId.of(TIME_ZONE)).toLocalDateTime());
        Date endDatetime = sdf.parse(formattedEndDate);

        when(reservationRepository.findByToday(startDatetime, endDatetime)).thenReturn(reservations);
        List<ReservationDto> reservationList = reservationService.todayReservation();
        assertNotNull(reservationList);
    }

}
