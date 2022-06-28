package com.crawler.sport.api.dto;

import com.crawler.sport.domain.MatchStatus;

public enum MatchStatusDto {
    FINISHED,
    TO_BE_PLAYED;

    public static MatchStatusDto from(MatchStatus matchStatus) {
        return MatchStatusDto.valueOf(matchStatus.toString());
    }
}
