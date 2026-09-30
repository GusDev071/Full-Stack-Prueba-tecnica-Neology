import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ParkingTicketDialog } from './parking-ticket-dialog';

@NgModule({
  declarations: [ParkingTicketDialog],
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatIconModule],
  exports: [ParkingTicketDialog],
})
export class ParkingTicketDialogModule {}