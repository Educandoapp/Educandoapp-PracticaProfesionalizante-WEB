import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl,FormBuilder } from '@angular/forms';
import { ContactoService } from 'src/app/services/contacto.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.component.html',
  styleUrls: ['./contacto.component.css']
})

export class ContactoComponent {
  contactoForm: FormGroup;

  constructor(private contactoService: ContactoService) {
    this.contactoForm = new FormGroup({
      nombre: new FormControl('', Validators.required),
      apellido: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      mensaje: new FormControl('', Validators.required)
    });
  }

  // Lógica para enviar el formulario
  onSubmit() {
    if (this.contactoForm.valid) {
      const formData = this.contactoForm.value;

      // Enviar solicitud POST al backend
      this.contactoService.enviarMensaje(formData).subscribe(
        response => {
          Swal.fire({
            icon: 'success',
            title: '¡Mensaje enviado!',
            text: 'Gracias por contactarnos.',
          }).then(() => {
            // Limpiar el formulario después de mostrar la alerta
            this.contactoForm.reset();
          });
        },
        error => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Ha ocurrido un error al enviar tu mensaje. Por favor, inténtalo de nuevo más tarde.',
          });
        }
      );
    } else {
      // Mostrar una alerta de error si el formulario no es válido
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Por favor, completa correctamente todos los campos del formulario.',
      });
    }
  }
}
