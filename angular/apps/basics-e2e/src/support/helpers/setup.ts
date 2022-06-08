const resetMainLocation = () => {
  cy.request('POST', 'http://localhost:4201/mainLocation', { name: '' }).its('status').should('be.ok');
};

const resetLocations = () => {
  cy.fixture('locations').then((locations: { id: string; name: string; temp: number }[]) => {
    const create = () => locations.forEach((location) => cy.request('POST', 'http://localhost:4201/locations', location));
    cy.request('http://localhost:4201/locations').then((resp) => {
      const body = resp.body as { id: string }[];
      body.forEach(({ id }, index) => {
        cy.request('DELETE', `http://localhost:4201/locations/${id}`);
        if (index === body.length - 1) {
          create();
        }
      });
      if (body.length === 0) {
        create();
      }
    });
  });
};

export const Setup = {
  resetMainLocation,
  resetLocations,
};
