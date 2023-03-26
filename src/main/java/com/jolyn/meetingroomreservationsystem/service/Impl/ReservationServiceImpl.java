package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.domain.Reservation;
import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.CreateReservationDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.ReservationDto;
import com.jolyn.meetingroomreservationsystem.exception.OverlappingReservationException;
import com.jolyn.meetingroomreservationsystem.repository.ReservationRepository;
import com.jolyn.meetingroomreservationsystem.repository.UserInfoRepository;
import com.jolyn.meetingroomreservationsystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.jolyn.meetingroomreservationsystem.exception.ExceptionHandling.OVERLAP_RESERVATION;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private String TIME_ZONE = "Asia/Singapore";

    @Override
    public Reservation findById(String id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Reservation> findByUser(String email) {
        return reservationRepository.findByUserInfoEmail(email);
    }

    @Override
    public List<ReservationDto> allReservation() {
        return generateDto(reservationRepository.findAll());
    }

    @Override
    public Reservation createReservation(CreateReservationDto dto) throws OverlappingReservationException {
        boolean isAvailable = isAvailable(null, dto.getStartDatetime(), dto.getEndDatetime());
        if (isAvailable) {
            Reservation reservation = new Reservation();
            reservation.setStartDatetime(dto.getStartDatetime());
            reservation.setEndDatetime(dto.getEndDatetime());
            reservation.setReason(dto.getReason());
            reservation.setUserInfo(userInfoRepository.findByEmail(dto.getUserEmail()));
            return reservationRepository.save(reservation);
        } else {
            throw new OverlappingReservationException(OVERLAP_RESERVATION);
        }
    }

    @Override
    public Reservation updateReservation(ReservationDto reservation) throws OverlappingReservationException {
        boolean isAvailable = isAvailable(reservation.getId(), reservation.getStartDatetime(), reservation.getEndDatetime());
        if (isAvailable) {
            UserInfo userInfo = userInfoRepository.findByEmail(reservation.getUserEmail());
            Reservation reservation1 = new Reservation(reservation.getId(), reservation.getStartDatetime(), reservation.getEndDatetime(), reservation.getReason(), userInfo);
            return reservationRepository.save(reservation1);
        } else {
            throw new OverlappingReservationException(OVERLAP_RESERVATION);
        }
    }

    @Override
    public void deleteReservation(String id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationDto> todayReservation() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(currentDate, LocalTime.MIN);
        String formattedStartDate = formatter.format(startOfDay.atZone(ZoneId.of(TIME_ZONE)).toLocalDateTime());
        Date startDatetime = sdf.parse(formattedStartDate);

        LocalDateTime endOfDay = LocalDateTime.of(currentDate, LocalTime.MAX);
        String formattedEndDate = formatter.format(endOfDay.atZone(ZoneId.of(TIME_ZONE)).toLocalDateTime());
        Date endDatetime = sdf.parse(formattedEndDate);

        List<Reservation> reservations = reservationRepository.findByToday(startDatetime, endDatetime);

        return generateDto(reservations);
    }

    private boolean isAvailable(String id, Date startDatetime, Date endDatetime) {
        List<Reservation> reservations = reservationRepository.clashedReservations(startDatetime, endDatetime);
        if(reservations.size() > 0) {
            if (id != null) {
                if (reservations.stream().filter(a -> id.equalsIgnoreCase(a.getId())).findFirst().isPresent()) {
                    reservations.remove(reservations.stream().filter(a -> id.equalsIgnoreCase(a.getId())).findFirst().get());
                    if (reservations.size() > 0) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private List<ReservationDto> generateDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(r -> new ReservationDto(r.getId(), r.getStartDatetime(), r.getEndDatetime(), r.getReason(), r.getUserInfo().getEmail()))
                .collect(Collectors.toList());
    }
}
