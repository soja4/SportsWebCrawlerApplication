import {RouterModule, Routes} from "@angular/router";
import {OverviewPageComponent} from "./overview-page.component";
import {NgModule} from "@angular/core";

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
    RouterModule.forChild(routes),
  ],
})
export class OverviewPageModule {}
