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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class HtmlCrawler extends WebCrawler {
    private static final Pattern EXCLUSIONS =
            Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
    private final MatchResultService matchResultService;

    private final TeamService teamService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

    public HtmlCrawler(MatchResultService matchResultService, TeamService teamService) {
        this.matchResultService = matchResultService;
        this.teamService = teamService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String urlString = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(urlString).matches()
                && urlString.startsWith("https://www.bbc.com/sport/football");
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData && url.matches(".+\\/football\\/\\d+")) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            String html = htmlParseData.getHtml();

            Document doc = Jsoup.parseBodyFragment(html);

            Elements elements =
                    doc.getElementsByClass("sp-c-fixture__team-name sp-c-fixture__team-name--home");
            if (elements.isEmpty()) {
                return;
            }
            String homeTeamName =
                    elements.get(0)
                            .getElementsByClass(
                                    "gs-u-display-block gs-u-display-none@m sp-c-fixture__team-name-trunc")
                            .text();
            String awayTeamName =
                    doc.getElementsByClass("sp-c-fixture__team-name sp-c-fixture__team-name--away")
                            .get(0)
                            .getElementsByClass(
                                    "gs-u-display-block gs-u-display-none@m sp-c-fixture__team-name-trunc")
                            .text();

            String matchDate =
                    doc.getElementsByClass("sp-c-fixture__date gel-minion").attr("datetime");
            LocalDate localDate = LocalDate.parse(matchDate);

            String homeTeamGoals =
                    doc.getElementsByClass(
                                    "sp-c-fixture sp-c-fixture--live-session-header gel-wrap")
                            .get(0)
                            .getElementsByClass(
                                    "sp-c-fixture__number sp-c-fixture__number--home sp-c-fixture__number--ft")
                            .text();
            String awayTeamGoals =
                    doc.getElementsByClass(
                                    "sp-c-fixture sp-c-fixture--live-session-header gel-wrap")
                            .get(0)
                            .getElementsByClass(
                                    "sp-c-fixture__number sp-c-fixture__number--away sp-c-fixture__number--ft")
                            .text();

            Team homeTeam = Team.builder().teamName(homeTeamName).build();
            Team awayTeam = Team.builder().teamName(awayTeamName).build();

            MatchResult matchResult =
                    MatchResult.builder()
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .homeTeamGoals(Integer.valueOf(homeTeamGoals))
                            .awayTeamGoals(Integer.valueOf(awayTeamGoals))
                            .matchDate(localDate)
                            .status(MatchStatus.FINISHED)
                            .build();

            teamService.setTeams(matchResult);

            List<MatchResult> matches = matchResultService.findSameMatchResult(matchResult);

            if (matches.isEmpty()) {
                matchResultService.save(matchResult);
            } else {
                log.info("There is already a match in database! ------------ ");
            }
        }

        if (page.getParseData() instanceof HtmlParseData
                && url.matches(".+\\/scores-fixtures\\/2022-(0[7-9]|1[0-2])|2023-\\d+]")) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            String html = htmlParseData.getHtml();

            Document doc = Jsoup.parseBodyFragment(html);

            String dateFromURL = url.substring(url.length() - 7);

            Elements elementsByDate = doc.getElementsByClass("qa-match-block");

            for (Element elementByDate : elementsByDate) {
                Elements elements = elementByDate.getElementsByClass("sp-c-fixture");

                String dateFromHTML =
                        elementByDate
                                .getElementsByClass("gel-minion sp-c-match-list-heading")
                                .text();

                String[] stringParts = dateFromHTML.split(" ");
                String dateNumber = stringParts[1];
                dateNumber = dateNumber.substring(0, dateNumber.length() - 2);
                if (dateNumber.length() == 1) {
                    dateNumber = "0" + dateNumber;
                }

                String localeDate = dateFromURL.concat("-").concat(dateNumber);

                LocalDate localDate = LocalDate.parse(localeDate);

                for (Element element : elements) {
                    String homeTeamName =
                            element.getElementsByClass(
                                            "sp-c-fixture__team sp-c-fixture__team--time sp-c-fixture__team--time-home")
                                    .get(0)
                                    .getElementsByClass(
                                            "gs-u-display-none gs-u-display-block@m qa-full-team-name sp-c-fixture__team-name-trunc")
                                    .text();
                    String awayTeamName =
                            element.getElementsByClass(
                                            "sp-c-fixture__team sp-c-fixture__team--time sp-c-fixture__team--time-away")
                                    .get(0)
                                    .getElementsByClass(
                                            "gs-u-display-none gs-u-display-block@m qa-full-team-name sp-c-fixture__team-name-trunc")
                                    .text();

                    Team homeTeam = Team.builder().teamName(homeTeamName).build();
                    Team awayTeam = Team.builder().teamName(awayTeamName).build();

                    MatchResult matchResult =
                            MatchResult.builder()
                                    .homeTeam(homeTeam)
                                    .awayTeam(awayTeam)
                                    .matchDate(localDate)
                                    .status(MatchStatus.TO_BE_PLAYED)
                                    .build();

                    teamService.setTeams(matchResult);

                    List<MatchResult> matches = matchResultService.findSameMatchResult(matchResult);

                    if (matches.isEmpty()) {
                        matchResultService.save(matchResult);
                    } else {
                        log.info(
                                "There is already a match in database TO BE PLAYED! ------------ ");
                    }
                }
            }
        }
    }
}
