package com.crawler.sport.api.dto;

import com.crawler.sport.domain.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResultDto {
    private String homeTeam;
    private String awayTeam;
    private Integer homeTeamGoals;
    private Integer awayTeamGoals;

    public static MatchResultDto from(MatchResult matchResult) {
        return MatchResultDto.builder()
                .awayTeam(matchResult.getAwayTeam())
                .awayTeamGoals(matchResult.getAwayTeamGoals())
                .homeTeam(matchResult.getHomeTeam())
                .homeTeamGoals(matchResult.getHomeTeamGoals())
                .build();
    }
}
