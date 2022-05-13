import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, catchError, map, Observable, of, tap } from 'rxjs';

export interface WeatherApiModel {
  temp: number;
  name: string;
}

export interface WeatherLocation {
  temp: number;
  location: string;
}

export interface LocationApiModel {
  name: string;
}

@Injectable()
export class WeatherService {
  static ENDPOINTS = {
    mainLocation: () => `${environment.weatherApi}/mainLocation`,
    queryLocation: (location: string) => `${environment.weatherApi}/locations?q=${location}`,
  };

  private _weather$ = new BehaviorSubject<WeatherLocation[]>([]);
  readonly weather$: Observable<WeatherLocation[]> = this._weather$.asObservable();
  private _warning = new BehaviorSubject<string | undefined>(undefined);
  readonly warning$: Observable<string | undefined> = this._warning.asObservable();
  private _isLoading$ = new BehaviorSubject<boolean>(false);
  readonly isLoading$: Observable<boolean> = this._isLoading$.asObservable();
  private _mainLocation$ = new BehaviorSubject<string | undefined>(undefined);
  readonly mainLocation$: Observable<string | undefined> = this._mainLocation$.asObservable();

  constructor(private http: HttpClient) {}

  private static mapApiModel(apiModel: WeatherApiModel[]): WeatherLocation[] {
    return apiModel.map((apiWeather) => ({ temp: apiWeather.temp, location: apiWeather.name }));
  }

  getMainLocation(): void {
    this._isLoading$.next(true);
    this.http
      .get<LocationApiModel>(WeatherService.ENDPOINTS.mainLocation())
      .pipe(
        tap(() => this._warning.next(undefined)),
        tap(() => this._isLoading$.next(false)),
        catchError((error) => this.handleError(error, { name: undefined }))
      )
      .subscribe((location) => this._mainLocation$.next(location.name));
  }

  getWeatherForLocation(location: string): void {
    this._isLoading$.next(true);
    this.http
      .get<WeatherApiModel[]>(WeatherService.ENDPOINTS.queryLocation(location))
      .pipe(
        tap(() => this._warning.next(undefined)),
        tap(() => this._isLoading$.next(false)),
        map((apiModel) => WeatherService.mapApiModel(apiModel)),
        catchError((error) => this.handleError(error, []))
      )
      .subscribe((weather) => this._weather$.next(weather));
  }

  saveMainLocation(location: string) {
    this._isLoading$.next(true);
    this.http
      .post<LocationApiModel>(WeatherService.ENDPOINTS.mainLocation(), { name: location })
      .pipe(
        tap(() => this._warning.next(undefined)),
        tap(() => this._isLoading$.next(false)),
        catchError((error) => this.handleError(error, undefined))
      )
      .subscribe(() => this._mainLocation$.next(location));
  }

  private handleError<S>(error: HttpErrorResponse, endState: S) {
    this._warning.next(error.error.message);
    this._isLoading$.next(false);
    return of(endState);
  }
}
