package services;

import datastructure.tree.AVLTree;
import datastructure.tree.TreeException;
import domain.Passenger;
import persistence.PassengerDataLoader;
import datastructure.list.SinglyLinkedList;

public class PassengerService {
    private static PassengerService instance;
    private AVLTree passengerTree;

    private PassengerService() {
        this.passengerTree = new AVLTree();
        loadPassengersToTree();
    }

    public static synchronized PassengerService getInstance() {
        if (instance == null) {
            instance = new PassengerService();
        }
        return instance;
    }

    private void loadPassengersToTree() {
        SinglyLinkedList<Passenger> passengers = PassengerDataLoader.loadPassengers();
        try {
            for (int i = 1; i <= passengers.size(); i++) {
                passengerTree.add(passengers.getNode(i).data);
            }
        } catch (Exception e) {
            System.err.println("Error loading passengers to AVL: " + e.getMessage());
        }
    }

    // a. Registrar nuevo pasajero
    public boolean registerPassenger(Passenger passenger) {
        try {
            if (findPassengerById(passenger.getId()) != null) return false;
            passengerTree.add(passenger);
            // Se puede guardar en JSON si quieres persistencia inmediata
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // b. Buscar pasajero por cédula
    public Passenger findPassengerById(int id) {
        try {
            return findPassengerById(passengerTree, id);
        } catch (TreeException e) {
            return null;
        }
    }

    private Passenger findPassengerById(AVLTree tree, int id) throws TreeException {
        return findPassengerById(tree.getRoot(), id);
    }

    // Haz root public en AVLTree, o crea un método getter para root. Aquí es solo ejemplo:
    private Passenger findPassengerById(Object nodeObj, int id) throws TreeException {
        if (nodeObj == null) return null;
        datastructure.tree.BTreeNode node = (datastructure.tree.BTreeNode) nodeObj;
        Passenger p = (Passenger) node.data;
        if (p.getId() == id) return p;
        Passenger left = findPassengerById(node.left, id);
        if (left != null) return left;
        return findPassengerById(node.right, id);
    }

    // c. Mostrar historial de vuelo
    public SinglyLinkedList<?> getFlightHistory(int passengerId) {
        Passenger p = findPassengerById(passengerId);
        if (p != null) return p.getFlightHistory();
        return null;
    }
}