import { Type } from '@angular/core';
import { WeatherPageComponent } from './pages/weather/weather-page.component';
import { EntryComponent } from './pages/entry/entry.component';

class DefinedRoute {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  constructor(public path: string, public component: Type<any>, public displayName: string) {}

  buildRouterLink() {
    return '/' + this.path.replace('**', '');
  }
}

export const DEFINED_ROUTES: DefinedRoute[] = [
  new DefinedRoute('weather', WeatherPageComponent, 'Weather'),
  new DefinedRoute('**', EntryComponent, 'Home'),
];
