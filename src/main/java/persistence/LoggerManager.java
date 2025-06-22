package persistence;

import domain.EventLog;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoggerManager {
    private static final String LOG_FILE = "data/eventlog.txt";
    private static List<EventLog> logEvents = new ArrayList<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra un evento en el log
     * @param eventLog Evento a registrar
     */
    public static void logEvent(EventLog eventLog) {
        logEvents.add(eventLog);
        writeToFile(eventLog);
    }

    /**
     * Escribe un evento en el archivo de log
     * @param eventLog Evento a escribir
     */
    private static void writeToFile(EventLog eventLog) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(formatEvent(eventLog) + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de log: " + e.getMessage());
        }
    }

    /**
     * Formatea un evento para su escritura en el archivo
     * @param eventLog Evento a formatear
     * @return Cadena formateada
     */
    private static String formatEvent(EventLog eventLog) {
        return "[" + eventLog.getDateTime().format(formatter) + "] " +
                "Usuario: " + eventLog.getUser() + " - " +
                eventLog.getDescription();
    }

    /**
     * Obtiene todos los eventos del log
     * @return Lista de eventos
     */
    public static List<EventLog> getAllEvents() {
        return logEvents;
    }

    /**
     * Obtiene eventos del log por usuario
     * @param user Usuario a filtrar
     * @return Lista de eventos del usuario
     */
    public static List<EventLog> getEventsByUser(String user) {
        List<EventLog> userEvents = new ArrayList<>();
        for (EventLog event : logEvents) {
            if (event.getUser().equals(user)) {
                userEvents.add(event);
            }
        }
        return userEvents;
    }

    /**
     * Obtiene eventos del log por fecha
     * @param date Fecha a filtrar
     * @return Lista de eventos de la fecha
     */
    public static List<EventLog> getEventsByDate(LocalDateTime date) {
        List<EventLog> dateEvents = new ArrayList<>();
        for (EventLog event : logEvents) {
            if (event.getDateTime().toLocalDate().equals(date.toLocalDate())) {
                dateEvents.add(event);
            }
        }
        return dateEvents;
    }
}