import { getHarness } from '@jscutlery/cypress-harness';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatButtonHarness } from '@angular/material/button/testing';

const searchLocation = (location: string) => {
  getHarness(MatInputHarness.with({ placeholder: 'Enter location' })).then((input) => input.setValue(location));
  getHarness(MatButtonHarness.with({ text: 'Search' })).then((button) => button.click());
};

export const WeatherApp = {
  searchLocation,
};
