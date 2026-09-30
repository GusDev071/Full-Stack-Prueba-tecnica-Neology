import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import {
  EstanciaResponse,
  InformePagosResponse,
  VehiculoDetalleResponse,
  VehiculoResponse,
} from '../models/parking.models';

export interface ResumenPdfData {
  vehicleCount: number;
  activeStays: EstanciaResponse[];
  report: InformePagosResponse | null;
}

@Injectable({ providedIn: 'root' })
export class ParkingPdfService {
  private readonly vehicleTypeLabels = {
    OFICIAL: 'Oficial',
    RESIDENTE: 'Residente',
    NO_RESIDENTE: 'No residente',
  };

  downloadDashboard(data: ResumenPdfData): void {
    const doc = this.createDocument('Resumen operativo');
    this.addMetricLine(doc, 54, 'Vehículos registrados', String(data.vehicleCount));
    this.addMetricLine(doc, 63, 'Vehículos dentro', String(data.activeStays.length));
    this.addMetricLine(doc, 72, 'Residentes del mes', data.report ? String(data.report.totalResidentes) : 'No disponible');
    this.addMetricLine(doc, 81, 'Cobro acumulado', data.report ? this.money(data.report.totalCobro) : 'No disponible');

    autoTable(doc, {
      startY: 94,
      head: [['Placa', 'Hora de entrada', 'Estado']],
      body: data.activeStays.length
        ? data.activeStays.map((stay) => [stay.placa, this.dateTime(stay.fechaHoraEntrada), 'Dentro'])
        : [['Sin estancias activas', '', '']],
      ...this.tableStyle(),
    });

    this.save(doc, 'resumen-operativo');
  }

  downloadVehicles(vehicles: VehiculoResponse[]): void {
    const doc = this.createDocument('Catálogo de vehículos', `${vehicles.length} registros incluidos`);
    autoTable(doc, {
      startY: 54,
      head: [['Placa', 'Tipo', 'Fecha de alta', 'Tiempo acumulado', 'Estado']],
      body: vehicles.map((vehicle) => [
        vehicle.placa,
        this.vehicleTypeLabels[vehicle.tipo],
        this.dateTime(vehicle.fechaAlta),
        vehicle.tiempoAcumulado == null ? 'No aplica' : `${vehicle.tiempoAcumulado} min`,
        vehicle.estanciaActiva ? 'Dentro' : 'Fuera',
      ]),
      ...this.tableStyle(),
    });
    this.save(doc, 'vehiculos');
  }

  downloadVehicleDetail(vehicle: VehiculoDetalleResponse): void {
    const doc = this.createDocument(`Ficha de vehículo ${vehicle.placa}`);
    this.addMetricLine(doc, 54, 'Tipo de vehículo', this.vehicleTypeLabels[vehicle.tipo]);
    this.addMetricLine(doc, 63, 'Fecha de alta', this.dateTime(vehicle.fechaAlta));
    this.addMetricLine(doc, 72, 'Tiempo acumulado', vehicle.tiempoAcumulado == null ? 'No aplica' : `${vehicle.tiempoAcumulado} min`);
    this.addMetricLine(doc, 81, 'Estancias registradas', String(vehicle.totalEstancias));

    autoTable(doc, {
      startY: 94,
      head: [['Entrada', 'Salida', 'Duración', 'Importe', 'Estado']],
      body: vehicle.estancias.length
        ? vehicle.estancias.map((stay) => [
            this.dateTime(stay.fechaHoraEntrada),
            stay.fechaHoraSalida ? this.dateTime(stay.fechaHoraSalida) : 'Pendiente',
            stay.minutos == null ? '—' : `${stay.minutos} min`,
            stay.importe == null ? '—' : this.money(stay.importe),
            stay.activa ? 'Activa' : 'Finalizada',
          ])
        : [['Sin estancias registradas', '', '', '', '']],
      ...this.tableStyle(),
    });
    this.save(doc, `vehiculo-${this.filePart(vehicle.placa)}`);
  }

  downloadResidentPayments(report: InformePagosResponse): void {
    const doc = this.createDocument('Pagos de residentes', `Generado ${this.dateTime(report.fechaGeneracion)}`);
    this.addMetricLine(doc, 54, 'Residentes', String(report.totalResidentes));
    this.addMetricLine(doc, 63, 'Minutos acumulados', `${report.totalMinutosAcumulados.toLocaleString('es-MX')} min`);
    this.addMetricLine(doc, 72, 'Total del corte', this.money(report.totalCobro));

    autoTable(doc, {
      startY: 84,
      head: [['Placa', 'Tiempo acumulado', 'Tarifa por minuto', 'Total a pagar']],
      body: report.residentes.map((resident) => [
        resident.placa,
        `${resident.tiempoAcumuladoMinutos.toLocaleString('es-MX')} min`,
        `${this.money(resident.tarifaPorMinuto)} / min`,
        this.money(resident.totalPagar),
      ]),
      ...this.tableStyle(),
    });
    this.save(doc, 'pagos-residentes');
  }

  private createDocument(title: string, subtitle = `Generado ${this.dateTime(new Date().toISOString())}`): jsPDF {
    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    doc.setFillColor(21, 42, 39);
    doc.rect(0, 0, 297, 39, 'F');
    doc.setFillColor(198, 226, 102);
    doc.rect(0, 0, 4, 39, 'F');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.setTextColor(198, 226, 102);
    doc.text('NEOLOGY  /  PARKING ACCESS', 15, 12);
    doc.setFontSize(19);
    doc.setTextColor(255, 255, 255);
    doc.text(title, 15, 25);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(218, 228, 222);
    doc.text(subtitle, 15, 33);
    return doc;
  }

  private addMetricLine(doc: jsPDF, y: number, label: string, value: string): void {
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.setTextColor(93, 111, 101);
    doc.text(`${label}:`, 15, y);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(31, 52, 44);
    doc.text(value, 66, y);
  }

  private tableStyle() {
    return {
      margin: { left: 15, right: 15 },
      styles: { font: 'helvetica' as const, fontSize: 8, cellPadding: 3, textColor: [46, 62, 54] as [number, number, number] },
      headStyles: { fillColor: [25, 58, 51] as [number, number, number], textColor: [255, 255, 255] as [number, number, number], fontStyle: 'bold' as const },
      alternateRowStyles: { fillColor: [244, 247, 243] as [number, number, number] },
      theme: 'grid' as const,
    };
  }

  private dateTime(value: string): string {
    return new Date(value).toLocaleString('es-MX', { dateStyle: 'short', timeStyle: 'short' });
  }

  private money(value: number): string {
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value).replace(/\u00a0/g, ' ');
  }

  private filePart(value: string): string {
    return value.toLowerCase().replace(/[^a-z0-9-]/g, '-');
  }

  private save(doc: jsPDF, name: string): void {
    const date = new Date().toISOString().slice(0, 10);
    doc.save(`${name}-${date}.pdf`);
  }
}