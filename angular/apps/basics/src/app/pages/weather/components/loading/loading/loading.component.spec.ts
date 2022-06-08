import { LoadingComponent } from './loading.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MockBuilder, MockRender } from 'ng-mocks';

describe('A loading component', () => {
  beforeEach(() => MockBuilder(LoadingComponent).mock(MatProgressSpinnerModule));

  it('should render a spinner', () => {
    const fixture = MockRender(LoadingComponent);
    expect(fixture).toMatchSnapshot();
  });
});
