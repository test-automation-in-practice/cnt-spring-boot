import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { NavigationModule } from './layout/navigation/navigation.module';
import { WeatherModule } from './pages/weather/weather.module';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DEFINED_ROUTES } from './routes';
import { EntryModule } from './pages/entry/entry.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(DEFINED_ROUTES),
    WeatherModule,
    EntryModule,
    NavigationModule,
    BrowserAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
