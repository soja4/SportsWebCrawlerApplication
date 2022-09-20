import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {OverviewPageComponentService} from "./match-result.service";
import {getMatchResultList, getMatchResultListError, getMatchResultListSuccess} from "./match-result.actions";
import {catchError, map, of, switchMap} from "rxjs";

@Injectable()
export class OverviewPageComponentEffects {
  constructor(private actions$: Actions, private overviewPageComponentService: OverviewPageComponentService) {
  }

  getMatches$ =
    createEffect(() =>
    this.actions$.pipe(
      ofType(getMatchResultList),
      switchMap((action) => this.overviewPageComponentService.getMatches(action.date).pipe(
        map((matchResults) => getMatchResultListSuccess({matchResults})),
        catchError(() => of(getMatchResultListError()))
      ))
    ));
}
