package com.tics.tics.inventario.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.tics.tics.inventario.dto.EquipoVistaDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteService {

    public void exportarPdf(List<EquipoVistaDTO> equipos, HttpServletResponse response) throws IOException {
        // Documento en formato A4 horizontal (apaisado) para que quepan las columnas
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph titulo = new Paragraph("Reporte de Equipos de Inventario", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla con 6 columnas según tus requerimientos
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 1.5f, 2.0f, 1.5f, 1.5f, 3.0f});

        escribirCabeceraTablaPdf(table);
        escribirDatosTablaPdf(equipos, table);

        document.add(table);
        document.close();
    }

    private void escribirCabeceraTablaPdf(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(72, 106, 230)); // Azul ControlTech
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        String[] cabeceras = {"No. Inventario", "Serie", "Artículo", "Marca", "Modelo", "Características"};
        for (String cabecera : cabeceras) {
            cell.setPhrase(new Phrase(cabecera, font));
            table.addCell(cell);
        }
    }

    private void escribirDatosTablaPdf(List<EquipoVistaDTO> equipos, PdfPTable table) {
        Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        for (EquipoVistaDTO equipo : equipos) {
            table.addCell(new Phrase(equipo.getInventario(), fontDatos));
            table.addCell(new Phrase(equipo.getSerie() != null ? equipo.getSerie() : "N/A", fontDatos));
            table.addCell(new Phrase(equipo.getNombreArticulo(), fontDatos));
            table.addCell(new Phrase(equipo.getMarca(), fontDatos));
            table.addCell(new Phrase(equipo.getModelo(), fontDatos));
            table.addCell(new Phrase(equipo.getCaracteristicas() != null ? equipo.getCaracteristicas() : "", fontDatos));
        }
    }

    public void exportarCsv(List<EquipoVistaDTO> equipos, HttpServletResponse response) throws IOException {
        // OpenCSV maneja automáticamente la coma que pueda haber en 'caracteristicas' o 'fechas'
        CSVWriter writer = new CSVWriter(response.getWriter());

        // Encabezados exactos solicitados
        String[] cabeceras = {"No. Inventario", "Serie", "Articulo", "Marca", "Modelo", "Cantidad", "Caracteristicas", "Fecha Registro", "Fecha Actualizacion"};
        writer.writeNext(cabeceras);

        for (EquipoVistaDTO eq : equipos) {
            String[] datos = {
                    eq.getInventario(),
                    eq.getSerie() != null ? eq.getSerie() : "",
                    eq.getNombreArticulo(),
                    eq.getMarca(),
                    eq.getModelo(),
                    String.valueOf(eq.getCantidad()),
                    eq.getCaracteristicas() != null ? eq.getCaracteristicas() : "",
                    eq.getFechaRegistro() != null ? eq.getFechaRegistro().toString() : "",
                    eq.getFechaActualizacion() != null ? eq.getFechaActualizacion().toString() : ""
            };
            writer.writeNext(datos);
        }
        writer.close();
    }
}