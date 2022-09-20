import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {MatchResult} from "../model/matchResult";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OverviewPageComponentService {

  constructor (private http: HttpClient) {}

  getMatches(date: Date): Observable<MatchResult[]> {
    return this.http.get<MatchResult[]>('/match-result/to-be-played/' + date);
  }
}
