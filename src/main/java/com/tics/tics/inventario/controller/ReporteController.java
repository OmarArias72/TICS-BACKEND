package com.tics.tics.inventario.controller;

import com.tics.tics.inventario.dto.EquipoVistaDTO;
import com.tics.tics.inventario.service.InventoryService;
import com.tics.tics.inventario.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reportes/equipos")
@RequiredArgsConstructor
public class ReporteController {

    private final InventoryService inventoryService;
    private final ReporteService reporteService;

    @GetMapping("/pdf")
    public void descargarReportePdf(
            HttpServletResponse response,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String articulo) throws IOException {

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reporte_equipos_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<EquipoVistaDTO> equipos = inventoryService.filtrarParaReporte(fechaInicio, fechaFin, articulo);
        reporteService.exportarPdf(equipos, response);
    }

    @GetMapping("/csv")
    public void descargarReporteCsv(
            HttpServletResponse response,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String articulo) throws IOException {

        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reporte_equipos_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        List<EquipoVistaDTO> equipos = inventoryService.filtrarParaReporte(fechaInicio, fechaFin, articulo);
        reporteService.exportarCsv(equipos, response);
    }
}