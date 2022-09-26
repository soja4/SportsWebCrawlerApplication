import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {MatchResult} from "../model/matchResult";
import {Observable} from "rxjs";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class OverviewPageComponentService {

  private apiServerUrl = environment.apiBaseUrl;

  constructor (private http: HttpClient) {}

  getMatches(date: Date): Observable<MatchResult[]> {
    return this.http.get<MatchResult[]>(this.apiServerUrl + '/match-result/to-be-played/' + date);
  }
}
