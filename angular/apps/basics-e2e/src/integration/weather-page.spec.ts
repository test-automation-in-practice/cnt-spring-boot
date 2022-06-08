import { App } from '../support/page-objects/app.po';
import { WeatherApp } from '../support/page-objects/weather.po';
import { Setup } from '../support/helpers/setup';

describe('Weather page', () => {
  before(() => {
    App.navigateTo('Weather');
    Setup.resetMainLocation();
    Setup.resetLocations();
    cy.get('basics-location').should('exist');
  });

  it('should return the results for a search for stuttgart', () => {
    WeatherApp.searchLocation('Stuttgart');
    cy.get('[id^=location]').should('have.length', 3);
  });

  it('should display the error for a non existent city', () => {
    const location = 'ThisCityDoesNotExist';
    WeatherApp.searchLocation(location);
    cy.get('basics-error-message').should('contain.text', `${location}`);
  });
});
