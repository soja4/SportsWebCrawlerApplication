package com.crawler.sport.service;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.MatchStatus;
import com.crawler.sport.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchResultService {

    private static final String SCORED_GOALS = "SCORED_GOALS";
    private static final String CONCEDED_GOALS = "CONCEDED_GOALS";
    private static final String TEAM_POINTS = "TEAM_POINTS";
    private static final String MATCHES_SIZE = "MATCHES_SIZE";
    private static final Integer WIN_POINTS = 3;
    private static final Integer LOSE_POINTS = 0;
    private static final Integer DRAW_POINTS = 1;
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "updatedAt"));
    @Autowired private MatchResultRepository matchRepository;

    public void save(MatchResult matchResult) {
        matchRepository.save(matchResult);
    }

    public List<MatchResult> getMatchResults() {
        return matchRepository.findAll();
    }

    public List<MatchResult> getMatchResultsForTeam(Integer teamId) {
        return matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId, pageable);
    }

    public List<MatchResult> getMatchResultsAsHomeTeam(Integer teamId) {
        return matchRepository.findByHomeTeamId(teamId, pageable);
    }

    public List<MatchResult> getMatchResultsAsAwayTeam(Integer teamId) {
        return matchRepository.findByAwayTeamId(teamId, pageable);
    }

    public List<MatchResult> getMatchesToBePlayed() {
        return matchRepository.findByStatus(MatchStatus.TO_BE_PLAYED);
    }

    public List<MatchResult> getMatchesToBePlayedAndOdds() {
        List<MatchResult> matches = getMatchesToBePlayed();

        for (MatchResult matchResult : matches) {
            Team homeTeam = matchResult.getHomeTeam();
            Team awayTeam = matchResult.getAwayTeam();

            Map<String, Integer> homeTeamFeatures = getScoredAndConcededGoals(homeTeam);
            Map<String, Integer> awayTeamFeatures = getScoredAndConcededGoals(awayTeam);

            Integer homeTeamGoalsScored = homeTeamFeatures.get(SCORED_GOALS);
            Integer homeTeamGoalsConceded = homeTeamFeatures.get(CONCEDED_GOALS);
            Double homeTeamAvgPoints =
                    (double)
                            (homeTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer awayTeamGoalsScored = awayTeamFeatures.get(SCORED_GOALS);
            Integer awayTeamGoalsConceded = awayTeamFeatures.get(CONCEDED_GOALS);
            Double awayTeamAvgPoints =
                    (double)
                            (awayTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer homeTeamWinsAtHome = 0;
            Integer homeTeamGoalsAtHome = 0;
            List<MatchResult> homeTeamAtHome = getMatchResultsAsHomeTeam(homeTeam.getId());
            for (MatchResult homeMatchResult : homeTeamAtHome) {
                homeTeamGoalsAtHome += homeMatchResult.getHomeTeamGoals();
                if (homeMatchResult.getHomeTeamGoals() > homeMatchResult.getAwayTeamGoals()) {
                    homeTeamWinsAtHome++;
                }
            }

            Integer awayTeamWinsAtAway = 0;
            Integer awayTeamGoalsAtAway = 0;
            List<MatchResult> awayTeamAtHome = getMatchResultsAsAwayTeam(awayTeam.getId());
            for (MatchResult awayMatchResult : awayTeamAtHome) {
                awayTeamGoalsAtAway += awayMatchResult.getAwayTeamGoals();
                if (awayMatchResult.getAwayTeamGoals() > awayMatchResult.getHomeTeamGoals()) {
                    awayTeamWinsAtAway++;
                }
            }
        }

        return null;
    }

    private Map<String, Integer> getScoredAndConcededGoals(Team team) {
        List<MatchResult> teamMatches = getMatchResultsForTeam(team.getId());
        Integer teamGoalsScored = 0;
        Integer teamGoalsConceded = 0;
        Integer teamPoints = 0;
        for (MatchResult teamMatch : teamMatches) {
            if (teamMatch.getHomeTeam().equals(team)) {
                teamGoalsScored += teamMatch.getHomeTeamGoals();
                teamGoalsConceded += teamMatch.getAwayTeamGoals();
                teamPoints +=
                        calculatePointsFromGame(
                                teamMatch.getHomeTeamGoals(), teamMatch.getAwayTeamGoals());
            } else {
                teamGoalsScored += teamMatch.getAwayTeamGoals();
                teamGoalsConceded += teamMatch.getHomeTeamGoals();
                teamPoints +=
                        calculatePointsFromGame(
                                teamMatch.getAwayTeamGoals(), teamMatch.getHomeTeamGoals());
            }
        }

        Map<String, Integer> teamFeatures = new HashMap<>();
        teamFeatures.put(SCORED_GOALS, teamGoalsScored);
        teamFeatures.put(CONCEDED_GOALS, teamGoalsConceded);
        teamFeatures.put(TEAM_POINTS, teamPoints);
        teamFeatures.put(MATCHES_SIZE, teamMatches.size());
        return teamFeatures;
    }

    private Integer calculatePointsFromGame(Integer goalsScored, Integer goalsConceded) {
        if (goalsScored > goalsConceded) {
            return WIN_POINTS;
        } else if (goalsConceded > goalsScored) {
            return LOSE_POINTS;
        } else {
            return DRAW_POINTS;
        }
    }
}
