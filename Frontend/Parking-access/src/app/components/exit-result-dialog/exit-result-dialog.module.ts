import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ExitResultDialog } from './exit-result-dialog';
import { ParkingTicketDialogModule } from '../parking-ticket-dialog/parking-ticket-dialog.module';

@NgModule({
  declarations: [ExitResultDialog],
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatIconModule, ParkingTicketDialogModule],
  exports: [ExitResultDialog],
})
export class ExitResultDialogModule {}