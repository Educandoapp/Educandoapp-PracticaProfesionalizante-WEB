import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}


  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    console.log('Verificando autenticación...');

    if (this.authService.estaAutenticado()) {
      console.log('Usuario autenticado.');
      return true;
    }

    // Si el usuario no está autenticado, redirige al inicio de sesión
    console.log('Usuario no autenticado. Redirigiendo al inicio de sesión...');
    this.router.navigate(['/login']);
    console.log('Redirigiendo al inicio de sesión...');
    return false;
  }

}
