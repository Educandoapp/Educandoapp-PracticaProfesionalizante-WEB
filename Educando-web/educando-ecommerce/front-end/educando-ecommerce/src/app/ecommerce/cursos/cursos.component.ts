import { Component } from '@angular/core';
import { Categoria } from 'src/app/interfaces/categoria.interface';
import { Curso } from 'src/app/interfaces/cursos.interface';
import { CheckoutService } from 'src/app/services/checkout.service';
import { CursosService } from 'src/app/services/cursos.service';

const bootstrapColors = [
  'primary',
  'secondary',
  'success',
  'danger',
  'warning',
  'info',
  'light',
  'dark'
];

@Component({
  selector: 'app-cursos',
  templateUrl: './cursos.component.html',
  styleUrls: ['./cursos.component.css']
})
export class CursosComponent {
  total$ = this.checkoutService.totalAction$;
  categorias: Categoria | any | null;
  cursos: Curso | any | null;
  cursosFiltrados: Curso | any | null;
  sinCursos: boolean = false;
  tituloCursos: string = 'Todos nuestros cursos';
  categoriaSeleccionada: number | null = null; // Variable para almacenar la categoría seleccionada en la búsqueda avanzada
  tipoBusqueda: string = 'categoria'; // Por defecto, la búsqueda será por categoría
  nombreCurso: string = ''; // Variable para almacenar el nombre del curso en la búsqueda por nombre
  calificacionMinima: number | null = null; // Variable para almacenar la calificación mínima del curso
  precioMaximo: number | null = null; // Variable para almacenar el precio máximo del curso

  constructor(
    private checkoutService: CheckoutService,
    private cursosService: CursosService
  ) {}

  ngOnInit(): void {
    this.getCategorias();
    this.getCursos();
  }

  // Método que obtiene las categorías de los cursos.
  getCategorias(): void {
    this.cursosService.getCategorias().subscribe(
      (categorias: Categoria) => {
        const categoriasArray = Object.values(categorias);

        // Mapea las categorías y asigna una clase de color de Bootstrap a cada una.
        this.categorias = categoriasArray.map((categoria: any, index: number) => ({
          ...categoria,
          colorClass: 'btn-' + bootstrapColors[index % bootstrapColors.length]
        }));
      },
      (error) => {
        console.log(error);
      }
    );
  }

  // Método que obtiene la lista de cursos.
  getCursos(): void {
    this.cursosService.getCursos().subscribe(
      (cursos: Curso) => {
        const cursosArray = Object.values(cursos);
        this.cursos = cursosArray;
        this.cursosFiltrados = cursos;
      },
      (error) => {
        console.log(error);
      }
    );
  }

  // Método que filtra los cursos por categoría.
  filtrarCursosPorCategoria(idCategoria: number | null): void {
    if (idCategoria === null) {
      // No hacemos nada si idCategoria es null
      return;
    }

    if (idCategoria === 0) {
      // Si se selecciona la opción "Todas las categorías", mostrar todos los cursos
      this.cursosFiltrados = this.cursos;
      this.tituloCursos = 'Todos nuestros cursos';
      this.sinCursos = false;
    } else {
      // Filtrar los cursos por la categoría seleccionada
      this.cursosService.getCursosPorCategoria(idCategoria).subscribe(
        (cursos: Curso) => {
          this.cursosFiltrados = cursos;
          this.sinCursos = this.cursosFiltrados.length === 0;
          const categoriaSeleccionada = this.categorias.find((categoria: Categoria) => categoria.id_categoria === idCategoria);
          if (categoriaSeleccionada) {
            this.tituloCursos = `Nuestros cursos de ${categoriaSeleccionada.nombre}`;
          } else {
            this.tituloCursos = 'Nuestros cursos';
          }
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }

// Método que filtra los cursos por nombre.
filtrarCursosPorNombre(nombreCurso: string): void {
  if (nombreCurso.trim() === '') {
    // Si el campo de nombre del curso está vacío, mostramos todos los cursos
    this.cursosFiltrados = this.cursos;
    this.tituloCursos = 'Todos nuestros cursos';
    this.sinCursos = false;
  } else {
    // Filtramos los cursos por nombre
    this.cursosFiltrados = this.cursos.filter((curso: Curso) => curso.nombre_curso.toLowerCase().includes(nombreCurso.toLowerCase()));
    this.sinCursos = this.cursosFiltrados.length === 0;
    this.tituloCursos = `Resultados de la búsqueda por nombre: ${nombreCurso}`;
  }
}
  // Método que filtra los cursos por calificación mínima.
  filtrarCursosPorCalificacion(calificacionMinima: number): void {
    if (calificacionMinima !== null) {
      // Filtramos los cursos por calificación mínima
      this.cursosFiltrados = this.cursos.filter((curso: Curso) => curso.calificacion !== null && curso.calificacion >= calificacionMinima);
      this.sinCursos = this.cursosFiltrados.length === 0;
      this.tituloCursos = `Cursos con calificación mínima de ${calificacionMinima} estrellas`;
    }
  }

  // Método que filtra los cursos por precio máximo.
  filtrarCursosPorPrecio(precioMaximo: number): void {
    if (precioMaximo !== null) {
      // Filtramos los cursos por precio máximo
      this.cursosFiltrados = this.cursos.filter((curso: Curso) => curso.precio !== null && curso.precio <= precioMaximo);
      this.sinCursos = this.cursosFiltrados.length === 0;
      this.tituloCursos = `Cursos con precio máximo de $${precioMaximo}`;
    }
  }

  // Método para abrir la búsqueda avanzada.
  abrirBusquedaAvanzada(): void {
    // Aquí puedes implementar la lógica para abrir el modal de búsqueda avanzada
    console.log('Abriendo búsqueda avanzada...');
  }

  // Método para realizar la búsqueda avanzada.
  realizarBusqueda(): void {
    if (this.tipoBusqueda === 'categoria') {
      this.filtrarCursosPorCategoria(this.categoriaSeleccionada);
    } else if (this.tipoBusqueda === 'nombre') {
      this.filtrarCursosPorNombre(this.nombreCurso);
    } else if (this.tipoBusqueda === 'calificacion') {
      if (this.calificacionMinima !== null) {
        this.filtrarCursosPorCalificacion(this.calificacionMinima);
      }
    } else if (this.tipoBusqueda === 'precio') {
      if (this.precioMaximo !== null) {
        this.filtrarCursosPorPrecio(this.precioMaximo);
      }
    }
  }

  // Método para mostrar todos los cursos sin filtrar.
  mostrarTodosLosCursos(): void {
    this.cursosFiltrados = this.cursos;
    this.tituloCursos = 'Todos nuestros cursos';
  }
}