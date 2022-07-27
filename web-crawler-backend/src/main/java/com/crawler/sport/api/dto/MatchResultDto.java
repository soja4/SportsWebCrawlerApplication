package com.crawler.sport.api.dto;

import com.crawler.sport.domain.MatchOutcome;
import com.crawler.sport.domain.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResultDto {
    private String homeTeam;
    private Integer homeTeamId;
    private String awayTeam;
    private Integer awayTeamId;
    private Integer homeTeamGoals;
    private Integer awayTeamGoals;
    private LocalDate matchDate;
    private MatchStatusDto matchStatusDto;
    private String matchOutcome;

    public static MatchResultDto from(MatchResult matchResult) {
        return MatchResultDto.builder()
                .awayTeam(matchResult.getAwayTeam().getTeamName())
                .awayTeamId(matchResult.getAwayTeam().getId())
                .awayTeamGoals(matchResult.getAwayTeamGoals())
                .homeTeam(matchResult.getHomeTeam().getTeamName())
                .homeTeamId(matchResult.getHomeTeam().getId())
                .homeTeamGoals(matchResult.getHomeTeamGoals())
                .matchDate(matchResult.getMatchDate())
                .matchStatusDto(MatchStatusDto.from(matchResult.getStatus()))
                .matchOutcome(matchResult.getMatchOutcome())
                .build();
    }
}
