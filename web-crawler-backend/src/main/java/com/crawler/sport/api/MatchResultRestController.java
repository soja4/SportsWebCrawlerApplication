package com.crawler.sport.api;

import com.crawler.sport.api.dto.MatchResultDto;
import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.service.MatchResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    private final MatchResultService matchResultService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "/to-be-played/{matchDate}")
    public List<MatchResultDto> getMatchesToBePlayedAndPredictedClass(@PathVariable String matchDate) throws Exception {
        log.info("fetching matches to be played");
        List<MatchResult> matchResults = matchResultService.getMatchesToBePlayedAndPredictions(matchDate);

        log.info("found " + matchResults.size() + " matches to be played");

        return matchResults.stream().map(MatchResultDto::from).collect(Collectors.toList());
    }
}
