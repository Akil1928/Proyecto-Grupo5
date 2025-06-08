package security;

import domain.User;
import persistence.FileManager;
import datastructure.list.SinglyLinkedList;
import datastructure.list.ListException;

/**
 * Clase para gestionar usuarios y autenticación
 */
public class UserManager {
    private SinglyLinkedList<User> users;
    private FileManager<User> fileManager;
    private static UserManager instance;
    private User currentUser;

    // Constructor privado para patrón Singleton
    private UserManager() {
        users = new SinglyLinkedList<>();
        fileManager = new FileManager<>();
        loadUsers();
    }

    /**
     * Obtener instancia única de UserManager (patrón Singleton)
     * @return Instancia de UserManager
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Cargar usuarios desde un archivo
     */
    private void loadUsers() {
        // Si no hay usuarios, crear el usuario administrador por defecto
        if (users.isEmpty()) {
            // Crear un usuario administrador por defecto
            User adminUser = new User(1, "admin",
                    PasswordEncryptor.encrypt("admin123"),
                    "admin@airport.com", "administrator");
            users.add(adminUser);

            // Crear un usuario normal por defecto
            User regularUser = new User(2, "user",
                    PasswordEncryptor.encrypt("user123"),
                    "user@airport.com", "user");
            users.add(regularUser);

            // Guardar usuarios en archivo
            saveUsers();
        }
    }

    /**
     * Guardar usuarios en un archivo
     */
    public void saveUsers() {
        // Implementar guardado de usuarios
        // fileManager.writeFile("users.dat", users);
    }

    /**
     * Autenticar un usuario
     *
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado o null si falla la autenticación
     */
    public User authenticate(String username, String password) {
        try {
            if (users.isEmpty()) {
                return null;
            }

            for (int i = 1; i <= users.size(); i++) {
                User user = (User) users.getNode(i).data;
                if (user.getName().equals(username) &&
                        PasswordEncryptor.verify(password, user.getPassword())) {
                    currentUser = user;
                    return user;
                }
            }
        } catch (ListException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registrar un nuevo usuario
     *
     * @param name Nombre de usuario
     * @param password Contraseña en texto plano
     * @param email Correo electrónico
     * @param role Rol (administrator o user)
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registerUser(String name, String password, String email, String role) {
        try {
            // Verificar si el usuario ya existe
            for (int i = 1; i <= users.size(); i++) {
                User user = (User) users.getNode(i).data;
                if (user.getName().equals(name) || user.getEmail().equals(email)) {
                    return false; // Usuario o email ya existen
                }
            }

            // Generar nuevo ID
            int newId = users.isEmpty() ? 1 : ((User) users.getNode(users.size()).data).getId() + 1;

            // Crear y añadir nuevo usuario
            User newUser = new User(newId, name,
                    PasswordEncryptor.encrypt(password),
                    email, role);
            users.add(newUser);

            // Guardar usuarios actualizados
            saveUsers();
            return true;
        } catch (ListException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener el usuario actual
     *
     * @return Usuario actual o null si no hay sesión iniciada
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Cerrar la sesión actual
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Verificar si el usuario actual es administrador
     *
     * @return true si el usuario actual es administrador, false en caso contrario
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && "administrator".equals(currentUser.getRole());
    }
}