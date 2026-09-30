import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { VehicleDetail } from '../vehicle-detail/vehicle-detail';
import { Vehicles } from './vehicles';

@NgModule({
  declarations: [Vehicles, VehicleDetail],
  imports: [
    CommonModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    RouterModule.forChild([
      { path: '', component: Vehicles },
      { path: ':placa', component: VehicleDetail },
    ]),
  ],
})
export class VehiclesModule {}