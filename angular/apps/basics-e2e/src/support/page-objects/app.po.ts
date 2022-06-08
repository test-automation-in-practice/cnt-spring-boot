import { getHarness } from '@jscutlery/cypress-harness';
import { MatButtonHarness } from '@angular/material/button/testing';

const navigateTo = (navbutton: string) => {
  cy.visit('http://localhost:4200/');
  getHarness(MatButtonHarness.with({ text: navbutton })).then((button) => button.click());
};

export const App = {
  navigateTo,
};
