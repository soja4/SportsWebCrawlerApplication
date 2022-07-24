package com.crawler.sport.classifier;

import com.crawler.sport.domain.MatchOutcome;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.ArrayList;

@Slf4j
@Component
public class ModelClassifier {

    private Attribute homeTeamGoalsScored;
    private Attribute awayTeamGoalsScored;
    private Attribute homeTeamGoalsConceded;
    private Attribute awayTeamGoalsConceded;
    private Attribute homeTeamAvgPoints;
    private Attribute awayTeamAvgPoints;
    private Attribute homeTeamWonMatchesAtHome;
    private Attribute awayTeamWonMatchesAtAway;
    private Attribute homeTeamGoalsScoredAtHome;
    private Attribute awayTeamGoalsScoredAtAway;

    private ArrayList<Attribute> attributes;
    private ArrayList<String> classVal;
    private Instances dataRaw;

    public ModelClassifier() {
        homeTeamGoalsScored = new Attribute("homeTeamGoalsScored");
        awayTeamGoalsScored = new Attribute("awayTeamGoalsScored");
        homeTeamGoalsConceded = new Attribute("homeTeamGoalsConceded");
        awayTeamGoalsConceded = new Attribute("awayTeamGoalsConceded");
        homeTeamAvgPoints = new Attribute("homeTeamAvgPoints");
        awayTeamAvgPoints = new Attribute("awayTeamAvgPoints");
        homeTeamWonMatchesAtHome = new Attribute("homeTeamWonMatchesAtHome");
        awayTeamWonMatchesAtAway = new Attribute("awayTeamWonMatchesAtAway");
        homeTeamGoalsScoredAtHome = new Attribute("homeTeamGoalsScoredAtHome");
        awayTeamGoalsScoredAtAway = new Attribute("awayTeamGoalsScoredAtAway");
        attributes = new ArrayList<>();
        classVal = new ArrayList<>();
        classVal.add(MatchOutcome.HOME_WIN.toString());
        classVal.add(MatchOutcome.DRAW.toString());
        classVal.add(MatchOutcome.AWAY_WIN.toString());

        attributes.add(homeTeamGoalsScored);
        attributes.add(awayTeamGoalsScored);
        attributes.add(homeTeamGoalsConceded);
        attributes.add(awayTeamGoalsConceded);
        attributes.add(homeTeamAvgPoints);
        attributes.add(awayTeamAvgPoints);
        attributes.add(homeTeamWonMatchesAtHome);
        attributes.add(awayTeamWonMatchesAtAway);
        attributes.add(homeTeamGoalsScoredAtHome);
        attributes.add(awayTeamGoalsScoredAtAway);

        attributes.add(new Attribute("matchOutcome", classVal));
        dataRaw = new Instances("data", attributes, 0);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
    }

    public Instances createInstance(
            double homeTeamGoalsScored,
            double awayTeamGoalsScored,
            double homeTeamGoalsConceded,
            double awayTeamGoalsConceded,
            double homeTeamAvgPoints,
            double awayTeamAvgPoints,
            double homeTeamWonMatchesAtHome,
            double awayTeamWonMatchesAtAway,
            double homeTeamGoalsScoredAtHome,
            double awayTeamGoalsScoredAtAway) {
        dataRaw.clear();
        double[] instanceValue1 =
                new double[] {
                    homeTeamGoalsScored,
                    awayTeamGoalsScored,
                    homeTeamGoalsConceded,
                    awayTeamGoalsConceded,
                    homeTeamAvgPoints,
                    awayTeamAvgPoints,
                    homeTeamWonMatchesAtHome,
                    awayTeamWonMatchesAtAway,
                    homeTeamGoalsScoredAtHome,
                    awayTeamGoalsScoredAtAway,
                    0
                };
        dataRaw.add(new DenseInstance(1.0, instanceValue1));
        return dataRaw;
    }

    public String classifiy(Instances insts, String path) {
        String result = "Not classified!!";
        Classifier cls;
        try {
            cls = (MultilayerPerceptron) SerializationHelper.read(path);
            result = classVal.get((int) cls.classifyInstance(insts.firstInstance()));
        } catch (Exception ex) {
            log.error("Error classifying!");
        }
        return result;
    }
}
