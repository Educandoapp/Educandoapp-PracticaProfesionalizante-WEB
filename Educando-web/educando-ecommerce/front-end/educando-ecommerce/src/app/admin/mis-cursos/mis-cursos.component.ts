import { Component } from '@angular/core';
import { Usuario } from 'src/app/interfaces/usuario.interface';
import { AuthService } from 'src/app/services/auth.service';
import { CursosService } from 'src/app/services/cursos.service';

@Component({
  selector: 'app-mis-cursos',
  templateUrl: './mis-cursos.component.html',
  styleUrls: ['./mis-cursos.component.css']
})
export class MisCursosComponent {
  userName: string = 'Usuario';
  userSurname: string = 'Prueba';
  userRole: number = 3;
  showProfileMenu: boolean = false;
  purchasedCoursesCount: number = 0;
  completedCoursesCount: number = 0;
  certificationsCount: number = 0;
  lastCourseTitle: string = 'Curso de Angular Avanzado';
  lastCourseDescription: string = 'Aprende a construir aplicaciones avanzadas con Angular';
  lastCourseImage: string | any;
  courses: any[] = [];
  currentUser: Usuario | null = null;

  constructor(
    private autenticacionService: AuthService,
    private cursosService: CursosService
  ) {}

  ngOnInit() {
    this.autenticacionService.getCambioEstadoAutenticacion().subscribe(autenticado => {
      if (autenticado) {
        // console.log(this.currentUser)
        this.currentUser = this.autenticacionService.getCurrentUser();
        this.userName = this.currentUser?.nombre || '';
        this.userSurname = this.currentUser?.apellido || '';
        this.userRole = this.currentUser?.id_rol_id || 1;
      } else {
        // console.log(this.currentUser)
        this.currentUser = null;
        this.userName = 'John Doe';
        this.userRole = 1;
      }
    });

    this.currentUser = this.autenticacionService.getCurrentUser();
    this.userName = this.currentUser?.nombre || '';
    this.userSurname = this.currentUser?.apellido || '';
    this.userRole = this.currentUser?.id_rol_id || 1;
    this.obtenerCursosUsuario();
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  obtenerCursosUsuario() {
    this.autenticacionService.obtenerCursosUsuario().subscribe(
      (response) => {
        // Actualiza los datos de los cursos del usuario
        this.courses = response;
        // console.log('Cursos obtenidos:', this.courses);
        this.purchasedCoursesCount = this.courses.length;
        this.completedCoursesCount = this.courses.filter((course) => course.progress === 100).length;
        this.certificationsCount = this.completedCoursesCount;
        if (this.courses.length > 0) {
          const lastCourse = this.courses[this.courses.length - 1];
          // console.log('Último curso obtenido:', lastCourse);
          this.lastCourseTitle = lastCourse.nombre_curso;
          this.lastCourseDescription = lastCourse.descripcion_curso;
          this.cursosService.obtenerCursoPorId(lastCourse.id_curso).subscribe(
            (curso) => {
              this.lastCourseImage = curso.imagen_url;
              // console.log('URL de la imagen:', this.lastCourseImage);
            },
            (error) => {
              console.error('Error al obtener la imagen del último curso:', error);
            }
          );
        }
      },
      (error) => {
        console.error('Error al obtener los cursos del usuario:', error);
      }
    );
  }
}
