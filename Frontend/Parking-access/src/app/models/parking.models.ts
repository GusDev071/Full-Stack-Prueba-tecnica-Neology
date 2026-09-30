export type TipoVehiculo = 'OFICIAL' | 'RESIDENTE' | 'NO_RESIDENTE';

export interface VehiculoResponse {
  placa: string;
  tipo: TipoVehiculo;
  fechaAlta: string;
  tiempoAcumulado?: number | null;
  estanciaActiva: boolean;
}

export interface EstanciaResponse {
  id: number;
  placa: string;
  fechaHoraEntrada: string;
  fechaHoraSalida: string | null;
  minutos: number | null;
  importe: number | null;
  activa: boolean;
}

export interface VehiculoDetalleResponse extends VehiculoResponse {
  totalEstancias: number;
  estancias: EstanciaResponse[];
}

export interface EntradaResponse {
  idEstancia: number;
  placa: string;
  tipoVehiculo: TipoVehiculo;
  fechaHoraEntrada: string;
  mensaje: string;
}

export interface SalidaResponse {
  idEstancia: number;
  placa: string;
  tipoVehiculo: TipoVehiculo;
  fechaHoraEntrada: string;
  fechaHoraSalida: string;
  minutos: number;
  tarifaPorMinuto: number;
  importeCobrado: number;
  tiempoAcumuladoTotal: number | null;
  mensaje: string;
}

export interface PagoResidenteResponse {
  placa: string;
  tiempoAcumuladoMinutos: number;
  tarifaPorMinuto: number;
  totalPagar: number;
}

export interface InformePagosResponse {
  fechaGeneracion: string;
  totalResidentes: number;
  totalMinutosAcumulados: number;
  totalCobro: number;
  residentes: PagoResidenteResponse[];
}

export interface ResumenMesResponse {
  mensaje: string;
  residentesReiniciados: number;
  estanciasFinalizadasEliminadas: number;
  fechaReinicio: string;
}

export interface VehiculoRequest {
  placa: string;
}

export interface EstanciaRequest extends VehiculoRequest {
  fechaHora?: string;
}