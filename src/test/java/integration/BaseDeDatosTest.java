package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;

/**
 * Suite de pruebas de integración para la clase {@link BaseDeDatos} y su interacción
 * con {@link AsociadoDTO} y el motor MySQL.
 */
public class BaseDeDatosTest {
    private BaseDeDatos baseDeDatos;

    @Before
    public void setUp() throws Exception {
        baseDeDatos = BaseDeDatos.getInstancia();
        if (baseDeDatos.getConexion() != null && baseDeDatos.getConexion().isClosed()) {
            Field instanciaField = BaseDeDatos.class.getDeclaredField("instancia");
            instanciaField.setAccessible(true);
            instanciaField.set(null, null);
            baseDeDatos = BaseDeDatos.getInstancia();
        }
        // Restablece la tabla Asociados y los 20 datos semilla antes de cada prueba
        baseDeDatos.iniciarBD();
    }

    @After
    public void tearDown() {
        baseDeDatos = null;
    }

    // =========================================================================
    // 1. Pruebas de Conexión y Patrón Singleton
    // =========================================================================

    @Test
    public void testSingletonMismaInstancia() throws SQLException {
        BaseDeDatos otraInstancia = BaseDeDatos.getInstancia();
        assertNotNull("La instancia no debe ser nula", baseDeDatos);
        assertSame("getInstancia() debe retornar exactamente la misma referencia Singleton", baseDeDatos, otraInstancia);
        assertNotNull("La conexión interna no debe ser nula", baseDeDatos.getConexion());
        assertFalse("La conexión debe estar abierta", baseDeDatos.getConexion().isClosed());
    }

    // =========================================================================
    // 2. Inicialización Automática de Tablas y Datos Semilla
    // =========================================================================

    @Test
    public void testIniciarBDCreaTablaAsociados() throws SQLException {
        String query = "SHOW TABLES;";
        ResultSet resultado = baseDeDatos.getConexion().createStatement().executeQuery(query);
        assertTrue("Debe existir al menos una tabla en la base de datos Grupo_7", resultado.next());
        assertEquals("La tabla creada debe llamarse Asociados", "Asociados", resultado.getString("Tables_in_Grupo_7"));
        resultado.close();
    }

    @Test
    public void testDatosSemillaCantidadTotal() throws SQLException {
        ResultSet resultado = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados;", -1);
        int contador = 0;
        while (resultado.next()) {
            contador++;
        }
        resultado.close();
        assertEquals("iniciarBD() debe insertar exactamente 20 asociados semilla", 20, contador);
    }

    @Test
    public void testDatosSemillaPrimerYUltimoRegistro() throws SQLException {
        ResultSet rsPrimero = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 1);
        assertTrue("Debe existir el asociado semilla con id = 1", rsPrimero.next());
        assertEquals("Juan", rsPrimero.getString("nombre"));
        assertEquals("State", rsPrimero.getString("apellido"));
        assertEquals("12345678", rsPrimero.getString("dni"));
        assertEquals("Buenos Aires", rsPrimero.getString("calle"));
        assertEquals(123, rsPrimero.getInt("numero"));
        assertEquals("Mar del Plata", rsPrimero.getString("ciudad"));
        assertEquals("2235551234", rsPrimero.getString("telefono"));
        rsPrimero.close();

