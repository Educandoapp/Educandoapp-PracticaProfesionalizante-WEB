import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ForoService } from '../../services/foro.service';
import { Foro } from '../../interfaces/foro.interface';
import { ForoRespuesta } from '../../interfaces/fororespuesta.interface';
import { Usuario } from 'src/app/interfaces/usuario.interface';

@Component({
  selector: 'app-foro',
  templateUrl: './foro.component.html',
  styleUrls: ['./foro.component.css']
})


export class ForoComponent implements OnInit {
  mensajes: Foro[] = [];
  respuestas: ForoRespuesta[] = [];
  mensajeActual: Foro | null = null;
  respuestasPorMensaje: { [key: number]: ForoRespuesta[] } = {};
  mostrarFormulario: boolean = false;
  nuevoTema: Foro = {
    id_foro: 0,
    id_usuarios: 1,
    id_rol: null,
    titulo: '',
    nombre: '',
    mensaje: '',
    fecha: new Date(),
    respondiendo: false,
    id_foro_respuesta_id: 1
  }
  id_usuario: number | null = null; 
  ;

  @ViewChild('areaRespuesta') areaRespuestaElement!: ElementRef;

  constructor(private foroService: ForoService) { }

  ngOnInit(): void {
    this.obtenerMensajesDelForo();
    this.obtenerIdUsuario();
  }

  obtenerIdUsuario() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No se pudo obtener el token del usuario.');
      return;
    }

    this.foroService.obtenerUsuario(token).subscribe(
      (usuario: Usuario) => {
        this.id_usuario = usuario.id_usuario;
      },
      (error) => {
        console.error('Error al obtener el usuario:', error);
      }
    );
  }

  // Método para verificar si el usuario logueado es el mismo que generó la respuesta
  esUsuarioActual(id_usuario_respuesta: number): boolean {
    return this.id_usuario == id_usuario_respuesta;
  }
   
  obtenerMensajesDelForo() {
    this.foroService.obtenerMensajesDelForo()
      .subscribe(
        (mensajes: Foro[]) => {
          this.mensajes = mensajes;
          this.obtenerRespuestasPorMensaje();
        },
        (error) => {
          console.error('Error al obtener mensajes del foro:', error);
        }
      );
  }

  obtenerRespuestasPorMensaje() {
    for (const mensaje of this.mensajes) {
      this.foroService.obtenerRespuestasDeMensaje(mensaje.id_foro)
        .subscribe(
          (respuestas: ForoRespuesta[]) => {
            this.respuestasPorMensaje[mensaje.id_foro] = respuestas;
          },
          (error) => {
            console.error('Error al obtener respuestas del mensaje:', error);
          }
        );
    }
  }

  cancelarRespuesta(): void {
    this.mensajeActual!.respondiendo = false;
    this.mensajeActual = null;
    this.areaRespuestaElement.nativeElement.value = '';
  }

  enviarRespuesta(respuesta: string, mensaje: Foro) {
    // Obtener el usuario actual del servicio
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No se pudo obtener el token del usuario.');
      return;
    }
  
    this.foroService.obtenerUsuario(token).subscribe(
      (usuario: Usuario) => {
        // Crear la nueva respuesta con el nombre del usuario obtenido
        const nuevaRespuesta: ForoRespuesta = {
          id_foro_respuesta: 0,
          id_foro: mensaje.id_foro,
          mensaje: respuesta,
          fecha: new Date(),
          nombre: `${usuario.nombre} ${usuario.apellido}`, // Agregamos el nombre del usuario
          id_usuario: usuario.id_usuario  // Agregamos el id_usuario
        };
    
        // Enviar la nueva respuesta al servidor
        this.foroService.enviarRespuesta(nuevaRespuesta).subscribe(
          (respuestaCreada: ForoRespuesta) => {
            if (!this.respuestasPorMensaje[mensaje.id_foro]) {
              this.respuestasPorMensaje[mensaje.id_foro] = [];
            }
            this.respuestasPorMensaje[mensaje.id_foro].push(respuestaCreada);
    
            mensaje.respondiendo = false;
            this.mensajeActual = null;
            this.areaRespuestaElement.nativeElement.value = '';
          },
          (error) => {
            console.error('Error al enviar la respuesta:', error);
          }
        );
      },
      (error) => {
        console.error('Error al obtener el usuario:', error);
      }
    );
  }
  

  eliminarRespuesta(respuestaId: number): void {
    this.foroService.eliminarRespuesta(respuestaId).subscribe();
    this.obtenerMensajesDelForo();
  }


  eliminarMensaje(mensajeId: number): void {
    this.foroService.eliminarMensaje(mensajeId).subscribe(
      () => {
        this.mensajes = this.mensajes.filter(m => m.id_foro !== mensajeId);
        console.log('Mensaje eliminado correctamente');
      },
      (error) => {
        console.error('Error al eliminar el mensaje:', error);
      }
    );
  }

  mostrarAreaRespuesta(mensaje: Foro) {
    mensaje.respondiendo = true;
    this.mensajeActual = mensaje;
  }

  mostrarFormularioAgregarTema() {
    this.mostrarFormulario = true;
  }

  ocultarFormularioAgregarTema() {
    this.mostrarFormulario = false;
  }

  agregarNuevoTema() {
    // Obtener el usuario actual del servicio
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No se pudo obtener el token del usuario.');
      return;
    }
  
    this.foroService.obtenerUsuario(token).subscribe(
      (usuario: Usuario) => {
        // Crear el nuevo tema con el usuario obtenido
        const nuevoTemaConUsuario: Foro = {
          id_foro: 0,
          id_usuarios: usuario.id_usuario, // Usar directamente el id_usuario
          id_rol: usuario.id_rol_id, // Usar el rol del usuario obtenido del servicio
          titulo: this.nuevoTema.titulo,
          nombre: `${usuario.nombre} ${usuario.apellido}`,
          mensaje: this.nuevoTema.mensaje,
          fecha: new Date(),
          respondiendo: false,
          id_foro_respuesta_id: 1
        };
  
        console.log('Mensaje a enviar al servidor:', nuevoTemaConUsuario); 
        // Enviar el nuevo tema al servidor
        this.foroService.enviarMensaje(nuevoTemaConUsuario).subscribe(
          (mensajeCreado: Foro) => {
            // Agregar el mensaje creado a la lista de mensajes y limpiar el formulario
            console.log(mensajeCreado); 
            this.mensajes.unshift(mensajeCreado);
            this.nuevoTema = {
              id_foro: 0,
              id_usuarios: 1,
              id_rol: null,
              titulo: '',
              nombre: '',
              mensaje: '',
              fecha: new Date(),
              respondiendo: false,
              id_foro_respuesta_id: 1
            };
            this.ocultarFormularioAgregarTema();
            this.obtenerMensajesDelForo();
          },
          (error) => {
            console.error('Error al enviar el nuevo tema:', error);
          }
        );
      },
      (error) => {
        console.error('Error al obtener el usuario:', error);
      }
    );
  }
  
}