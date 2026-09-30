import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { Dashboard } from './dashboard';

@NgModule({
  declarations: [Dashboard],
  imports: [CommonModule, MatButtonModule, MatIconModule, RouterModule.forChild([{ path: '', component: Dashboard }])],
})
export class DashboardModule {}