package com.crawler.sport.service;

import com.crawler.sport.classifier.ModelClassifier;
import com.crawler.sport.classifier.ModelGenerator;
import com.crawler.sport.domain.MatchOutcome;
import com.crawler.sport.domain.MatchResult;
import com.crawler.sport.domain.MatchStatus;
import com.crawler.sport.domain.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.Debug;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchResultService {

    private static final String SCORED_GOALS = "SCORED_GOALS";
    private static final String CONCEDED_GOALS = "CONCEDED_GOALS";
    private static final String TEAM_POINTS = "TEAM_POINTS";
    private static final String MATCHES_SIZE = "MATCHES_SIZE";

    private static final Integer NUMBER_OF_MATCHES = 10;
    private static final Integer WIN_POINTS = 3;
    private static final Integer LOSE_POINTS = 0;
    private static final Integer DRAW_POINTS = 1;
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "matchDate"));
    Pageable pageableForFinishedMatches =
            PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "matchDate"));
    @Autowired private MatchResultRepository matchRepository;

    public static final String MODEL_PATH = "/Users/soja/model.bin";

    ModelGenerator mg = new ModelGenerator();

    Filter filter = new Normalize();

    public void save(MatchResult matchResult) {
        matchRepository.save(matchResult);
    }

    public List<MatchResult> getMatchResults() {
        return matchRepository.findAll();
    }

    public List<MatchResult> getFinishedMatchResultsForTeam(Integer teamId) {
        return matchRepository.findFinishedByHomeTeamIdOrAwayTeamId(teamId, MatchStatus.FINISHED);
    }

    public List<MatchResult> getMatchResultsAsHomeTeam(Integer teamId) {
        return matchRepository.findByHomeTeamIdAndStatus(teamId, MatchStatus.FINISHED, pageable);
    }

    public List<MatchResult> getMatchesByStatus(MatchStatus matchStatus) {
        return matchRepository.findByStatus(matchStatus, pageableForFinishedMatches);
    }

    public List<MatchResult> getMatchResultsAsAwayTeam(Integer teamId) {
        return matchRepository.findByAwayTeamIdAndStatus(teamId, MatchStatus.FINISHED, pageable);
    }

    public List<MatchResult> getMatchesToBePlayed() {
        return matchRepository.findByStatus(MatchStatus.TO_BE_PLAYED, pageableForFinishedMatches);
    }

    public List<MatchResult> getMatchesToBePlayedAndPredictions() throws Exception {
        List<MatchResult> matches = getMatchesToBePlayed();

        Instances dataset = loadDataset();

        // divide dataset to train dataset 80% and test dataset 20%
        int trainSize = (int) Math.round(dataset.numInstances() * 0.8);
        int testSize = dataset.numInstances() - trainSize;

        dataset.randomize(new Debug.Random(1));

        // Normalize dataset
        filter.setInputFormat(dataset);
        Instances datasetnor = Filter.useFilter(dataset, filter);

        Instances traindataset = new Instances(datasetnor, 0, trainSize);
        Instances testdataset = new Instances(datasetnor, trainSize, testSize);

        // build classifier with train dataset
        MultilayerPerceptron ann = (MultilayerPerceptron) mg.buildClassifier(traindataset);

        // Evaluate classifier with test dataset
        String evalsummary = mg.evaluateModel(ann, traindataset, testdataset);
        System.out.println("Evaluation: " + evalsummary);

        // Save model
        mg.saveModel(ann, MODEL_PATH);

        // classify a single instance
        ModelClassifier cls = new ModelClassifier();

        for (MatchResult matchResult : matches) {
            Team homeTeam = matchResult.getHomeTeam();
            Team awayTeam = matchResult.getAwayTeam();

            Map<String, Integer> homeTeamFeatures = getScoredAndConcededGoals(homeTeam);
            Map<String, Integer> awayTeamFeatures = getScoredAndConcededGoals(awayTeam);

            List<MatchResult> homeTeamAtHome = getMatchResultsAsHomeTeam(homeTeam.getId());
            List<MatchResult> awayTeamAtHome = getMatchResultsAsAwayTeam(awayTeam.getId());

            if (!homeTeamFeatures.get(MATCHES_SIZE).equals(NUMBER_OF_MATCHES)
                    || !awayTeamFeatures.get(MATCHES_SIZE).equals(NUMBER_OF_MATCHES)
                    || homeTeamAtHome.size() != NUMBER_OF_MATCHES
                    || awayTeamAtHome.size() != NUMBER_OF_MATCHES) {
                continue;
            }
            Integer homeTeamGoalsScored = homeTeamFeatures.get(SCORED_GOALS);
            Integer homeTeamGoalsConceded = homeTeamFeatures.get(CONCEDED_GOALS);
            Double homeTeamAvgPoints =
                    (double)
                            (homeTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer awayTeamGoalsScored = awayTeamFeatures.get(SCORED_GOALS);
            Integer awayTeamGoalsConceded = awayTeamFeatures.get(CONCEDED_GOALS);
            Double awayTeamAvgPoints =
                    (double)
                            (awayTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer homeTeamWinsAtHome = 0;
            Integer homeTeamGoalsAtHome = 0;

            for (MatchResult homeMatchResult : homeTeamAtHome) {
                homeTeamGoalsAtHome += homeMatchResult.getHomeTeamGoals();
                if (homeMatchResult.getHomeTeamGoals() > homeMatchResult.getAwayTeamGoals()) {
                    homeTeamWinsAtHome++;
                }
            }

            Integer awayTeamWinsAtAway = 0;
            Integer awayTeamGoalsAtAway = 0;

            for (MatchResult awayMatchResult : awayTeamAtHome) {
                awayTeamGoalsAtAway += awayMatchResult.getAwayTeamGoals();
                if (awayMatchResult.getAwayTeamGoals() > awayMatchResult.getHomeTeamGoals()) {
                    awayTeamWinsAtAway++;
                }
            }

            String classname = null;
            try {
                classname =
                        cls.classify(
                                Filter.useFilter(
                                        cls.createInstance(
                                                homeTeamGoalsScored,
                                                awayTeamGoalsScored,
                                                homeTeamGoalsConceded,
                                                awayTeamGoalsConceded,
                                                homeTeamAvgPoints,
                                                awayTeamAvgPoints,
                                                homeTeamWinsAtHome,
                                                awayTeamWinsAtAway,
                                                homeTeamGoalsAtHome,
                                                awayTeamGoalsAtAway),
                                        filter),
                                MODEL_PATH);

                log.info("The class name is  " + classname);
            } catch (Exception e) {
                log.error("Can not classify instance with id: {}", matchResult.getId());
                }

        }

        return null;
    }

    public Instances getFinishedMatchesAndMakeInstances() {

        List<MatchResult> matches = getMatchesByStatus(MatchStatus.FINISHED);

        ArrayList<String> classVal = new ArrayList<>();
        classVal.add(MatchOutcome.HOME_WIN.toString());
        classVal.add(MatchOutcome.DRAW.toString());
        classVal.add(MatchOutcome.AWAY_WIN.toString());

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("homeTeamGoalsScored"));
        attributes.add(new Attribute("awayTeamGoalsScored"));
        attributes.add(new Attribute("homeTeamGoalsConceded"));
        attributes.add(new Attribute("awayTeamGoalsConceded"));
        attributes.add(new Attribute("homeTeamAvgPoints"));
        attributes.add(new Attribute("awayTeamAvgPoints"));
        attributes.add(new Attribute("homeTeamWonMatchesAtHome"));
        attributes.add(new Attribute("awayTeamWonMatchesAtAway"));
        attributes.add(new Attribute("homeTeamGoalsScoredAtHome"));
        attributes.add(new Attribute("awayTeamGoalsScoredAtAway"));
        attributes.add(new Attribute("matchOutcome", classVal));
        Instances data = new Instances("data", attributes, 0);

        for (MatchResult matchResult : matches) {

            Team homeTeam = matchResult.getHomeTeam();
            Team awayTeam = matchResult.getAwayTeam();

            Map<String, Integer> homeTeamFeatures = getScoredAndConcededGoals(homeTeam);
            Map<String, Integer> awayTeamFeatures = getScoredAndConcededGoals(awayTeam);

            List<MatchResult> homeTeamAtHome = getMatchResultsAsHomeTeam(homeTeam.getId());
            List<MatchResult> awayTeamAtHome = getMatchResultsAsAwayTeam(awayTeam.getId());

            if (homeTeamFeatures.get(MATCHES_SIZE) != NUMBER_OF_MATCHES
                    || awayTeamFeatures.get(MATCHES_SIZE) != NUMBER_OF_MATCHES
                    || homeTeamAtHome.size() != NUMBER_OF_MATCHES
                    || awayTeamAtHome.size() != NUMBER_OF_MATCHES) {
                continue;
            }

            Integer homeTeamGoalsScored = homeTeamFeatures.get(SCORED_GOALS);
            Integer homeTeamGoalsConceded = homeTeamFeatures.get(CONCEDED_GOALS);
            Double homeTeamAvgPoints =
                    (double)
                            (homeTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer awayTeamGoalsScored = awayTeamFeatures.get(SCORED_GOALS);
            Integer awayTeamGoalsConceded = awayTeamFeatures.get(CONCEDED_GOALS);
            Double awayTeamAvgPoints =
                    (double)
                            (awayTeamFeatures.get(TEAM_POINTS)
                                    / homeTeamFeatures.get(MATCHES_SIZE));

            Integer homeTeamWinsAtHome = 0;
            Integer homeTeamGoalsAtHome = 0;

            for (MatchResult homeMatchResult : homeTeamAtHome) {
                homeTeamGoalsAtHome += homeMatchResult.getHomeTeamGoals();
                if (homeMatchResult.getHomeTeamGoals() > homeMatchResult.getAwayTeamGoals()) {
                    homeTeamWinsAtHome++;
                }
            }

            Integer awayTeamWinsAtAway = 0;
            Integer awayTeamGoalsAtAway = 0;

            for (MatchResult awayMatchResult : awayTeamAtHome) {
                awayTeamGoalsAtAway += awayMatchResult.getAwayTeamGoals();
                if (awayMatchResult.getAwayTeamGoals() > awayMatchResult.getHomeTeamGoals()) {
                    awayTeamWinsAtAway++;
                }
            }

            double[] values =
                    new double[] {
                        homeTeamGoalsScored,
                        awayTeamGoalsScored,
                        homeTeamGoalsConceded,
                        awayTeamGoalsConceded,
                        homeTeamAvgPoints,
                        awayTeamAvgPoints,
                        homeTeamWinsAtHome,
                        awayTeamWinsAtAway,
                        homeTeamGoalsAtHome,
                        awayTeamGoalsAtAway,
                        getMatchOutcome(matchResult)
                    };

            data.add(new DenseInstance(1.0, values));
        }

        return data;
    }

    private double getMatchOutcome(MatchResult matchResult) {
        if (matchResult.getHomeTeamGoals().compareTo(matchResult.getAwayTeamGoals()) > 0) {
            return MatchOutcome.HOME_WIN.getOutcome();
        } else if (matchResult.getHomeTeamGoals().compareTo(matchResult.getAwayTeamGoals()) < 0) {
            return MatchOutcome.AWAY_WIN.getOutcome();
        } else {
            return MatchOutcome.DRAW.getOutcome();
        }
    }

    private Map<String, Integer> getScoredAndConcededGoals(Team team) {
        List<MatchResult> teamMatches = getFinishedMatchResultsForTeam(team.getId());
        Integer teamGoalsScored = 0;
        Integer teamGoalsConceded = 0;
        Integer teamPoints = 0;
        for (MatchResult teamMatch : teamMatches) {
            if (teamMatch.getHomeTeam().equals(team)) {
                teamGoalsScored += teamMatch.getHomeTeamGoals();
                teamGoalsConceded += teamMatch.getAwayTeamGoals();
                teamPoints +=
                        calculatePointsFromGame(
                                teamMatch.getHomeTeamGoals(), teamMatch.getAwayTeamGoals());
            } else {
                teamGoalsScored += teamMatch.getAwayTeamGoals();
                teamGoalsConceded += teamMatch.getHomeTeamGoals();
                teamPoints +=
                        calculatePointsFromGame(
                                teamMatch.getAwayTeamGoals(), teamMatch.getHomeTeamGoals());
            }
        }

        Map<String, Integer> teamFeatures = new HashMap<>();
        teamFeatures.put(SCORED_GOALS, teamGoalsScored);
        teamFeatures.put(CONCEDED_GOALS, teamGoalsConceded);
        teamFeatures.put(TEAM_POINTS, teamPoints);
        teamFeatures.put(MATCHES_SIZE, teamMatches.size());
        return teamFeatures;
    }

    private Integer calculatePointsFromGame(Integer goalsScored, Integer goalsConceded) {
        if (goalsScored > goalsConceded) {
            return WIN_POINTS;
        } else if (goalsConceded > goalsScored) {
            return LOSE_POINTS;
        } else {
            return DRAW_POINTS;
        }
    }

    private Instances loadDataset() {
        Instances dataset = getFinishedMatchesAndMakeInstances();
        try {

            if (dataset.classIndex() == -1) {
                dataset.setClassIndex(dataset.numAttributes() - 1);
            }
        } catch (Exception ex) {
            log.error("Error loading data");
        }

        return dataset;
    }

    public List<MatchResult> findSameMatchResult(MatchResult matchResult) {
        return matchRepository
                .findByHomeTeamIdAndAwayTeamIdAndHomeTeamGoalsAndAwayTeamGoalsAndMatchDate(
                        matchResult.getHomeTeam().getId(),
                        matchResult.getAwayTeam().getId(),
                        matchResult.getHomeTeamGoals(),
                        matchResult.getAwayTeamGoals(),
                        matchResult.getMatchDate());
    }
}
