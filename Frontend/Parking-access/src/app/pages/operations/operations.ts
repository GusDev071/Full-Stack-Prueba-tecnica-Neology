import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroupDirective, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SalidaResponse, TipoVehiculo } from '../../models/parking.models';
import { ParkingApiService } from '../../services/parking-api.service';
import { ExitResultDialog } from '../../components/exit-result-dialog/exit-result-dialog';
import { TimePickerDialog } from '../../components/time-picker-dialog/time-picker-dialog';

const platePattern = /^[A-Z0-9-]{3,12}$/i;

@Component({
  selector: 'app-operations',
  standalone: false,
  templateUrl: './operations.html',
  styleUrl: './operations.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Operations {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ParkingApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  protected readonly selectedTab = signal(this.route.snapshot.queryParamMap.get('tab') === 'alta' ? 2 : 0);
  protected readonly busy = signal(false);
  protected readonly confirmedMonth = signal(false);

  protected readonly entryForm = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(platePattern)]],
    fecha: this.fb.control<Date | null>(null, Validators.required),
    hora: ['', Validators.required],
  });
  protected readonly exitForm = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(platePattern)]],
    fecha: this.fb.control<Date | null>(null, Validators.required),
    hora: ['', Validators.required],
  });
  protected readonly vehicleForm = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(platePattern)]],
    tipo: this.fb.control<TipoVehiculo>('RESIDENTE', Validators.required),
  });

  protected registerEntry(formDirective: FormGroupDirective): void {
    if (this.entryForm.invalid) { this.entryForm.markAllAsTouched(); return; }
    const { placa, fecha, hora } = this.entryForm.getRawValue();
    if (!fecha) return;
    this.run(this.api.registerEntry({ placa: placa.trim().toUpperCase(), fechaHora: this.formatDateTime(fecha, hora) }), (result) => {
      formDirective.resetForm();
      return result.mensaje;
    });
  }

  protected registerExit(formDirective: FormGroupDirective): void {
    if (this.exitForm.invalid) { this.exitForm.markAllAsTouched(); return; }
    const { placa, fecha, hora } = this.exitForm.getRawValue();
    if (!fecha) return;
    this.run(this.api.registerExit({ placa: placa.trim().toUpperCase(), fechaHora: this.formatDateTime(fecha, hora) }), (result) => {
      return result.mensaje;
    }, (result) => this.showExitResult(result, formDirective));
  }

  protected createVehicle(formDirective: FormGroupDirective): void {
    if (this.vehicleForm.invalid) { this.vehicleForm.markAllAsTouched(); return; }
    const { placa, tipo } = this.vehicleForm.getRawValue();
    if (!tipo) return;
    this.run(this.api.createVehicle(tipo, { placa: placa.trim().toUpperCase() }), (vehicle) => {
      formDirective.resetForm({ placa: '', tipo: 'RESIDENTE' });
      return `Vehículo ${vehicle.placa} agregado al catálogo.`;
    });
  }

  protected startMonth(): void {
    if (!this.confirmedMonth()) return;
    this.run(this.api.startMonth(), (result) => {
      this.confirmedMonth.set(false);
      return result.mensaje;
    });
  }

  protected openTimePicker(kind: 'entry' | 'exit'): void {
    const form = kind === 'entry' ? this.entryForm : this.exitForm;
    const label = kind === 'entry' ? 'Hora de entrada' : 'Hora de salida';
    this.dialog.open(TimePickerDialog, {
      width: '380px',
      maxWidth: 'calc(100vw - 32px)',
      data: { label, value: form.controls.hora.value },
    }).afterClosed().subscribe((value: string | undefined) => {
      if (value) form.controls.hora.setValue(value);
    });
  }

  private formatDateTime(date: Date, time: string): string {
    const [hours, minutes] = time.split(':').map(Number);
    const localDate = new Date(date);
    localDate.setHours(hours, minutes, 0, 0);
    const year = localDate.getFullYear();
    const month = String(localDate.getMonth() + 1).padStart(2, '0');
    const day = String(localDate.getDate()).padStart(2, '0');
    const hour = String(localDate.getHours()).padStart(2, '0');
    const minute = String(localDate.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hour}:${minute}:00`;
  }

  private run<T>(
    request: import('rxjs').Observable<T>,
    successMessage: (response: T) => string,
    onSuccess?: (response: T) => void,
  ): void {
    this.busy.set(true);
    request.subscribe({
      next: (response) => {
        if (onSuccess) onSuccess(response);
        else {
          this.snackBar.open(successMessage(response), 'Cerrar', {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
            panelClass: ['parking-toast', 'parking-toast-success'],
          });
        }
        this.busy.set(false);
      },
      error: (error: unknown) => {
        const message = error instanceof HttpErrorResponse
          ? ((error.error as { mensaje?: string } | null)?.mensaje ?? 'No se pudo completar la operación. Revisa la conexión con el backend.')
          : 'No se pudo completar la operación.';
        this.snackBar.open(message, 'Cerrar', {
          duration: 6000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['parking-toast', 'parking-toast-error'],
        });
        this.busy.set(false);
      },
    });
  }

  private showExitResult(result: SalidaResponse, formDirective: FormGroupDirective): void {
    this.dialog.open(ExitResultDialog, {
      width: '560px',
      maxWidth: 'calc(100vw - 24px)',
      data: result,
      ariaLabel: 'Resultado de la salida registrada',
    }).afterClosed().subscribe(() => formDirective.resetForm());
  }
}