package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Integer> {

    List<MatchResult> findByHomeTeamIdOrAwayTeamId(
            Integer homeTeamId, Integer awayTeamId, Pageable pageable);

    List<MatchResult> findByStatus(MatchStatus matchStatus);

    List<MatchResult> findByHomeTeamId(Integer teamId, Pageable pageable);

    List<MatchResult> findByAwayTeamId(Integer teamId, Pageable pageable);
}
