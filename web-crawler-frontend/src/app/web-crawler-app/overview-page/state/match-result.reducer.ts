import {createReducer, on} from '@ngrx/store';
import {getMatchResultListSuccess} from "./match-result.actions";
import {MatchResult} from "../model/matchResult";

export interface MatchResultState {
  matchResults: MatchResult[] // Pass the entity type, on this case Entity[]
}

export const initialState: MatchResultState = {
  matchResults: []
};

export const matchResultReducer = createReducer(initialState, on(getMatchResultListSuccess, (state, action) => ({
  ...state,
  matchResults: action.matchResults
})))

/*export function matchResultReducer(state: MatchResult[] = [], action: Action) {
  switch (action.type) {
    case getMatchResultListSuccess:
      return {};

    default:
      return state;
  }
}*/

