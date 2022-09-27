import {createFeatureSelector, createSelector} from "@ngrx/store";

export const getMatchResultsSelector = createSelector(
  createFeatureSelector('matchResults'),
  (state: any) => state.matchResults
);

export const getIsLoading = createSelector(
  createFeatureSelector('isLoading'),
  (state: any) => state.isLoading
);
