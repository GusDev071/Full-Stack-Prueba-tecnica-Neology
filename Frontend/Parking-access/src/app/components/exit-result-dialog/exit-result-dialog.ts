import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { SalidaResponse, TipoVehiculo } from '../../models/parking.models';
import { ParkingTicketDialog } from '../parking-ticket-dialog/parking-ticket-dialog';

@Component({
  selector: 'app-exit-result-dialog',
  standalone: false,
  templateUrl: './exit-result-dialog.html',
  styleUrl: './exit-result-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExitResultDialog {
  protected readonly data = inject<SalidaResponse>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<ExitResultDialog>);
  private readonly dialog = inject(MatDialog);
  protected readonly vehicleTypeLabels: Record<TipoVehiculo, string> = {
    OFICIAL: 'Oficial',
    RESIDENTE: 'Residente',
    NO_RESIDENTE: 'No residente',
  };

  protected close(): void {
    this.dialogRef.close();
  }

  protected generateTicket(): void {
    this.dialog.open(ParkingTicketDialog, {
      width: '420px',
      maxWidth: 'calc(100vw - 24px)',
      panelClass: 'parking-ticket-dialog',
      data: this.data,
      ariaLabel: `Ticket de estacionamiento ${this.data.placa}`,
    });
  }
}