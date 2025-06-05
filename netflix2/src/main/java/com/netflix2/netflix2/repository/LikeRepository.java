package com.netflix2.netflix2.repository;

import com.netflix2.netflix2.entity.Like;
import com.netflix2.netflix2.dto.LikeDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    boolean existsByUserIdAndMovieId(String userId, String movieId);

    void deleteByUserIdAndMovieId(String userId, String movieId);

    @Query("SELECT new com.netflix2.netflix2.dto.LikeDTO(l.movieId, COUNT(l)) " +
           "FROM Like l GROUP BY l.movieId ORDER BY COUNT(l) DESC")
    List<LikeDTO> findTop10ByMovieIdGroupByCount();
}
