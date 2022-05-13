import { Component, EventEmitter, Input, Output } from '@angular/core';
import { WeatherLocation } from '../../service/weather.service';

@Component({
  selector: 'examples-weather-results',
  templateUrl: './weather-results.component.html',
  styleUrls: ['./weather-results.component.scss'],
})
export class WeatherResultsComponent {
  @Input() weatherLocations: WeatherLocation[] = [];
  @Output() savedLocation = new EventEmitter<WeatherLocation>();

  saveLocation($event: MouseEvent, location: WeatherLocation) {
    $event.stopPropagation();
    this.savedLocation.emit(location);
  }

  locationId(location: WeatherLocation): string {
    const locationForId = location.location.replace(' ', '-');
    return `location-${locationForId}`;
  }
}
