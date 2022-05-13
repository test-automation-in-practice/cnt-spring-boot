import { MockBuilder, MockRender } from 'ng-mocks';
import { WeatherResultsComponent } from './weather-results.component';
import { WeatherModule } from '../../weather.module';
import { WeatherLocation } from '../../service/weather.service';
import { By } from '@angular/platform-browser';

describe('A user', () => {
  describe('viewing his results', () => {
    beforeEach(() => MockBuilder(WeatherResultsComponent, WeatherModule));

    it('should see a consistent layout', () => {
      const weatherLocations: WeatherLocation[] = [
        { temp: 25, location: 'Stuttgart' },
        { temp: 22, location: 'Freiburg' },
      ];
      const fixture = MockRender(WeatherResultsComponent, { weatherLocations });
      expect(fixture).toMatchSnapshot();
    });

    it('should be able to save his location', () => {
      const stuttgartLocation = { temp: 25, location: 'Stuttgart' };
      const weatherLocations: WeatherLocation[] = [stuttgartLocation, { temp: 22, location: 'Freiburg' }];
      const fixture = MockRender(WeatherResultsComponent, { weatherLocations });
      jest.spyOn(fixture.point.componentInstance.savedLocation, 'emit');
      const card = fixture.debugElement.query(By.css(`#location-${stuttgartLocation.location}`));
      const button = card.query(By.css('button')).nativeElement;
      button.click();
      expect(fixture.point.componentInstance.savedLocation.emit).toHaveBeenCalledWith(stuttgartLocation);
    });
  });
});
