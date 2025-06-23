package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import datastructure.list.ListException;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private final FlightService flightService = FlightService.getInstance();
    private final AirportService airportService = AirportService.getInstance();
    private final PersonService personService = PersonService.getInstance();

    public void generateCustomReport(String filePath,
                                     boolean includeAirports,
                                     boolean includeRoutes,
                                     boolean includePassengers,
                                     boolean includeOccupancy) throws Exception {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Encabezado
        addHeader(document, "Reporte Estadístico del Sistema");

        // Título
        addTitle(document, "Datos Generados el: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // Secciones
        if (includeAirports) {
            generateTopAirports(document);
            document.newPage();
        }

        if (includeRoutes) {
            generateTopRoutes(document);
            document.newPage();
        }

        if (includePassengers) {
            generateTopPassengers(document);
            document.newPage();
        }

        if (includeOccupancy) {
            generateAverageOccupancy(document);
        }

        // Pie de página
        addFooter(document);

        document.close();
    }

    private void addHeader(Document document, String title) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph para = new Paragraph(title, font);
        para.setAlignment(Element.ALIGN_CENTER);
        para.setSpacingAfter(20);
        document.add(para);
    }

    private void addTitle(Document document, String subtitle) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph para = new Paragraph(subtitle, font);
        para.setAlignment(Element.ALIGN_CENTER);
        para.setSpacingAfter(30);
        document.add(para);
    }

    private void addFooter(Document document) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.LIGHT_GRAY);
        Paragraph footer = new Paragraph("© 2024 Sistema de Gestión de Aerolíneas", font);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
    }

    private void generateTopAirports(Document document) throws DocumentException {
        List<Flight> allFlights = flightService.getAllFlights();

        Map<String, Long> airportCounts = allFlights.stream()
                .collect(Collectors.groupingBy(Flight::getOrigin, Collectors.counting()));

        List<Map.Entry<String, Long>> topAirports = airportCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Encabezados
        addTableHeader(table, "Aeropuerto");
        addTableHeader(table, "Vuelos Salientes");

        // Datos
        for (Map.Entry<String, Long> entry : topAirports) {
            table.addCell(entry.getKey());
            table.addCell(entry.getValue().toString());
        }

        document.add(new Paragraph("a. Top 5 aeropuertos con más vuelos salientes",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(table);
    }

    private void generateTopRoutes(Document document) throws DocumentException {
        List<Flight> allFlights = flightService.getAllFlights();

        Map<String, Long> routeCounts = allFlights.stream()
                .map(f -> f.getOrigin() + " → " + f.getDestination())
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

        List<Map.Entry<String, Long>> topRoutes = routeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        addTableHeader(table, "Ruta");
        addTableHeader(table, "Número de Vuelos");

        for (Map.Entry<String, Long> entry : topRoutes) {
            table.addCell(entry.getKey());
            table.addCell(entry.getValue().toString());
        }

        document.add(new Paragraph("b. Rutas más utilizadas",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(table);
    }

    private void generateTopPassengers(Document document) throws DocumentException, ListException {
        List<Passenger> passengers = personService.getAllPassengersList();

        Map<String, Long> passengerFlights = passengers.stream()
                .collect(Collectors.toMap(
                        p -> p.getName() + " (" + p.getId() + ")",
                        p -> (long) p.getFlightHistory().size()
                ));

        List<Map.Entry<String, Long>> topPassengers = passengerFlights.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        addTableHeader(table, "Pasajero");
        addTableHeader(table, "Vuelos Realizados");

        for (Map.Entry<String, Long> entry : topPassengers) {
            table.addCell(entry.getKey());
            table.addCell(entry.getValue().toString());
        }

        document.add(new Paragraph("c. Pasajeros con más vuelos realizados",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(table);
    }

    private void generateAverageOccupancy(Document document) throws DocumentException {
        List<Flight> allFlights = flightService.getAllFlights();

        double totalOccupancyRate = 0;
        for (Flight flight : allFlights) {
            double occupancyRate = (double) flight.getOccupancy() / flight.getCapacity() * 100;
            totalOccupancyRate += occupancyRate;
        }
        double averageOccupancy = totalOccupancyRate / allFlights.size();

        Paragraph p = new Paragraph();
        p.add(new Paragraph("d. Porcentaje de ocupación promedio por vuelo",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        p.add(new Paragraph(String.format("El porcentaje de ocupación promedio es: %.2f%%", averageOccupancy)));
        p.setSpacingAfter(10);
        document.add(p);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(header,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        table.addCell(cell);
    }
}