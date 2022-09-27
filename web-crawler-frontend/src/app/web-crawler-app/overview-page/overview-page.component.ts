import {Component} from "@angular/core";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {State, Store} from "@ngrx/store";
import {getMatchResultList} from "./state/match-result.actions";

import * as _moment from 'moment';
import {getMatchResultsSelector} from "./state/match-result.selector";
import {MatchResult} from "./model/matchResult";

const moment = _moment;

@Component({
  selector: 'overview-page',
  templateUrl: './overview-page.component.html',
  styleUrls: [
    './overview-page.component.scss'
  ],
})
export class OverviewPageComponent {

  matchResults!: MatchResult[];

  displayedColumns: string[] = ['homeTeam', 'matchOutcome', 'awayTeam'];

  constructor(private store: Store<State<any>>) {
  }

  addEvent(type: string, event: MatDatepickerInputEvent<unknown, unknown | null>) {
    // @ts-ignore
    let date = moment(event.value);
    let selDate = date.format('DD');
    let selMonth = date.format('MM');
    let selYear = date.format('YYYY');
    let dateString = selYear+'-'+selMonth+'-'+selDate;
    // @ts-ignore
    this.store.dispatch(getMatchResultList({date: dateString}))

    this.store.select(getMatchResultsSelector).pipe().subscribe( matchResults => {
      // @ts-ignore
      this.matchResults = matchResults;
      console.log(this.matchResults);
    });
  }
}
