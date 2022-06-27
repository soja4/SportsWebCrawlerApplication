package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchResult, Integer> {

    List<MatchResult> findByHomeTeamIdOrAwayTeamId(Integer homeTeamId, Integer awayTeamId);
}
