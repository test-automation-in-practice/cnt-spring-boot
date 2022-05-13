import { ErrorMessageComponent } from './error-message.component';
import { MockBuilder, MockRender } from 'ng-mocks';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

describe('A user triggering an error', () => {
  beforeEach(() => MockBuilder(ErrorMessageComponent).mock(MatCardModule).mock(MatIconModule));

  it('can get it displayed', () => {
    const fixture = MockRender(ErrorMessageComponent, { message: 'Error message will be visible!' });
    expect(fixture).toMatchSnapshot();
  });
});
