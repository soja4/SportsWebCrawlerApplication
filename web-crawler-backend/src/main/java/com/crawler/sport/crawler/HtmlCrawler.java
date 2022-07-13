package com.crawler.sport.crawler;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.MatchStatus;
import com.crawler.sport.domain.Team;
import com.crawler.sport.service.MatchResultService;
import com.crawler.sport.service.TeamService;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
@Component
public class HtmlCrawler extends WebCrawler {
    private static final Integer MATCH_DATE_BEGIN = 12;
    private static final Integer MATCH_DATE_END = 22;
    private static final Pattern EXCLUSIONS =
            Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
    private final MatchResultService matchService;

    private final TeamService teamService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

    public HtmlCrawler(MatchResultService matchResultService, TeamService teamService) {
        this.matchService = matchResultService;
        this.teamService = teamService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String urlString = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(urlString).matches()
                && urlString.startsWith("https://www.xscores.com/soccer");
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData && url.contains("/soccer/match")) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            String html = htmlParseData.getHtml();

            Document doc = Jsoup.parseBodyFragment(html);
            String matchScore = doc.getElementsByClass("match_details_score").text();
            String matchStatus = doc.getElementsByClass("match_details_status").text();

            String homeTeamName = doc.getElementsByClass("hTeam").text();
            String awayTeamName = doc.getElementsByClass("aTeam").text();

            String matchDate = doc.getElementsByClass("match_details_date").text();
            matchDate = matchDate.substring(MATCH_DATE_BEGIN, MATCH_DATE_END);
            LocalDate localDate = LocalDate.parse(matchDate, formatter);

            String[] score = new String[0];

            if (!matchScore.isEmpty()) {
                score = matchScore.replaceAll(" ", "").split("-");
            }

            log.info(matchScore);
            log.info(localDate.toString());
            log.info(homeTeamName);
            log.info(awayTeamName);
            log.info("----------");

            Team homeTeam = Team.builder().teamName(homeTeamName).build();
            Team awayTeam = Team.builder().teamName(awayTeamName).build();

            MatchResult matchResult =
                    MatchResult.builder()
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .homeTeamGoals(score.length != 0 ? Integer.valueOf(score[0]) : null)
                            .awayTeamGoals(score.length != 0 ? Integer.valueOf(score[1]) : null)
                            .matchDate(localDate)
                            .status(
                                    matchScore.isEmpty()
                                            ? MatchStatus.TO_BE_PLAYED
                                            : MatchStatus.FINISHED)
                            .build();

            teamService.setTeams(matchResult);

            matchService.save(matchResult);
        }
    }
}
