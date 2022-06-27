package com.crawler.sport.crawler;

import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.Team;
import com.crawler.sport.service.MatchService;
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
    private static Integer counter = 0;
    private static final Integer MATCH_DATE_BEGIN = 12;
    private static final Integer MATCH_DATE_END = 22;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

    private final MatchService matchService;

    private static final Pattern EXCLUSIONS =
            Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

    public HtmlCrawler(MatchService matchService) {
        this.matchService = matchService;
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
            if (!matchScore.isEmpty() && counter < 10) {
                counter++;
                String homeTeamName = doc.getElementsByClass("hTeam").text();
                String awayTeamName = doc.getElementsByClass("aTeam").text();

                String[] score = matchScore.replaceAll(" ", "").split("-");
                String matchDate = doc.getElementsByClass("match_details_date").text();
                matchDate = matchDate.substring(MATCH_DATE_BEGIN, MATCH_DATE_END);
                LocalDate localDate = LocalDate.parse(matchDate, formatter);

                log.info(matchScore);
                log.info(localDate.toString());
                log.info(homeTeamName);
                log.info(awayTeamName);
                log.info("----------");
                log.info(counter.toString());

                Team homeTeam = Team.builder().teamName(homeTeamName).build();
                Team awayTeam = Team.builder().teamName(awayTeamName).build();

                MatchResult matchResult =
                        MatchResult.builder()
                                .homeTeam(homeTeam)
                                .awayTeam(awayTeam)
                                .homeTeamGoals(Integer.valueOf(score[0]))
                                .awayTeamGoals(Integer.valueOf(score[1]))
                                .matchDate(localDate)
                                .build();

                matchService.save(matchResult);
            }
        }
    }
}
