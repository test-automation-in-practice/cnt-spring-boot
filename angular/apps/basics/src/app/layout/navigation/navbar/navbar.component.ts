import { Component } from '@angular/core';
import { DEFINED_ROUTES } from '../../../routes';

@Component({
  selector: 'basics-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  routes = DEFINED_ROUTES.filter((route) => !!route.displayName);
}
