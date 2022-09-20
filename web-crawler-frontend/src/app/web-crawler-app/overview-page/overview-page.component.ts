import {Component} from "@angular/core";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {State, Store} from "@ngrx/store";
import {getMatchResultList} from "./state/match-result.actions";

@Component({
  selector: 'overview-page',
  templateUrl: './overview-page.component.html',
  styleUrls: [
    './overview-page.component.scss'
  ],
})
export class OverviewPageComponent {

  constructor(private store: Store<State<any>>) {
  }

  addEvent(type: string, event: MatDatepickerInputEvent<unknown, unknown | null>) {
    // @ts-ignore
    this.store.dispatch(getMatchResultList({date: event.value}))
  }
}
