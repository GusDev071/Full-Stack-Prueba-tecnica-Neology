import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { TipoVehiculo, VehiculoResponse } from '../../models/parking.models';
import { ParkingApiService } from '../../services/parking-api.service';
import { ParkingPdfService } from '../../services/parking-pdf.service';

@Component({
  selector: 'app-vehicles',
  standalone: false,
  templateUrl: './vehicles.html',
  styleUrl: './vehicles.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Vehicles implements OnInit {
  private readonly api = inject(ParkingApiService);
  private readonly pdf = inject(ParkingPdfService);
  protected readonly vehicles = signal<VehiculoResponse[]>([]);
  protected readonly query = signal('');
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly filteredVehicles = computed(() => {
    const term = this.query().trim().toUpperCase();
    return this.vehicles().filter((vehicle) => !term || vehicle.placa.toUpperCase().includes(term));
  });
  protected readonly typeLabels: Record<TipoVehiculo, string> = {
    OFICIAL: 'Oficial', RESIDENTE: 'Residente', NO_RESIDENTE: 'No residente',
  };

  ngOnInit(): void { this.loadVehicles(); }

  protected loadVehicles(): void {
    this.loading.set(true);
    this.api.getVehicles().subscribe({
      next: (vehicles) => { this.vehicles.set(vehicles); this.loading.set(false); this.error.set(''); },
      error: () => { this.loading.set(false); this.error.set('No fue posible cargar el catálogo. Revisa la conexión con el backend.'); },
    });
  }

  protected updateQuery(value: string): void { this.query.set(value); }

  protected downloadPdf(): void { this.pdf.downloadVehicles(this.filteredVehicles()); }
}