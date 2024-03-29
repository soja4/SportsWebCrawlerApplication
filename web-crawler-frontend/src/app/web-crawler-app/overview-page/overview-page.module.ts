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
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTableModule} from "@angular/material/table";

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
    StoreModule.forFeature('isLoading', matchResultReducer),
    RouterModule.forChild(routes),
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
    MatMomentDateModule,
    BrowserAnimationsModule,
    EffectsModule.forFeature([OverviewPageComponentEffects]),
    HttpClientModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
  ],
})
export class OverviewPageModule {}
