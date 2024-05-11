import { Component, OnInit } from '@angular/core';
import { UsuariosService } from 'src/app/services/usuarios.service';
import { Usuario } from 'src/app/interfaces/usuario.interface';
import { Router } from '@angular/router';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent implements OnInit {
  usuario: Usuario = {
    id_usuario: 0,
    email: '',
    nombre: '',
    apellido: '',
    password: '',
    id_rol_id: 0,    
    urlImagen: ''
    // Agrega otras propiedades si es necesario
  };

  id_usuario: number | null = null; 
  UrlImagenIngresada: string = '';
  nuevaPassword: string = '';
  contrasenasCoinciden: boolean = true; // Definir la propiedad contrasenasCoinciden
  confirmarPassword: string = ''; // Definir la propiedad confirmarPassword
  mostrarErrorContrasenas: boolean = false;
  mostrarExito: boolean = false;
  mostrarRepetido : boolean = false;


  constructor(private usuariosService: UsuariosService, private router: Router) { }

  ngOnInit(): void {
    this.obtenerIdUsuario();
    //this.obtenerDetallesUsuario();
  }

  obtenerDetallesUsuario(idUsuario: number) {     
    this.usuariosService.getUsuarioById(idUsuario).subscribe(
      (usuario: Usuario) => {
        this.usuario = usuario;
        this.UrlImagenIngresada = usuario.urlImagen;         
      },
      (error) => {
        console.error('Error al obtener los detalles del usuario:', error);
      }
    );
  }

  obtenerIdUsuario(){
    const token = localStorage.getItem('accessToken');
    if (!token) {        
        return;
    }
    this.usuariosService.obtenerUsuarioConectado(token).subscribe(
      (usuario: Usuario) => {
        this.id_usuario = usuario.id_usuario; 
        this.obtenerDetallesUsuario(this.id_usuario)
      },
      (error) => {
        console.error('Error al obtener el usuario:', error);
      }
    );    
  }

  actualizarUsuario(usuario: Usuario) {
    const usuarioActualizado = { 
      ...this.usuario,
      urlImagen: this.UrlImagenIngresada
     };
  
    if (this.nuevaPassword) {
      // Valida si la nueva contraseña coincide con la actual
      const passwordActual = this.usuario.password || ''; // Asigna una cadena vacía si this.usuario.password es undefined
    this.usuariosService.validarPassword(this.nuevaPassword, passwordActual).subscribe(
        (respuesta) => {
          if (respuesta.mensaje === 'Las contraseñas coinciden') {
            this.mostrarErrorContrasenas = false;
            this.mostrarExito = false;
            this.mostrarRepetido = true;
          } else {
            usuarioActualizado.password = this.nuevaPassword;
            this.actualizarUsuarioEnServidor(usuarioActualizado);
            this.mostrarExito = true;
            this.mostrarErrorContrasenas = false;
            this.mostrarExito = false;
          }
        },
        (error) => {
          console.error('Error al validar la contraseña:', error);
        }
      );
    } else {
      // No se proporcionó una nueva contraseña, se actualiza el usuario sin modificar la contraseña
      delete usuarioActualizado.password;
      // usuarioActualizado.password = null; // Asignar null (si se permite en el back-end)
      this.actualizarUsuarioEnServidor(usuarioActualizado);
    }
  }
  
  actualizarUsuarioEnServidor(usuarioActualizado: Usuario) {
    this.usuariosService.actualizarUsuario(this.usuario.id_usuario, usuarioActualizado).subscribe(
      (usuarioActualizadoDesdeServidor: Usuario) => {
        this.usuario = {
          ...this.usuario,
          ...usuarioActualizadoDesdeServidor,
          urlImagen: usuarioActualizadoDesdeServidor.urlImagen // Actualizar urlImagen con el valor recibido del servidor
        };
        this.mostrarErrorContrasenas = false;
        this.mostrarExito = true;
      },
      (error) => {
        console.error('Error al actualizar el usuario:', error);
      }
    );
  }
    
  cancelar():void{
    this.router.navigateByUrl('/admin');
  }
}

