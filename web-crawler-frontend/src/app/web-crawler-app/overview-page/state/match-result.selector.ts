import {createFeatureSelector, createSelector} from "@ngrx/store";
import {MatchResult} from "../model/matchResult";

export const getMatchResultsSelector = createSelector(
  createFeatureSelector('matchResults'),
  (state:any) => state.matchResults,
  (matchResults: MatchResult[]) => matchResults
);
