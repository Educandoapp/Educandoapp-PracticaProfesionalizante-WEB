import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { MisCursosComponent } from './mis-cursos/mis-cursos.component';
import { AuthGuard } from '../guard/guard.guard';

const routes: Routes = [
  {
    path: 'admin',
    canActivate: [AuthGuard],
    children: [
      { path: 'mis-cursos', component: MisCursosComponent },
      { path: 'home', component: AdminDashboardComponent },
    ]
  }
];

console.log('Rutas del módulo de administrador:', routes);

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
