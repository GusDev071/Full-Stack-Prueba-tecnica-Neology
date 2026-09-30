import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SalidaResponse } from '../../models/parking.models';

@Component({
  selector: 'app-parking-ticket-dialog',
  standalone: false,
  templateUrl: './parking-ticket-dialog.html',
  styleUrl: './parking-ticket-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ParkingTicketDialog {
  protected readonly data = inject<SalidaResponse>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<ParkingTicketDialog>);
  protected readonly printedAt = new Date();

  protected durationLabel(): string {
    const hours = Math.floor(this.data.minutos / 60);
    const minutes = this.data.minutos % 60;
    if (hours === 0) return `${minutes} min`;
    return minutes === 0 ? `${hours} h` : `${hours} h ${minutes} min`;
  }

  protected printTicket(): void {
    window.print();
  }

  protected close(): void {
    this.dialogRef.close();
  }
}