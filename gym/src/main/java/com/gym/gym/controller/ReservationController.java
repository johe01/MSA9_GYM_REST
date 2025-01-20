package com.gym.gym.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gym.gym.domain.CustomUser;
import com.gym.gym.domain.Option;
import com.gym.gym.domain.Page;
import com.gym.gym.domain.Reservation;
import com.gym.gym.domain.TrainerProfile;
import com.gym.gym.domain.Users;
import com.gym.gym.service.ReservationService;
import com.gym.gym.service.TrainerProfileService;
import com.gym.gym.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin("*")
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TrainerProfileService trainerProfileService;

    @Autowired
    private UserService userService;

    // 관리자 예약 목록
    @GetMapping("/admin/reservation/list")
    public ResponseEntity<?> getAllReservation(@RequestParam(name = "keyword", defaultValue = "") String keyword,
            Option option, Page page) {
        try {
            List<Reservation> reservationList = reservationService.list(keyword, option, page);
            return new ResponseEntity<>(reservationList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("예약 목록 전체 조회 오류");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 관리자 캘린더 예약 목록
    @GetMapping("/admin/reservation/calendar")
    public ResponseEntity<?> getCalendarReservation(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "code", required = false) int code) {
        try {

            List<Users> trainerUsers = reservationService.trainerUsers();

            List<Reservation> sortByTrainer = reservationService.sortByTrainer(keyword, code);
            List<Map<String, Object>> reservationResponse = new ArrayList<>();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            for (Reservation rv : sortByTrainer) {
                Map<String, Object> response = new HashMap<>();
                String formattedTime = timeFormat.format(rv.getRvDate());

                response.put("start", rv.getRvDate());
                response.put("end", "");
                response.put("description", "");
                response.put("textColor", "white");
                response.put("user_no", rv.getUserNo());

                if (rv.getEnabled() == 2) {
                    response.put("title", formattedTime + " " + rv.getUserName() + "님 완료");
                    response.put("color", "#2a9c1b");
                    response.put("type", "completed");
                } else {
                    response.put("title", formattedTime + " " + rv.getUserName() + "님 예약");
                    response.put("color", "cornflowerblue");
                    response.put("type", "reservation");
                }

                reservationResponse.add(response);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("reservationList", reservationResponse);
            result.put("trainerUserList", trainerUsers);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("캘린더 조회 오류");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 내 예약 목록
    @GetMapping("/user/myPage/ptList/{no}")
    public ResponseEntity<?> getMyReservation(@PathVariable("no") Long no,
            @AuthenticationPrincipal CustomUser userDetails) {
        try {
            if (no != userDetails.getNo()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            List<Reservation> reservationCount = reservationService.userByList(no);
            long disabledCount = reservationService.disabledCount(userDetails.getNo());

            if (!reservationCount.isEmpty()) {
                Reservation lastReservation = reservationCount.get(reservationCount.size() - 1);
                int ptCount = lastReservation.getPtCount();
                ptCount -= disabledCount;

                ptCount = Math.max(ptCount, 0);
            }

            List<Reservation> reservationList = reservationService.userByList(no);
            return new ResponseEntity<>(reservationList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("회원 예약 조회 오류");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 예약 등록 페이지
    @GetMapping("/user/reservation/reservationInsert/{no}")
    public ResponseEntity<?> getMyTrainer(
            @PathVariable("no") int no,
            @AuthenticationPrincipal CustomUser userDetails
            ) {

        try {
            TrainerProfile trainerProfile = trainerProfileService.selectTrainer(no);

            int code = 1;

            log.info("담당 트레이너 번호 : " + no);
            log.info("trainerProfile : " + trainerProfile);

            List<Reservation> reservationByTrainer = reservationService.sortByTrainer(String.valueOf(no), code);

            log.info(("트레이너 예약 데이터 : " + reservationByTrainer));

            List<Map<String, Object>> reservationResponse = new ArrayList<>();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            for (Reservation rv : reservationByTrainer) {
                Map<String, Object> response = new HashMap<>();
                String formattedTime = timeFormat.format(rv.getRvDate());

                response.put("start", rv.getRvDate());
                response.put("end", "");
                response.put("description", "");
                response.put("textColor", "white");
                response.put("user_no", rv.getUserNo());

                if (rv.getEnabled() == 2) {
                    response.put("title", formattedTime + " " + rv.getUserName() + "님 완료");
                    response.put("color", "#2a9c1b");
                    response.put("type", "completed");
                } else {
                    response.put("title", formattedTime + " " + rv.getUserName() + "님 예약");
                    response.put("color", "cornflowerblue");
                    response.put("type", "reservation");
                }

                reservationResponse.add(response);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("trainerProfile", trainerProfile);
            result.put("reservationByTrainer", reservationByTrainer);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 예약 등록
    @PostMapping("/user/reservation/reservationInsert")
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation,
            @AuthenticationPrincipal CustomUser userDetails) {
        try {
            Long no = userDetails.getNo();
            reservation.setUserNo(no);

            int result = reservationService.insert(reservation);

            if (result > 0) {
                return new ResponseEntity<>("예약 성공", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("예약 실패", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 예약 취소
    @PutMapping("/user/myPage/ptList")
    public ResponseEntity<?> updateReservation(@RequestParam("no") int no, RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.findByNo(no);

            reservation.setCanceledAt(new Date());
            reservation.setEnabled(0);
            int result = reservationService.cancel(reservation);

            if (result > 0) {
                redirectAttributes.addFlashAttribute("message", "예약이 취소되었습니다.");
                return new ResponseEntity<>("예약 취소 성공", HttpStatus.OK);
            } else {
                redirectAttributes.addFlashAttribute("message", "예약 취소에 실패했습니다.");
                return new ResponseEntity<>("예약 취소 실패", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 관리자 예약 취소/완료
    @PutMapping("/admin/reservation/list")
    public ResponseEntity<?> updateReservationByAdmin(
            @RequestParam("reservationNo") int reservationNo,
            @RequestParam("action") String action
            ) {
        try {
            Reservation reservation = reservationService.findByNo(reservationNo);
            int result = 0;

            if ("complete".equals(action)) {
                reservation.setCanceledAt(new Date());
                reservation.setEnabled(2);
                result = reservationService.complete(reservation);

                List<Reservation> reservationCount = reservationService.userByList(reservation.getUserNo());
                long disabledCount = reservationService.disabledCount(reservation.getUserNo());
                if (!reservationCount.isEmpty()) {
                    Reservation lastReservation = reservationCount.get(reservationCount.size() - 1);
                    int ptCount = lastReservation.getPtCount();
                    ptCount -= disabledCount;
                    if (ptCount < 0) {
                        Users user = userService.select(reservation.getUserNo());
                        userService.updateTrainerNo(user.getNo());
                    }
                }

            } else if ("cancel".equals(action)) {
                reservation.setCanceledAt(new Date());
                reservation.setEnabled(0);
                result = reservationService.cancel(reservation);
            }

            if (result > 0) {
                return new ResponseEntity<>("예약 처리 성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("예약 처리 실패", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
