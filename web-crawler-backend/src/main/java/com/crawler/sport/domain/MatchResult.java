package com.crawler.sport.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "match_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "home_team_goals")
    private Integer homeTeamGoals;

    @Column(name = "away_team_goals")
    private Integer awayTeamGoals;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Transient
    private String matchOutcome;
}
