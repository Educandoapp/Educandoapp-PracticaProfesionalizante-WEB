import { InicioComponent } from './pages/inicio/inicio.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { NosotrosComponent } from './pages/nosotros/nosotros.component';
import { RegistroComponent } from './pages/registro/registro.component';
import { ContactoComponent } from './pages/contacto/contacto.component';
import { AdministradoresComponent } from './pages/administradores/administradores.component';
import { CursosComponent } from './ecommerce/cursos/cursos.component';
import { CheckoutComponent } from './ecommerce/checkout/checkout.component';
import { DetailsComponent } from './ecommerce/cursos-container/details/details.component';
import { ForoComponent } from './pages/foro/foro.component';
import { TareasComponent } from './pages/tareas/tareas.component';
import {AreaPersonalComponent} from './layouts/area-personal/area-personal.component';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { MisCursosComponent } from './admin/mis-cursos/mis-cursos.component';

const routes: Routes = [
  {path: '*', redirectTo: '', pathMatch: 'full'},
    { path: '', component: InicioComponent },
    { path: 'contacto', component: ContactoComponent },
    { path: 'login', component: LoginComponent },
    { path: 'nosotros', component: NosotrosComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'administradores', component: AdministradoresComponent },
    { path: 'cursos', component: CursosComponent, },
    { path: 'cursos/checkout', component: CheckoutComponent },
    { path: 'cursos/details/:idCategoria/:idCurso', component: DetailsComponent },
    { path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) },
    { path: 'foro', component: ForoComponent }, // Agrega la ruta para el componente de foro
    { path: 'area-personal', component: AreaPersonalComponent },
    {path: 'tareas', component: TareasComponent},
    { path: 'perfil', component: PerfilComponent },
];

@NgModule({
    imports: [
      RouterModule.forRoot(routes)
    ],
    exports: [
      RouterModule
    ]
  })
  export class AppRoutingModule { }
