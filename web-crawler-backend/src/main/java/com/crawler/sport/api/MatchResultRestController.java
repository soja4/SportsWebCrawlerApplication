package com.crawler.sport.api;

import com.crawler.sport.api.dto.MatchResultDto;
import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.service.MatchResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("match-result")
@RequiredArgsConstructor
public class MatchResultRestController {

    private final MatchResultService matchService;

    @GetMapping(path = "all")
    public List<MatchResultDto> getMatches() {
        log.info("fetching match results");
        List<MatchResult> matchResults = matchService.getMatchResults();

        log.info("found " + matchResults.size() + " matchResults");

        return matchResults.stream().map(MatchResultDto::from).collect(Collectors.toList());
    }

    @GetMapping(path = "/to-be-played")
    public List<MatchResultDto> getMatchesToBePlayed() {
        log.info("fetching matches to be played");
        List<MatchResult> matchResults = matchService.getMatchesToBePlayedAndOdds();

        log.info("found " + matchResults.size() + " matches to be played");

        return matchResults.stream().map(MatchResultDto::from).collect(Collectors.toList());
    }

    @GetMapping(path = "{teamId}")
    public List<MatchResultDto> getMatchesForTeam(@PathVariable Integer teamId) {
        log.info("fetching match results for specific team with id: {}", teamId);
        List<MatchResult> matchResults = matchService.getMatchResultsForTeam(teamId);

        log.info("found " + matchResults.size() + " matchResults");

        return matchResults.stream().map(MatchResultDto::from).collect(Collectors.toList());
    }
}
