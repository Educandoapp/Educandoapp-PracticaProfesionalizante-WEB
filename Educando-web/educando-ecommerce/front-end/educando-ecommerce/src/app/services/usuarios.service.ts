import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../interfaces/usuario.interface';
import { environment } from 'src/environments/environment';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class UsuariosService {

  // URL de la API
  private apiUrl =  environment.apiUrl;

  constructor(
    private http: HttpClient
  ) {}

  // Obtiene todos los usuarios
  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}usuario`);
  }

  // Obtiene un usuario por su ID
  getUsuarioById(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}usuario/${id}/`);
  }

  // Crea un nuevo usuario
  crearUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.apiUrl}usuario`, usuario);
  }

   // Método para actualizar un usuario existente por su ID
   actualizarUsuario(id: number, usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}usuarios/${id}/`, usuario);
  }

  obtenerUsuarioConectado(token: string): Observable<Usuario> {
    // Envía el token en el cuerpo de la solicitud POST
    return this.http.post<Usuario>(`${this.apiUrl}obtener-usuario/`, { token });
  }

  validarPassword(passwordPlana: string, passwordEncriptada: string): Observable<{ mensaje: string }> {
    const url = `${this.apiUrl}auth/validar_password/`;
    const body = { password_plana: passwordPlana, password_encriptada: passwordEncriptada };
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<{ mensaje: string }>(url, body, { headers });
  }

}
