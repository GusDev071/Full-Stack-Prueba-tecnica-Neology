import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  EntradaResponse,
  EstanciaRequest,
  EstanciaResponse,
  InformePagosResponse,
  ResumenMesResponse,
  SalidaResponse,
  TipoVehiculo,
  VehiculoDetalleResponse,
  VehiculoRequest,
  VehiculoResponse,
} from '../models/parking.models';

@Injectable({ providedIn: 'root' })
export class ParkingApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/neo';

  getVehicles(): Observable<VehiculoResponse[]> {
    return this.http.get<VehiculoResponse[]>(`${this.baseUrl}/vehiculos`);
  }

  getVehicle(placa: string): Observable<VehiculoDetalleResponse> {
    return this.http.get<VehiculoDetalleResponse>(`${this.baseUrl}/vehiculos/${encodeURIComponent(placa)}`);
  }

  getStays(): Observable<EstanciaResponse[]> {
    return this.http.get<EstanciaResponse[]>(`${this.baseUrl}/estancias`);
  }

  getActiveStays(): Observable<EstanciaResponse[]> {
    return this.http.get<EstanciaResponse[]>(`${this.baseUrl}/estancias/activas`);
  }

  getResidentPayments(): Observable<InformePagosResponse> {
    return this.http.get<InformePagosResponse>(`${this.baseUrl}/residentes/pagos`);
  }

  createVehicle(tipo: TipoVehiculo, request: VehiculoRequest): Observable<VehiculoResponse> {
    const endpoint: Record<TipoVehiculo, string> = {
      OFICIAL: 'oficiales',
      RESIDENTE: 'residentes',
      NO_RESIDENTE: 'no-residentes',
    };
    return this.http.post<VehiculoResponse>(`${this.baseUrl}/vehiculos/${endpoint[tipo]}`, request);
  }

  registerEntry(request: EstanciaRequest): Observable<EntradaResponse> {
    return this.http.post<EntradaResponse>(`${this.baseUrl}/estancias/entrada`, request);
  }

  registerExit(request: EstanciaRequest): Observable<SalidaResponse> {
    return this.http.post<SalidaResponse>(`${this.baseUrl}/estancias/salida`, request);
  }

  startMonth(): Observable<ResumenMesResponse> {
    return this.http.post<ResumenMesResponse>(`${this.baseUrl}/mes/iniciar`, {});
  }
}