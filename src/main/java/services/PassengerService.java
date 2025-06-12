package services;

import datastructure.tree.AVLTree;
import datastructure.tree.BTreeNode;
import domain.Passenger;
import persistence.PassengerDataLoader;
import datastructure.list.SinglyLinkedList;

public class PassengerService {
    private static PassengerService instance;
    private AVLTree passengerTree;

    private PassengerService() {
        this.passengerTree = PassengerDataLoader.loadPassengers();
    }

    public static synchronized PassengerService getInstance() {
        if (instance == null) {
            instance = new PassengerService();
        }
        return instance;
    }

    // a. Registrar nuevo pasajero
    public boolean registerPassenger(Passenger passenger) {
        try {
            if (findPassengerById(passenger.getId()) != null) return false;
            passengerTree.add(passenger);
            // PassengerDataLoader.savePassengers(passengerTree); // Guarda si quieres persistencia inmediata
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // b. Buscar pasajero por cédula
    public Passenger findPassengerById(int id) {
        return findPassengerById(passengerTree.getRoot(), id);
    }

    private Passenger findPassengerById(BTreeNode node, int id) {
        if (node == null) return null;
        Passenger p = (Passenger) node.data;
        if (p.getId() == id) return p;
        Passenger found = findPassengerById(node.left, id);
        if (found != null) return found;
        return findPassengerById(node.right, id);
    }

    // c. Mostrar historial de vuelo (asumiendo que Passenger tiene getFlightHistory)
    public SinglyLinkedList<?> getFlightHistory(int passengerId) {
        Passenger p = findPassengerById(passengerId);
        if (p != null) return p.getFlightHistory();
        return null;
    }

    // d. Obtener todos los pasajeros como lista (para mostrar en tabla, etc)
    public java.util.List<Passenger> getAllPassengers() {
        java.util.List<Object> inOrder = new java.util.ArrayList<>();
        passengerTree.inOrderList(inOrder);
        java.util.List<Passenger> result = new java.util.ArrayList<>();
        for (Object obj : inOrder) result.add((Passenger) obj);
        return result;
    }

    public Passenger getPassengerById(int id) {
        return getPassengerById(passengerTree.getRoot(), id);
    }

    private Passenger getPassengerById(BTreeNode node, int id) {
        if (node == null) {
            return null;  // No se encontró el pasajero
        }

        Passenger passenger = (Passenger) node.data;

        // Si el ID coincide, devolver el pasajero
        if (passenger.getId() == id) {
            return passenger;
        }

        // Buscar en el subárbol izquierdo
        Passenger leftSearch = getPassengerById(node.left, id);
        if (leftSearch != null) {
            return leftSearch;
        }

        // Buscar en el subárbol derecho
        return getPassengerById(node.right, id);
    }

}