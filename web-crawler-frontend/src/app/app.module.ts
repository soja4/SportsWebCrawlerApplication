import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {OverviewPageModule} from "./web-crawler-app/overview-page/overview-page.module";

import {StoreModule} from '@ngrx/store';
import {matchResultReducer} from "./web-crawler-app/overview-page/state/match-result.reducer";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    OverviewPageModule,
    StoreModule.forRoot({count: matchResultReducer})
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
