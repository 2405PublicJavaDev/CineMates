package com.filmfellows.cinemates.domain.movie.model.service;

import com.filmfellows.cinemates.app.movie.dto.MovieDTO;
import com.filmfellows.cinemates.app.movie.dto.MovieListDTO;
import com.filmfellows.cinemates.app.movie.dto.MovieReservationRateDTO;
import com.filmfellows.cinemates.domain.movie.model.vo.Movie;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface MovieService {
    List<MovieListDTO> selectAllMovieList();
    List<MovieDTO> selectMovieDetail(Long movieNo);
    void updateMovie(MovieDTO movieDTO);

    void insertMovie(MovieDTO movie);

    List<Movie> selectSearchMovie(Map<String, String> paramMap);

    List<Movie> selectMovieList(RowBounds rowBounds, Integer currentPage);
    int totalMovieCount();

    List<MovieReservationRateDTO> getMovieReservationRates();

    List<MovieListDTO> getComingSoonMovies();

    List<MovieListDTO> getNowShowingMovies();

    List<MovieListDTO> getMoviesByStatusAndSort(String status, int page, int size, String sortBy);

    int getTrailrtConunt(Long movieNo);

    int getStillcutCount(Long movieNo);
    List<MovieDTO> selectMovieDetailAndPages(Long movieNo, int page, int size);
}
