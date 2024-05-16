import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Foro } from '../interfaces/foro.interface';
import { ForoRespuesta } from '../interfaces/fororespuesta.interface';
import { environment } from '../../environments/environment';
import { Usuario } from '../interfaces/usuario.interface';


@Injectable({
  providedIn: 'root'
})
export class ForoService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Método para obtener todos los mensajes del foro
  // ForoService
  obtenerMensajesDelForo(): Observable<Foro[]> {
   return this.http.get<Foro[]>(`${this.apiUrl}/foro/`);
  }

  // Método para enviar un nuevo mensaje al foro
  enviarMensaje(mensaje: Foro): Observable<Foro> {
    return this.http.post<Foro>(`${this.apiUrl}foro/`, mensaje);
  }

  eliminarMensaje(mensajeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/foro/${mensajeId}/`);
  }

  // Función para obtener las respuestas de un mensaje específico
  obtenerRespuestasDeMensaje(mensajeId: number): Observable<ForoRespuesta[]> {
    return this.http.get<ForoRespuesta[]>(`${this.apiUrl}foro-respuestas/?mensaje=${mensajeId}`);
  }

  enviarRespuesta(respuesta: ForoRespuesta): Observable<ForoRespuesta> {
    return this.http.post<ForoRespuesta>(`${this.apiUrl}foro-respuestas/`, respuesta);
  }

  eliminarRespuesta(respuestaId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}foro-respuestas/${respuestaId}/eliminar/`);
  }

  obtenerUsuario(token: string): Observable<Usuario> {
    // Envía el token en el cuerpo de la solicitud POST
    return this.http.post<Usuario>(`${this.apiUrl}obtener-usuario/`, { token });
  }
}
