import { Component } from '@angular/core';

@Component({
    selector: 'app-tareas',
    templateUrl: './tareas.component.html',
    styleUrls: ['./tareas.component.css']
})
export class TareasComponent {
    recordatorios: any[] = [];
    nuevoRecordatorio = { titulo: '', descripcion: '', fecha: '', hora: '' };
    editando = false;
    recordatorioEditando: any = null;

    constructor() {
        this.cargarRecordatorios();
        this.verificarTareasProgramadas();
        setInterval(() => {
            this.verificarTareasProgramadas();
        }, 60000); // Verificar cada minuto
    }

    crearRecordatorio() {
        if (this.nuevoRecordatorio.titulo.trim() !== '' && this.nuevoRecordatorio.descripcion.trim() !== '' &&
            this.nuevoRecordatorio.fecha.trim() !== '' && this.nuevoRecordatorio.hora.trim() !== '') {
            const nuevoRecordatorio = {
                titulo: this.nuevoRecordatorio.titulo,
                descripcion: this.nuevoRecordatorio.descripcion,
                fecha: this.nuevoRecordatorio.fecha,
                hora: this.nuevoRecordatorio.hora
            };
            this.recordatorios.push(nuevoRecordatorio);
            this.guardarRecordatorios();
            this.nuevoRecordatorio = { titulo: '', descripcion: '', fecha: '', hora: '' };
        }
    }

    eliminarRecordatorio(index: number) {
        this.recordatorios.splice(index, 1);
        this.guardarRecordatorios();
    }

    editarRecordatorio(recordatorio: any) {
        this.nuevoRecordatorio = {
            titulo: recordatorio.titulo,
            descripcion: recordatorio.descripcion,
            fecha: recordatorio.fecha,
            hora: recordatorio.hora
        };
        this.recordatorioEditando = recordatorio;
        this.editando = true;
    }

    guardarCambios() {
        const index = this.recordatorios.indexOf(this.recordatorioEditando);
        if (index !== -1) {
            this.recordatorios[index] = {
                titulo: this.nuevoRecordatorio.titulo,
                descripcion: this.nuevoRecordatorio.descripcion,
                fecha: this.nuevoRecordatorio.fecha,
                hora: this.nuevoRecordatorio.hora
            };
            this.guardarRecordatorios();
            this.nuevoRecordatorio = { titulo: '', descripcion: '', fecha: '', hora: '' };
            this.recordatorioEditando = null;
            this.editando = false;
        }
    }

    cancelarEdicion() {
        this.nuevoRecordatorio = { titulo: '', descripcion: '', fecha: '', hora: '' };
        this.recordatorioEditando = null;
        this.editando = false;
    }

    private cargarRecordatorios() {
        const recordatoriosGuardados = localStorage.getItem('recordatorios');
        if (recordatoriosGuardados) {
            this.recordatorios = JSON.parse(recordatoriosGuardados);
        }
    }

    private guardarRecordatorios() {
        localStorage.setItem('recordatorios', JSON.stringify(this.recordatorios));
    }

    verificarTareasProgramadas() {
        const ahora = new Date();
        for (const tarea of this.recordatorios) {
            const fechaTarea = new Date(tarea.fecha + 'T' + tarea.hora);
            if (fechaTarea <= ahora) {
                this.mostrarPopupTarea(tarea);
            }
        }
    }

    mostrarPopupTarea(tarea: any) {
        alert(`¡Tarea programada cumplida!\n${tarea.titulo}\n${tarea.descripcion}`);
    }
}
