import '../../testing-helpers/window.mock';
import { MockBuilder, MockRender, MockService } from 'ng-mocks';
import { WeatherPageComponent } from './weather-page.component';
import { WeatherModule } from './weather.module';
import { WeatherLocation, WeatherService } from './service/weather.service';
import { of } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { environment } from '../../../environments/environment';
import { ApiModelGenerators } from '../../testing-helpers/api-model-generators';
import { MatCardHarness } from '@angular/material/card/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatCardModule } from '@angular/material/card';
import { WeatherIntroductionComponent } from './paragraphs/introduction/weather-introduction.component';
import { WeatherExplanationComponent } from './paragraphs/explanation/weather-explanation.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { LocationComponent } from './components/location/location.component';
import { ErrorMessageComponent } from './components/error-message/error-message.component';
import { LoadingComponent } from './components/loading/loading/loading.component';
import { WeatherResultsComponent } from './components/result/weather-results.component';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterTestingModule } from '@angular/router/testing';

type serviceMockProps = {
  isLoading?: boolean;
  warning?: string | undefined;
  weather?: WeatherLocation[];
  mainLocation?: string | undefined;
};

function createServiceMock(opts: serviceMockProps) {
  const stateOverrides = {
    isLoading$: of(!!opts.isLoading),
    warning$: of(opts.warning),
    weather$: of(opts.weather ? opts.weather : []),
    mainLocation$: of(opts.mainLocation),
  };
  return MockService(WeatherService, stateOverrides);
}

const MaterialModules = [MatCardModule, MatExpansionModule, MatInputModule, MatButtonModule, MatDividerModule, MatProgressSpinnerModule];

describe('A user visiting the Component Testing Page', () => {
  /**
   * These tests are testing the complete module and give us confidence for bigger refactorings.
   * They are rather brittle and thus, we have to use them carefully.
   * Further, they do not give good insights in why a specific scenario is failing.
   * This loads to a need of additional tests for your components to get better feedback.
   */
  describe('and having their data loaded', () => {
    let fixture: ComponentFixture<WeatherPageComponent>;
    let loader: HarnessLoader;
    let controller: HttpTestingController;

    beforeEach(async () => {
      return TestBed.configureTestingModule({
        declarations: [
          WeatherPageComponent,
          WeatherIntroductionComponent,
          WeatherExplanationComponent,
          LocationComponent,
          ErrorMessageComponent,
          LoadingComponent,
          WeatherResultsComponent,
        ],
        imports: [NoopAnimationsModule, HttpClientTestingModule, ReactiveFormsModule, ...MaterialModules],
        providers: [WeatherService],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(WeatherPageComponent);
      loader = TestbedHarnessEnvironment.loader(fixture);
      controller = TestBed.inject(HttpTestingController);
    });

    it('should load their saved location', async () => {
      const location = 'stuttgart';
      const temp = 25;

      const locationRequest = controller.expectOne(`${environment.weatherApi}/mainLocation`);
      locationRequest.flush(ApiModelGenerators.createLocationApiModel(location));
      const weatherRequest = controller.expectOne(`${environment.weatherApi}/locations?q=${location}`);
      weatherRequest.flush(ApiModelGenerators.createWeatherApiModel(location, temp));

      await loader.getHarness(MatCardHarness.with({ subtitle: new RegExp(`.*(${location}).*`) }));
    });

    it('should load the weather for their saved location', async () => {
      const location = 'stuttgart';
      const temp = 25;

      const locationRequest = controller.expectOne(`${environment.weatherApi}/mainLocation`);
      locationRequest.flush(ApiModelGenerators.createLocationApiModel(location));
      const weatherRequest = controller.expectOne(`${environment.weatherApi}/locations?q=${location}`);
      weatherRequest.flush(ApiModelGenerators.createWeatherApiModel(location, temp));

      await loader.getHarness(MatCardHarness.with({ title: location }));
    });

    it('should be able to search for a location', async () => {
      const location = 'stuttgart';
      const temp = 25;

      const locationRequest = controller.expectOne(`${environment.weatherApi}/mainLocation`);
      locationRequest.flush(ApiModelGenerators.createLocationApiModel(location));
      const weatherRequest = controller.expectOne(`${environment.weatherApi}/locations?q=${location}`);
      weatherRequest.flush(ApiModelGenerators.createWeatherApiModel(location, temp));

      const locationCard = await loader.getHarness(MatCardHarness.with({ subtitle: new RegExp(`.*(location).*`) }));
      const locationInput = await locationCard.getHarness(MatInputHarness);
      const searchedWeather = 'Frankfurt';
      await locationInput.setValue(searchedWeather);
      const searchButton = await locationCard.getHarness(MatButtonHarness);
      await searchButton.click();
      const searchedWeatherRequest = controller.expectOne(`${environment.weatherApi}/locations?q=${searchedWeather}`);
      searchedWeatherRequest.flush(ApiModelGenerators.createWeatherApiModel(searchedWeather, temp));

      await loader.getHarness(MatCardHarness.with({ title: searchedWeather }));
    });
  });

  describe('should have a consistent layout', () => {
    beforeEach(() => MockBuilder(WeatherPageComponent, WeatherModule));

    it('if the service is loading', () => {
      const service = createServiceMock({ isLoading: true });
      const fixture = MockRender(WeatherPageComponent, {}, { providers: [{ provide: WeatherService, useValue: service }] });
      expect(fixture).toMatchSnapshot();
    });

    it('if the service has a warning', () => {
      const service = createServiceMock({ warning: 'This is a warning!' });
      const fixture = MockRender(WeatherPageComponent, {}, { providers: [{ provide: WeatherService, useValue: service }] });
      expect(fixture).toMatchSnapshot();
    });

    it('if the service has no warning', () => {
      const service = createServiceMock({
        weather: [
          { location: 'Stuttgart', temp: 23 },
          { location: 'Frankfurt', temp: 15 },
        ],
      });
      const fixture = MockRender(WeatherPageComponent, {}, { providers: [{ provide: WeatherService, useValue: service }] });
      expect(fixture).toMatchSnapshot();
    });
  });
});
