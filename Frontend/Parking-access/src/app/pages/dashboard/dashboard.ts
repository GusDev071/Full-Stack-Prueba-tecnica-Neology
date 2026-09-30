import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { EstanciaResponse, InformePagosResponse } from '../../models/parking.models';
import { ParkingApiService } from '../../services/parking-api.service';
import { ParkingPdfService } from '../../services/parking-pdf.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard implements OnInit {
  private readonly api = inject(ParkingApiService);
  private readonly pdf = inject(ParkingPdfService);
  protected readonly activeStays = signal<EstanciaResponse[]>([]);
  protected readonly report = signal<InformePagosResponse | null>(null);
  protected readonly vehicleCount = signal(0);
  protected readonly loading = signal(true);
  protected readonly loadError = signal(false);
  protected readonly today = new Date();

  ngOnInit(): void {
    forkJoin({
      vehicles: this.api.getVehicles().pipe(catchError(() => { this.loadError.set(true); return of([]); })),
      stays: this.api.getActiveStays().pipe(catchError(() => { this.loadError.set(true); return of([]); })),
      report: this.api.getResidentPayments().pipe(catchError(() => { this.loadError.set(true); return of(null); })),
    }).subscribe(({ vehicles, stays, report }) => {
      this.vehicleCount.set(vehicles.length);
      this.activeStays.set(stays);
      this.report.set(report);
      this.loading.set(false);
    });
  }

  protected downloadPdf(): void {
    this.pdf.downloadDashboard({
      vehicleCount: this.vehicleCount(),
      activeStays: this.activeStays(),
      report: this.report(),
    });
  }
}