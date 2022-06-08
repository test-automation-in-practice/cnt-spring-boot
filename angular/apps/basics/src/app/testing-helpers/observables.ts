import { lastValueFrom, Observable, take } from 'rxjs';

export function toPromise<S>(obs: Observable<S>): Promise<S> {
  return lastValueFrom(obs.pipe(take(1)));
}
