import { Routes } from '@angular/router';
export const routes: Routes = [
  { path: '', loadChildren: () => import('./pages/dashboard/dashboard.module').then((module) => module.DashboardModule) },
  { path: 'vehiculos', loadChildren: () => import('./pages/vehicles/vehicles.module').then((module) => module.VehiclesModule) },
  { path: 'operaciones', loadChildren: () => import('./pages/operations/operations.module').then((module) => module.OperationsModule) },
  { path: 'reportes', loadChildren: () => import('./pages/reports/reports.module').then((module) => module.ReportsModule) },
  { path: '**', redirectTo: '' },
];