import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TimePickerDialog } from './time-picker-dialog';

@NgModule({
  declarations: [TimePickerDialog],
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatSelectModule],
  exports: [TimePickerDialog],
})
export class TimePickerDialogModule {}