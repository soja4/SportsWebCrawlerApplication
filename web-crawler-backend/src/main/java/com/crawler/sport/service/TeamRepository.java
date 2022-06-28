package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository  extends JpaRepository<Team, Integer> {

    Team findByTeamName(String teamName);
}