        ResultSet rsUltimo = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 20);
        assertTrue("Debe existir el asociado semilla con id = 20", rsUltimo.next());
        assertEquals("Adrian", rsUltimo.getString("nombre"));
        assertEquals("Composite", rsUltimo.getString("apellido"));
        assertEquals("28293031", rsUltimo.getString("dni"));
        rsUltimo.close();
    }

    /**
     * Verifica la consistencia de los datos semilla en la columna ciudad.
     * FALLA EN ROJO: El registro id = 4 (Jose Concurrencia) fue cargado en iniciarBD()
     * con ciudad = ' Mar del Plata' (con un espacio en blanco al inicio).
     */
    @Test
    public void testDatosSemillaConsistenciaFormatoCiudad() throws SQLException {
        ResultSet resultado = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados;", -1);
        while (resultado.next()) {
            String ciudad = resultado.getString("ciudad");
            assertEquals(
                "Inconsistencia en dato semilla para id=" + resultado.getInt("id") + ": la ciudad no debe tener espacios iniciales",
                "Mar del Plata",
                ciudad
            );
        }
        resultado.close();
    }

    /**
     * Verifica la consistencia de longitud/formato de los teléfonos en los datos semilla.
     * FALLA EN ROJO: Los registros id = 12 ('155889900') e id = 17 ('155445566') tienen 9 dígitos
     * mientras que todos los demás asociados semilla tienen teléfonos de 10 dígitos.
     */
    @Test
    public void testDatosSemillaConsistenciaLongitudTelefono() throws SQLException {
        ResultSet resultado = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados;", -1);
        while (resultado.next()) {
            String telefono = resultado.getString("telefono");
            assertEquals(
                "Inconsistencia en teléfono semilla para id=" + resultado.getInt("id") + " (" + telefono + ")",
                10,
                telefono.length()
            );
        }
        resultado.close();
    }

    @Test
    public void testIniciarBDIdempotenciaRestauraEstadoSemilla() throws SQLException {
        // Modificamos el estado eliminando un registro e insertando otro
        baseDeDatos.ejecutarDelete("DELETE FROM Asociados WHERE dni = ?;", "12345678");
        AsociadoDTO extra = new AsociadoDTO("Temp", "User", "99999999", "Mar del Plata", "Calle", 10, "2230001111");
        baseDeDatos.ejecutarInsert(
            "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);",
            extra
        );

        // Re-ejecutar iniciarBD() debe recrear la tabla y restaurar exactamente los 20 asociados originales
        baseDeDatos.iniciarBD();

        ResultSet rs = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados;", -1);
        int contador = 0;
        while (rs.next()) {
            contador++;
        }
        rs.close();
        assertEquals("iniciarBD() debe restaurar exactamente 20 registros", 20, contador);
    }

    // =========================================================================
    // 3. Operaciones CRUD Parametrizadas sobre BaseDeDatos
    // =========================================================================

    @Test
    public void testEjecutarInsertConAsociadoDTO() throws SQLException {
        AsociadoDTO nuevoDTO = new AsociadoDTO("Enzo", "Sorrenti", "40506070", "Mar del Plata", "Constitucion", 5000, "2234445566");
        String insertSQL = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        baseDeDatos.ejecutarInsert(insertSQL, nuevoDTO);

        ResultSet rs = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 21);
        assertTrue("El asociado insertado debe existir con id = 21", rs.next());
        assertEquals("Enzo", rs.getString("nombre"));
        assertEquals("Sorrenti", rs.getString("apellido"));
        assertEquals("40506070", rs.getString("dni"));
        assertEquals("Constitucion", rs.getString("calle"));
        assertEquals(5000, rs.getInt("numero"));
        assertEquals("Mar del Plata", rs.getString("ciudad"));
        assertEquals("2234445566", rs.getString("telefono"));
        rs.close();
    }

    @Test
    public void testEjecutarConsultaConIdInexistenteDevuelveResultSetVacio() throws SQLException {
        ResultSet rs = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 999);
        assertNotNull("El ResultSet no debe ser nulo", rs);
        assertFalse("No debe haber filas para un ID inexistente (999)", rs.next());
        rs.close();
    }

    /**
     * Prueba el método real BaseDeDatos.ejecutarActualizacion(String, AsociadoDTO)
     * intentando actualizar el registro id = 2.
     * FALLA EN ROJO: BaseDeDatos.ejecutarActualizacion tiene hardcodeado stmt.setInt(8, 1),
     * por lo que ignora a quién se quiere actualizar y siempre pisa el registro con id = 1 (Juan State).
     */
    @Test
    public void testEjecutarActualizacionModificaRegistroCorrectoSinPisarIdUno() throws SQLException {
        String updateSQL = "UPDATE Asociados SET nombre = ?, apellido = ?, dni = ?, calle = ?, numero = ?, ciudad = ?, telefono = ? WHERE id = ?;";
        AsociadoDTO dtoActualizado = new AsociadoDTO("MariaModificada", "TemplateMod", "99887766", "Mar del Plata", "Libertad", 999, "2230000000");

        baseDeDatos.ejecutarActualizacion(updateSQL, dtoActualizado);

        // El registro id = 1 (Juan State, DNI 12345678) NO debería haber sido modificado
        ResultSet rsId1 = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 1);
        assertTrue(rsId1.next());
        String nombreId1 = rsId1.getString("nombre");
        rsId1.close();

        // Si actualizar funcionara recibiendo el ID objetivo (ej. id = 2), Juan seguiría en id = 1
        assertEquals(
            "ERROR: ejecutarActualizacion tiene hardcodeado stmt.setInt(8, 1) y sobreescribió a Juan State (id=1)",
            "Juan",
            nombreId1
        );
    }

    @Test
    public void testEjecutarDeleteEliminaAsociadoPorDni() throws SQLException {
        String deleteSQL = "DELETE FROM Asociados WHERE dni = ?;";
        baseDeDatos.ejecutarDelete(deleteSQL, "12345678");

        ResultSet rs = baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", 1);
        assertFalse("El asociado con DNI 12345678 (id=1) debe haber sido eliminado", rs.next());
        rs.close();
    }

    // =========================================================================
    // 4. Captura de Excepciones SQL y Validaciones de Precondiciones
    // =========================================================================

    @Test
    public void testEjecutarInsertDniDuplicadoLanzaSQLException() {
        // '12345678' ya pertenece al asociado semilla id = 1 (columna DNI es UNIQUE)
        AsociadoDTO dtoDuplicado = new AsociadoDTO("Otro", "Usuario", "12345678", "Mar del Plata", "Calle", 100, "2231112233");
        String insertSQL = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        assertThrows(
            "Insertar un DNI duplicado debe lanzar SQLException por restricción UNIQUE",
            SQLException.class,
            () -> baseDeDatos.ejecutarInsert(insertSQL, dtoDuplicado)
        );
    }

    @Test
    public void testEjecutarInsertDniExcedeLongitudColumnaLanzaSQLException() {
        // La columna DNI está definida como VARCHAR(10)
        AsociadoDTO dtoDniLargo = new AsociadoDTO("Nombre", "Apellido", "123456789012345", "Mar del Plata", "Calle", 100, "2231112233");
        String insertSQL = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        assertThrows(
            "Insertar un DNI mayor a VARCHAR(10) debe lanzar SQLException (Data truncation)",
            SQLException.class,
            () -> baseDeDatos.ejecutarInsert(insertSQL, dtoDniLargo)
        );
    }

    @Test
    public void testEjecutarConsultaSQLInvalidoLanzaSQLException() {
        assertThrows(
            "Una consulta SQL mal formada o sobre tabla inexistente debe lanzar SQLException",
            SQLException.class,
            () -> baseDeDatos.ejecutarConsulta("SELECT * FROM TablaInexistente WHERE id = ?;", 1)
        );
    }

    @Test
    public void testPrecondicionesNulasLanzanAssertionError() {
        AsociadoDTO dtoValido = new AsociadoDTO("Juan", "Perez", "30405060", "Mar del Plata", "Luro", 123, "2234567890");

        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarConsulta(null, 1));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarInsert(null, dtoValido));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarInsert("INSERT INTO Asociados VALUES (?)", null));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarActualizacion(null, dtoValido));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarActualizacion("UPDATE Asociados SET nombre = ?", null));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarDelete(null, "12345678"));
        assertThrows(AssertionError.class, () -> baseDeDatos.ejecutarDelete("DELETE FROM Asociados WHERE dni = ?", null));
    }

    /**
     * Verifica qué ocurre al usar el valor límite id = -1 en una consulta parametrizada con '?'.
     * Como ejecutarConsulta usa (id != -1) como valor centinela para omitir stmt.setInt(1, id),
     * pasar id = -1 en una query con '?' deja el parámetro sin asignar y lanza SQLException.
     */
    @Test
    public void testEjecutarConsultaConCentinelaMenosUnoEnQueryConParametroLanzaSQLException() {
        assertThrows(
            "Pasar id = -1 a una consulta con '?' no setea el parámetro 1 y debe lanzar SQLException",
            SQLException.class,
            () -> baseDeDatos.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ?;", -1)
        );
    }

    /**
     * Prueba de integración entre AsociadoDTO y BaseDeDatos.ejecutarInsert.
     * FALLA EN ROJO: El constructor parametrizado de AsociadoDTO no valida nulls ni numero <= 0,
     * y la tabla Asociados en iniciarBD() no tiene restricciones NOT NULL ni CHECK (numero > 0),
     * permitiendo insertar registros inválidos en MySQL sin lanzar AssertionError ni SQLException.
     */
    @Test
    public void testEjecutarInsertConDTOInvalidoPorConstructorDeberiaRechazarse() {
        AsociadoDTO dtoInvalido = new AsociadoDTO(null, null, null, null, null, -10, null);
        String insertSQL = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        assertThrows(
            "No debería permitirse insertar un AsociadoDTO con campos null y número negativo (-10)",
            Throwable.class,
            () -> baseDeDatos.ejecutarInsert(insertSQL, dtoInvalido)
        );
    }
}
