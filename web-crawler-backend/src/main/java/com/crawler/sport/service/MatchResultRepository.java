package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Integer> {

    @Query(
            nativeQuery = true,
            value =
                    "select * "
                            + "from match_result "
                            + "where (home_team_id = ?1 or away_team_id = ?1) "
                            + "and status = 'FINISHED' order by match_date asc "
                            + "limit 20")
    List<MatchResult> findFinishedByHomeTeamIdOrAwayTeamId(Integer teamId, MatchStatus matchStatus);

    List<MatchResult> findByStatus(MatchStatus matchStatus, Pageable pageable);

    List<MatchResult> findByHomeTeamIdAndStatus(
            Integer teamId, MatchStatus matchStatus, Pageable pageable);

    List<MatchResult> findByAwayTeamIdAndStatus(
            Integer teamId, MatchStatus matchStatus, Pageable pageable);

    List<MatchResult> findByHomeTeamIdAndAwayTeamIdAndHomeTeamGoalsAndAwayTeamGoalsAndMatchDate(
            Integer homeTeamId,
            Integer awayTeamId,
            Integer homeTeamGoals,
            Integer awayTeamGoals,
            LocalDate matchDate);
}
