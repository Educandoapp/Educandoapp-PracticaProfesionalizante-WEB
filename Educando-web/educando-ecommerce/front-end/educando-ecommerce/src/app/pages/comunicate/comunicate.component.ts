import { Component, NgModule } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-contacto',
  templateUrl: './comunicate.component.html',
  styleUrls: ['./comunicate.component.css']
})
export class ContactoComponent {
  contactoForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.contactoForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      mensaje: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.contactoForm.valid) {
      Swal.fire({
        icon: 'success',
        title: '¡Mensaje enviado!',
        text: 'Gracias por contactarnos.',
      }).then(() => {
        this.contactoForm.reset();
      });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Por favor, completa correctamente todos los campos del formulario.',
      });
    }
  }
}

@NgModule({
  declarations: [
    ContactoComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  exports: [
    ContactoComponent
  ]
})
export class ComunicateModule { }