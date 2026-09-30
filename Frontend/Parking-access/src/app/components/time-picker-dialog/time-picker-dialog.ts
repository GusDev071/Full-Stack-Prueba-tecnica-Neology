import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

export interface TimePickerData {
  label: string;
  value: string;
}

@Component({
  selector: 'app-time-picker-dialog',
  standalone: false,
  templateUrl: './time-picker-dialog.html',
  styleUrl: './time-picker-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimePickerDialog {
  private readonly data = inject<TimePickerData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<TimePickerDialog, string>);
  protected readonly hours = Array.from({ length: 24 }, (_, index) => String(index).padStart(2, '0'));
  protected readonly minutes = Array.from({ length: 60 }, (_, index) => String(index).padStart(2, '0'));
  protected readonly hour = signal(this.data.value.split(':')[0] || '00');
  protected readonly minute = signal(this.data.value.split(':')[1] || '00');
  protected readonly label = this.data.label;

  protected confirm(): void {
    this.dialogRef.close(`${this.hour()}:${this.minute()}`);
  }
}