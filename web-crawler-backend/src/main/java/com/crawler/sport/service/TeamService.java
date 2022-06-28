package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    @Autowired private TeamRepository teamRepository;

    public void setTeams(MatchResult matchResult) {
        Team homeTeam = getTeamByName(matchResult.getHomeTeam().getTeamName());
        if (homeTeam != null) {
            matchResult.setHomeTeam(homeTeam);
        }
        Team awayTeam = getTeamByName(matchResult.getAwayTeam().getTeamName());
        if (awayTeam != null) {
            matchResult.setAwayTeam(awayTeam);
        }
    }

    private Team getTeamByName(String teamName) {
        return teamRepository.findByTeamName(teamName);
    }
}
