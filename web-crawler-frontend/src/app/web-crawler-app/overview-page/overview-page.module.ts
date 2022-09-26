import {RouterModule, Routes} from "@angular/router";
import {OverviewPageComponent} from "./overview-page.component";
import {NgModule} from "@angular/core";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatInputModule} from "@angular/material/input";
import {MatNativeDateModule} from "@angular/material/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {EffectsModule} from "@ngrx/effects";
import {OverviewPageComponentEffects} from "./state/match-result.effects";
import {HttpClientModule} from "@angular/common/http";
import {MatMomentDateModule} from "@angular/material-moment-adapter";
import {StoreModule} from "@ngrx/store";
import {matchResultReducer} from "./state/match-result.reducer";

const routes: Routes = [
  {
    path: '',
    component: OverviewPageComponent,
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];

@NgModule({
  declarations: [
    OverviewPageComponent,
  ],
  providers: [],
  imports: [
    StoreModule.forFeature('matchResults', matchResultReducer),
    RouterModule.forChild(routes),
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
    MatMomentDateModule,
    BrowserAnimationsModule,
    EffectsModule.forFeature([OverviewPageComponentEffects]),
    HttpClientModule,
  ],
})
export class OverviewPageModule {}
