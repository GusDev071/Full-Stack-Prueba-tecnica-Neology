import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { VehiculoDetalleResponse } from '../../models/parking.models';
import { ParkingApiService } from '../../services/parking-api.service';
import { ParkingPdfService } from '../../services/parking-pdf.service';

@Component({
  selector: 'app-vehicle-detail',
  standalone: false,
  templateUrl: './vehicle-detail.html',
  styleUrl: './vehicle-detail.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VehicleDetail implements OnInit {
  private readonly api = inject(ParkingApiService);
  private readonly pdf = inject(ParkingPdfService);
  private readonly route = inject(ActivatedRoute);
  private readonly location = inject(Location);
  protected readonly vehicle = signal<VehiculoDetalleResponse | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  ngOnInit(): void {
    const plate = this.route.snapshot.paramMap.get('placa');
    if (!plate) { this.loading.set(false); this.error.set('No se indicó una placa.'); return; }
    this.api.getVehicle(plate).subscribe({
      next: (vehicle) => { this.vehicle.set(vehicle); this.loading.set(false); },
      error: (response: { error?: { mensaje?: string } }) => {
        this.error.set(response.error?.mensaje ?? 'No se encontró el vehículo o no fue posible consultar el backend.');
        this.loading.set(false);
      },
    });
  }

  protected goBack(): void { this.location.back(); }

  protected downloadPdf(): void {
    const vehicle = this.vehicle();
    if (vehicle) this.pdf.downloadVehicleDetail(vehicle);
  }
}