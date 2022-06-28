CREATE TABLE `matchResult`
(
    `id`            integer NOT NULL,
    `homeTeamId`    integer not null
        constraint fk_home_team_id
            references team,
    `awayTeamId`    integer not null
        constraint fk_away_team_id
            references team,
    `homeTeamGoals` integer not null,
    `awayTeamGoals` integer not null,
    `matchDate`     date    not null,
    PRIMARY KEY (`id`)
);

CREATE TABLE `teamName`
(
    `id`       integer      NOT NULL,
    `teamName` varchar(255) not null
        PRIMARY KEY (`id`)
);