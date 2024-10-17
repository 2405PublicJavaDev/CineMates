package com.filmfellows.cinemates.app.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmfellows.cinemates.domain.reservation.model.Service.ReservationService;
import com.filmfellows.cinemates.domain.reservation.model.vo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReservationController {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static SecureRandom random = new SecureRandom();

    @Autowired
    ReservationService rService;
    @Autowired
    private ReservationDTO reservationDTO;


    @GetMapping("/Ticketing")
    public String showShowTimePage(Model model, HttpSession session, String title , Integer movieNo) {
        System.out.println("title : " + title);
        String memberId = (String) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/login";
        }

        List<SearchMovieDTO> movieList = rService.selectAllMovies();
        List<SearchLocationCodeDTO> lList = rService.selectAllLocationCode();
        List<String> rList = rService.selectCinemaName();
        List<String> processedList = new ArrayList<>();
            Map<String, String> ageRatings = new HashMap<>();

        for (SearchMovieDTO movie : movieList) {
            String ageRating = rService.getAgeRatingByTitle(movie.getTitle());
            ageRatings.put(movie.getTitle(), ageRating);
        }
        for (String cinemaGroup : rList) {
            String[] cinemas = cinemaGroup.split(",");
            for (String cinema : cinemas) {
                processedList.add(cinema.trim());
            }
        }

        if (title != null && movieNo != null) {
            model.addAttribute("selectedMovieTitle", title);
            model.addAttribute("selectedMovieNo", movieNo);
        }
        model.addAttribute("ageRatings", ageRatings);
        model.addAttribute("movieList", movieList);
        model.addAttribute("rList", processedList);
        model.addAttribute("lList", lList);
        return "pages/reservation/showtime";
    }

    @PostMapping("/Ticketing/PersonSeat")
    public String showPersonSeatPage(@ModelAttribute ReservationDTO rDTO, @RequestParam String reservationSeat, Model model, HttpSession session,
                                     @RequestParam String title) {
        String memberId = (String) session.getAttribute("memberId");
        String randomString = generateRandomString(10);
        rDTO.setReservationNo(randomString);
        ShowInfoDTO sDTO = rService.selectMoviePoster(title);
        System.out.println("영화 포스터: " + sDTO);

        // JSON 문자열을 Map으로 변환
        ObjectMapper mapper = new ObjectMapper();
        Map<Integer, List<Integer>> reservedSeats;
        try {
            reservedSeats = mapper.readValue(reservationSeat, new TypeReference<Map<Integer, List<Integer>>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            reservedSeats = new HashMap<>();
        }

        // 예약된 좌석 정보를 모델에 추가
        model.addAttribute("reservationSeat", reservedSeats);
        model.addAttribute("sDTO", sDTO);

        System.out.println("rDTO 보여줘라 " + rDTO);
        model.addAttribute("memberId", memberId);
        model.addAttribute("rDTO", rDTO);
        return "pages/reservation/personSeat";
    }

    private static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int character = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @GetMapping("/getCinemas")
    public ResponseEntity<List<String>> selectCinemas(
            @RequestParam String cinemaAddress
//            @RequestParam Integer cinemaLocationCode
    ) {
        List<String> addresses = Arrays.asList(cinemaAddress.split("/"));
        List<String> allCinemas = new ArrayList<>();
        for (String address : addresses) {
            List<String> cinemas = rService.selectCinemas(address);
            allCinemas.addAll(cinemas);
            System.out.println(cinemas);
        }
//        List<String>address = rService.selectCinemasByCode(cinemaLocationCode);
        return ResponseEntity.ok(allCinemas);
    }

    @GetMapping("/getMovies")
    public ResponseEntity<List<String>> selectMovies(@RequestParam String cinemaName) {
        System.out.println("영화 이름" + cinemaName);
        List<String> mList = rService.selectMovies(cinemaName);
        System.out.println("영화 목록: " + mList);
        return ResponseEntity.ok(mList);
    }

    @GetMapping("/getShowtimes")
    public ResponseEntity<Map<String, Object>> selectShowInfo(
            @RequestParam String cinemaName,
            @RequestParam String title,
            @RequestParam String reservationDate) {
        List<ShowInfoDTO> sList = rService.selectShowInfo(cinemaName, title);
        List<ReservationDTO> rList = rService.selectReservationSeat(reservationDate);

        Map<Integer, List<Integer>> reservedSeatsMap = new HashMap<>();

        for (ShowInfoDTO show : sList) {
            int totalSeats = Integer.parseInt(show.getScreenSeat());
            List<Integer> reservedSeats = rList.stream()
                    .filter(r -> r.getScreenName().equals(show.getScreenName()) &&
                            r.getShowtimeTime().equals(show.getShowtimeTime()))
                    .flatMap(r -> Arrays.stream(r.getReservationSeat().split(",")).map(Integer::parseInt))
                    .collect(Collectors.toList());

            int availableSeats = totalSeats - reservedSeats.size();
            show.setAvailableSeats(availableSeats);

            reservedSeatsMap.put((show.getShowtimeNo()), reservedSeats);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("showInfoList", sList);
        response.put("reservationSeat", reservedSeatsMap);

        return ResponseEntity.ok(response);
    }

}
