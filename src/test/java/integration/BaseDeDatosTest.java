package integration;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistencia.BaseDeDatos;

public class BaseDeDatosTest {
    private BaseDeDatos baseDeDatos;

    @Before
    public void setUp() {
        baseDeDatos = BaseDeDatos.getInstancia();
        
        String query = "INSERT INTO Asociados (nombre, apellido, dni, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement stmt = baseDeDatos.getConexion().prepareStatement(query);
            stmt.setString(1, "TestNombre");
            stmt.setString(2, "TestApellido");
            stmt.setString(3, "11111100");
            stmt.setString(4, "TestCalle");
            stmt.setInt(5, 123);
            stmt.setString(6, "TestCiudad");
            stmt.setString(7, "1234567890");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        String query = "DELETE FROM Asociados WHERE dni = ?;";
        try {
            PreparedStatement stmt = baseDeDatos.getConexion().prepareStatement(query);
            stmt.setString(1, "11111100");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        baseDeDatos = null;
    }

    

    @Test
    public void testSingleton() {
        BaseDeDatos otraInstancia = BaseDeDatos.getInstancia();
        assertEquals("Debe ser la misma instancia", baseDeDatos, otraInstancia);
    }

    @Test
    public void testIniciarBD() {
        String query = "SHOW TABLES;";
        try {
            ResultSet resultado = baseDeDatos.getConexion().createStatement().executeQuery(query);
            resultado.next(); // Mueve el cursor al primer resultado
            assertEquals("Debe haber al menos una tabla en la base de datos",  resultado.getString("Tables_in_Grupo_7"),"Asociados");
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e;
            });
        }
    }
    @Test
    public void testDatosSemilla() {
        String query = "SELECT * FROM Asociados;";
        try {
            ResultSet resultado = baseDeDatos.getConexion().createStatement().executeQuery(query);
            resultado.next(); // Mueve el cursor al primer resultado
            assertEquals("Debe haber al menos un asociado en la base de datos",  resultado.getString("nombre"),"Juan");
            assertEquals("Debe haber al menos un asociado en la base de datos",  resultado.getString("apellido"),"State");
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e;
            });
        }
    }

   

    @Test
    public void testEjecutarInsert() {
        String query = "INSERT INTO Asociados (nombre, apellido, dni, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement stmt = baseDeDatos.getConexion().prepareStatement(query);
            stmt.setString(1, "TestNombre");
            stmt.setString(2, "TestApellido");
            stmt.setString(3, "111111001");
            stmt.setString(4, "TestCalle");
            stmt.setInt(5, 123);
            stmt.setString(6, "TestCiudad");
            stmt.setString(7, "1234567890");
            int rowsAffected = stmt.executeUpdate();
            assertEquals("Debe insertar un nuevo asociado", 1, rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e;
            });
        }
    }

    @Test   
    public void testEjecutarActualizacion() {
        String query = "UPDATE Asociados SET nombre = ?, apellido = ?, dni = ?, calle = ?, numero = ?, ciudad = ?, telefono = ? WHERE dni = ?;";
        try {
            PreparedStatement stmt = baseDeDatos.getConexion().prepareStatement(query);
            stmt.setString(1, "UpdatedNombre");
            stmt.setString(2, "UpdatedApellido");
            stmt.setString(3, "000222222");
            stmt.setString(4, "UpdatedCalle");
            stmt.setInt(5, 321);
            stmt.setString(6, "UpdatedCiudad");
            stmt.setString(7, "0987654321");
            stmt.setString(8, "11111100"); // Actualiza el asociado con dni 000222222
            int rowsAffected = stmt.executeUpdate();
            assertEquals("Debe actualizar un asociado existente", 1, rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e;
            });
        }
    } 
    @Test
    public void testEjecutarConsulta() {
        String query = "SELECT * FROM Asociados WHERE id = ?;";
        try {
            ResultSet resultado = baseDeDatos.ejecutarConsulta(query, 1);
            resultado.next(); // Mueve el cursor al primer resultado
            assertEquals("Debe haber un asociado con id 1",  resultado.getString("nombre"),"Juan");
            assertEquals("Debe haber un asociado con id 1",  resultado.getString("apellido"),"State");
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e;
            });
        }
    }
}