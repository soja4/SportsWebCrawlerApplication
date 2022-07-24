package com.crawler.sport.domain;

public enum MatchOutcome {
    HOME_WIN(1.0),
    DRAW(0.0),
    AWAY_WIN(2.0);

    private double outcome;

    MatchOutcome(double outcome) {
        this.outcome = outcome;
    }

    public double getOutcome() {
        return outcome;
    }
}
