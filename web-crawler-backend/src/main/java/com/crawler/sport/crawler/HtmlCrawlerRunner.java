package com.crawler.sport.crawler;

import com.crawler.sport.service.MatchResultService;
import com.crawler.sport.service.TeamService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class HtmlCrawlerRunner implements CommandLineRunner {

    @Value("${url.root}")
    private String urlRoot;

    @Autowired private MatchResultService matchResultService;
    @Autowired private TeamService teamService;

    @Override
    public void run(String... args) throws Exception {
        File crawlStorage = new File("src/test/resources/crawler4j");
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());

        int numCrawlers = 12;

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        BlockingQueue<String> urlsQueue = new ArrayBlockingQueue<>(400);

        controller.addSeed(urlRoot);

        CrawlController.WebCrawlerFactory<HtmlCrawler> factory =
                () -> new HtmlCrawler(matchResultService, teamService);

        controller.start(factory, numCrawlers);
    }
}
