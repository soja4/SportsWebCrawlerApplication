import {createAction, props} from '@ngrx/store';
import {MatchResult} from "../model/matchResult";


export const getMatchResultList = createAction(
  '[MatchResult] Get list of match results',
);


export const getMatchResultListSuccess = createAction(
  '[MatchResult] Get list of match results success',
  props<{ matchResults: MatchResult[] }>()
);

export const getMatchResultListError = createAction(
  '[MatchResult] Get list of match results error',
);
