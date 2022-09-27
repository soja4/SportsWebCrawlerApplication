import {createFeatureSelector, createSelector} from "@ngrx/store";

export const getMatchResultsSelector = createSelector(
  createFeatureSelector('matchResults'),
  (state: any) => state.matchResults
);
