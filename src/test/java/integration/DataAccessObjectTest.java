package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

/**
 * Suite de pruebas de integración para {@link DataAccessObject}, verificando el mapeo
 * con {@link AsociadoDTO} y {@link Asociado}, las operaciones CRUD parametrizadas y el
 * manejo de errores de SQL contra el motor MySQL a través de {@link BaseDeDatos}.
 */
public class DataAccessObjectTest {
    private DataAccessObject dao;
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
        baseDeDatos.iniciarBD();
        dao = new DataAccessObject();
    }

    @After
    public void tearDown() {
        dao = null;
        baseDeDatos = null;
    }

    // =========================================================================
    // 1. Pruebas de Mapeo Objeto-Relacional (CrearDTO)
    // =========================================================================

    @Test
    public void testCrearDTODesdeAsociadoMapeoCorrecto() {
        Domicilio domicilio = new Domicilio("Av. Colon", 1500, "Mar del Plata");
        Asociado asociado = new Asociado("Laureano", "Garcia", "42111222", "2235998877", domicilio);

        AsociadoDTO dto = dao.CrearDTO(asociado);

        assertNotNull("El DTO generado no debe ser nulo", dto);
        assertEquals("Laureano", dto.getNombre());
        assertEquals("Garcia", dto.getApellido());
        assertEquals("42111222", dto.getDni());
        assertEquals("Av. Colon", dto.getCalle());
        assertEquals(1500, dto.getNumero());
        assertEquals("Mar del Plata", dto.getCiudad());
        assertEquals("2235998877", dto.getTelefono());
    }

    @Test
    public void testCrearDTODesdeParametrosMapeoCorrecto() {
        AsociadoDTO dto = dao.CrearDTO(10, "Lucas", "Paniagua", "41222333", "San Juan", 2400, "Mar del Plata", "2236123456");

        assertNotNull("El DTO generado no debe ser nulo", dto);
        assertEquals("Lucas", dto.getNombre());
        assertEquals("Paniagua", dto.getApellido());
        assertEquals("41222333", dto.getDni());
        assertEquals("San Juan", dto.getCalle());
        assertEquals(2400, dto.getNumero());
        assertEquals("Mar del Plata", dto.getCiudad());
        assertEquals("2236123456", dto.getTelefono());
    }

    @Test
    public void testCrearDTODesdeAsociadoConDomicilioNullLanzaNullPointerException() {
        // Persona y Asociado no validan que domicilio != null en su constructor
        Asociado asociadoSinDomicilio = new Asociado("Pedro", "Gomez", "38111222", "2234445566", null);

        assertThrows(
            "CrearDTO lanza NullPointerException cuando el Asociado tiene Domicilio null",
            NullPointerException.class,
            () -> dao.CrearDTO(asociadoSinDomicilio)
        );
    }

    // =========================================================================
    // 2. Operaciones de Alta y Lectura (guardar, cargarBD, cargarConLimite, cargar)
    // =========================================================================

    @Test
    public void testGuardarAsociadoValidoPersisteEnBD() throws SQLException {
        Asociado nuevo = new Asociado("Nicolas", "Zuviria", "43999888", "2234776655", new Domicilio("Alberti", 950, "Mar del Plata"));

        dao.guardar(nuevo);

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals("La cantidad total de asociados debe aumentar de 20 a 21", 21, lista.size());

        AsociadoDTO guardado = lista.get(20);
        assertEquals("Nicolas", guardado.getNombre());
        assertEquals("Zuviria", guardado.getApellido());
        assertEquals("43999888", guardado.getDni());
        assertEquals("Alberti", guardado.getCalle());
        assertEquals(950, guardado.getNumero());
        assertEquals("Mar del Plata", guardado.getCiudad());
        assertEquals("2234776655", guardado.getTelefono());
    }

    @Test
    public void testFlujoCompletoAsociadoDTOHaciaAsociadoYGuardarEnBD() throws SQLException {
        // Simula el flujo real de Clinica.guardarNuevoAsociado(AsociadoDTO)
        AsociadoDTO dtoEntrada = new AsociadoDTO("Luciano", "Vega", "45123456", "Mar del Plata", "Salta", 1850, "2235112233");
        Asociado asociadoDominio = new Asociado(dtoEntrada);

        dao.guardar(asociadoDominio);

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals(21, lista.size());
        AsociadoDTO recuperado = lista.get(20);
        assertEquals("Luciano", recuperado.getNombre());
        assertEquals("Vega", recuperado.getApellido());
        assertEquals("45123456", recuperado.getDni());
        assertEquals("Salta", recuperado.getCalle());
        assertEquals(1850, recuperado.getNumero());
        assertEquals("Mar del Plata", recuperado.getCiudad());
        assertEquals("2235112233", recuperado.getTelefono());
    }

    @Test
    public void testCargarBDDevuelveTodosLosAsociadosSemilla() throws SQLException {
        ArrayList<AsociadoDTO> asociados = dao.cargarBD();
        assertNotNull(asociados);
        assertEquals("cargarBD() debe retornar los 20 asociados semilla", 20, asociados.size());
        assertEquals("Juan", asociados.get(0).getNombre());
        assertEquals("12345678", asociados.get(0).getDni());
        assertEquals("Adrian", asociados.get(19).getNombre());
        assertEquals("28293031", asociados.get(19).getDni());
    }

    @Test
    public void testCargarConLimiteValoresLimiteYParticionesEquivalencia() throws SQLException {
        // Valor límite inferior válido: 1
        ArrayList<AsociadoDTO> unAsociado = dao.cargarConLimite(1);
        assertEquals("Con límite 1 debe retornar exactamente 1 asociado", 1, unAsociado.size());
        assertEquals("12345678", unAsociado.get(0).getDni());

        // Valor intermedio: 5
        ArrayList<AsociadoDTO> cincoAsociados = dao.cargarConLimite(5);
        assertEquals("Con límite 5 debe retornar exactamente 5 asociados", 5, cincoAsociados.size());

        // Valor límite exacto a la cantidad de registros en BD: 20
        ArrayList<AsociadoDTO> veinteAsociados = dao.cargarConLimite(20);
        assertEquals("Con límite 20 debe retornar exactamente los 20 asociados semilla", 20, veinteAsociados.size());

        // Valor superior a la cantidad en BD: 25 (debe retornar los 20 existentes)
        ArrayList<AsociadoDTO> maximoAsociados = dao.cargarConLimite(25);
        assertEquals("Con límite 25 sobre 20 registros debe retornar 20 asociados", 20, maximoAsociados.size());
    }

    /**
     * Prueba cargar un asociado existente por su ID primario (id = 1).
     * FALLA EN ROJO: DataAccessObject.cargar(int id) no invoca resultado.next()
     * antes de leer resultado.getInt("id"), lanzando SQLException: Before start of result set.
     */
    @Test
    public void testCargarPorIdExistente() throws SQLException {
        AsociadoDTO dto = dao.cargar(1);
        assertNotNull("cargar(1) debe devolver el AsociadoDTO correspondiente al id = 1", dto);
        assertEquals("Juan", dto.getNombre());
        assertEquals("State", dto.getApellido());
        assertEquals("12345678", dto.getDni());
    }

    /**
     * Prueba cargar un asociado con un ID que no existe en la base de datos (id = 999).
     * FALLA EN ROJO: DataAccessObject.cargar(int id) no verifica if (resultado.next()),
     * por lo que lanza SQLException: Before start of result set en lugar de retornar null.
     */
    @Test
    public void testCargarPorIdInexistenteDevuelveNull() throws SQLException {
        AsociadoDTO dto = dao.cargar(999);
        assertNull("cargar(999) sobre un ID inexistente debería retornar null sin lanzar SQLException", dto);
    }

    // =========================================================================
    // 3. Operaciones de Actualización y Baja (actualizar, eliminar)
    // =========================================================================

    /**
     * Prueba actualizar los datos de un asociado existente (id = 2, Maria Template, DNI 87654321).
     * FALLA EN ROJO: DataAccessObject.actualizar delega en BaseDeDatos.ejecutarActualizacion,
     * donde el parámetro WHERE id = ? está hardcodeado en 1 (stmt.setInt(8, 1)).
     * Al intentar poner el DNI '87654321' (del registro 2) en el registro 1, MySQL lanza
     * SQLIntegrityConstraintViolationException: Duplicate entry '87654321' for key 'Asociados.DNI'.
     */
    @Test
    public void testActualizarAsociadoExistente() throws SQLException {
        Asociado mariaModificada = new Asociado(
            "Maria",
            "TemplateActualizada",
            "87654321",
            "2239999999",
            new Domicilio("Nueva Calle", 555, "Mar del Plata")
        );

        dao.actualizar(mariaModificada);

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        AsociadoDTO mariaEnBD = lista.stream()
            .filter(a -> "87654321".equals(a.getDni()))
            .findFirst()
            .orElse(null);

        assertNotNull("El asociado con DNI 87654321 debe seguir existiendo", mariaEnBD);
        assertEquals("TemplateActualizada", mariaEnBD.getApellido());
        assertEquals("Nueva Calle", mariaEnBD.getCalle());
        assertEquals(555, mariaEnBD.getNumero());
        assertEquals("2239999999", mariaEnBD.getTelefono());
    }

    /**
     * Prueba dar de alta un nuevo asociado y luego actualizar su nombre/domicilio (cambiando su DNI),
     * verificando que el registro id = 1 (Juan State) no sea corrompido.
     * FALLA EN ROJO: Como el UPDATE tiene hardcodeado id = 1, actualizar el nuevo asociado
     * sobreescribe a Juan State (id = 1) y deja el asociado nuevo sin cambios.
     */
    @Test
    public void testActualizarAsociadoNuevoNoCorrompeRegistroIdUno() throws SQLException {
        Asociado nuevo = new Asociado("Martin", "Bellone", "44111222", "2231231234", new Domicilio("Mitre", 1000, "Mar del Plata"));
        dao.guardar(nuevo);

        Asociado modificado = new Asociado("MartinMod", "BelloneMod", "44999888", "2239998887", new Domicilio("Mitre", 2000, "Mar del Plata"));
        dao.actualizar(modificado);

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        boolean existeJuanStateOriginal = lista.stream()
            .anyMatch(a -> "12345678".equals(a.getDni()) && "Juan".equals(a.getNombre()));

        assertTrue(
            "El registro id = 1 (Juan State, DNI 12345678) fue sobreescrito por culpa del id=1 hardcodeado en actualizar()",
            existeJuanStateOriginal
        );
    }

    @Test
    public void testEliminarAsociadoExistentePorDni() throws SQLException {
        dao.eliminar("12345678");

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals("La cantidad de asociados debe reducirse de 20 a 19", 19, lista.size());
        boolean existeEliminado = lista.stream().anyMatch(a -> "12345678".equals(a.getDni()));
        assertFalse("El asociado con DNI 12345678 ya no debe estar en la base de datos", existeEliminado);
    }

    @Test
    public void testEliminarAsociadoConDniInexistenteMantieneRegistrosIntactos() throws SQLException {
        dao.eliminar("99999999");

        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals("Eliminar un DNI inexistente no debe alterar los 20 registros semilla", 20, lista.size());
    }

    // =========================================================================
    // 4. Excepciones de SQL y Validaciones de Integridad
    // =========================================================================

    @Test
    public void testGuardarAsociadoDniDuplicadoLanzaSQLException() {
        // DNI '12345678' ya existe en los datos semilla
        Asociado duplicado = new Asociado("Clon", "State", "12345678", "2235550000", new Domicilio("Calle", 10, "Mar del Plata"));

        assertThrows(
            "Guardar un asociado con DNI duplicado debe lanzar SQLException",
            SQLException.class,
            () -> dao.guardar(duplicado)
        );
    }

    @Test
    public void testGuardarAsociadoTelefonoExcedeVarcharLanzaSQLException() {
        // Columna telefono es VARCHAR(15)
        Asociado telefonoLargo = new Asociado("Ana", "Gomez", "50607080", "22355512345678901234", new Domicilio("Calle", 10, "Mar del Plata"));

        assertThrows(
            "Guardar un asociado con teléfono > 15 caracteres debe lanzar SQLException (Data truncation)",
            SQLException.class,
            () -> dao.guardar(telefonoLargo)
        );
    }

    @Test
    public void testPrecondicionesInvalidasLanzanAssertionError() {
        // Precondiciones con referencias nulas
        assertThrows(AssertionError.class, () -> dao.guardar(null));
        assertThrows(AssertionError.class, () -> dao.actualizar(null));
        assertThrows(AssertionError.class, () -> dao.CrearDTO((Asociado) null));

        // Valores límite inválidos en cargar(id) y cargarConLimite(limite): 0 y negativos
        assertThrows(AssertionError.class, () -> dao.cargar(0));
        assertThrows(AssertionError.class, () -> dao.cargar(-1));
        assertThrows(AssertionError.class, () -> dao.cargarConLimite(0));
        assertThrows(AssertionError.class, () -> dao.cargarConLimite(-5));

        // Precondición de DNI vacío o nulo en eliminar(dni)
        assertThrows(AssertionError.class, () -> dao.eliminar(""));
        assertThrows(AssertionError.class, () -> dao.eliminar(null));
    }

    /**
     * Prueba de integridad al intentar persistir un Asociado con campos vacíos ("").
     * FALLA EN ROJO: Ni Asociado, ni AsociadoDTO, ni DataAccessObject validan cadenas vacías,
     * permitiendo insertar en MySQL un registro sin nombre, sin apellido y con DNI vacío "".
     */
    @Test
    public void testGuardarAsociadoConCamposVaciosDeberiaRechazarse() {
        Asociado asociadoVacio = new Asociado("", "", "", "", new Domicilio("", 1, ""));

        assertThrows(
            "No debería permitirse guardar en la base de datos un Asociado con nombre, apellido y DNI vacíos",
            AssertionError.class,
            () -> dao.guardar(asociadoVacio)
        );
    }

    /**
     * Demuestra cómo la falta de validación en el constructor de AsociadoDTO y la ausencia de NOT NULL
     * en la tabla Asociados corrompen la lectura posterior en DataAccessObject.cargarBD().
     * FALLA EN ROJO: Se inserta un AsociadoDTO con numero = -5 (permitido por el constructor de AsociadoDTO
     * y por MySQL), y al llamar a dao.cargarBD(), CrearDTO invoca setNumero(-5) lanzando AssertionError.
     */
    @Test
    public void testCorrupcionDeBDPorDTOInvalidoImpideCargarBD() throws SQLException {
        AsociadoDTO dtoNumeroNegativo = new AsociadoDTO("Invalido", "Test", "91929394", "Mar del Plata", "Calle", -5, "2231112222");
        String insertSQL = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";
        baseDeDatos.ejecutarInsert(insertSQL, dtoNumeroNegativo);

        // Debería poder cargarse la BD o haberse impedido el insert previo, pero explota con AssertionError en CrearDTO
        ArrayList<AsociadoDTO> asociados = dao.cargarBD();
        assertEquals(21, asociados.size());
    }
}
