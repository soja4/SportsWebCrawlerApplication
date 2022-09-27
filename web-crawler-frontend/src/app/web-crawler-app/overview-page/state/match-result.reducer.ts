import {createReducer, on} from '@ngrx/store';
import {getMatchResultList, getMatchResultListSuccess} from "./match-result.actions";
import {MatchResult} from "../model/matchResult";

export interface MatchResultState {
  matchResults: MatchResult[],
  isLoading: boolean
}

export const initialState: MatchResultState = {
  matchResults: [],
  isLoading: false
};

export const matchResultReducer = createReducer(initialState,
  on(getMatchResultListSuccess, (state, action) => ({
    ...state,
    matchResults: action.matchResults,
    isLoading: false
  })),
  on(getMatchResultList, (state) => ({
    ...state,
    isLoading: true
  })))

