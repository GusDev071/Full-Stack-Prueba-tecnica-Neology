import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { InformePagosResponse } from '../../models/parking.models';
import { ParkingApiService } from '../../services/parking-api.service';
import { ParkingPdfService } from '../../services/parking-pdf.service';

@Component({
  selector: 'app-reports',
  standalone: false,
  templateUrl: './reports.html',
  styleUrl: './reports.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Reports implements OnInit {
  private readonly api = inject(ParkingApiService);
  private readonly pdf = inject(ParkingPdfService);
  protected readonly report = signal<InformePagosResponse | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  ngOnInit(): void { this.loadReport(); }

  protected loadReport(): void {
    this.loading.set(true);
    this.api.getResidentPayments().subscribe({
      next: (report) => { this.report.set(report); this.error.set(''); this.loading.set(false); },
      error: () => { this.error.set('No fue posible generar el informe. Verifica que el backend esté activo.'); this.loading.set(false); },
    });
  }

  protected downloadPdf(): void {
    const report = this.report();
    if (report) this.pdf.downloadResidentPayments(report);
  }
}