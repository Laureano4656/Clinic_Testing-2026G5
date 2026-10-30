package integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

/**
 * Clase de pruebas aislada exclusivamente para verificar el cierre de conexión
 * ({@link BaseDeDatos#cerrarConexion()} y {@link DataAccessObject#cerrarConexion()}),
 * separada del resto de la suite para no cerrar la conexión compartida del Singleton
 * durante la ejecución de las demás pruebas CRUD.
 */
public class CerrarConexionTest {
    private BaseDeDatos baseDeDatos;
    private DataAccessObject dao;

    @Before
    public void setUp() {
        baseDeDatos = BaseDeDatos.getInstancia();
        dao = new DataAccessObject();
    }

    @Test
    public void testCerrarConexionCierraEfectivamenteLaConexion() throws SQLException {
        baseDeDatos.cerrarConexion();
        assertTrue(
            "La conexión debe figurar como cerrada tras invocar cerrarConexion()",
            baseDeDatos.getConexion().isClosed()
        );
    }

    @Test
    public void testCerrarConexionDesdeDAOLanzaSQLExceptionEnOperacionesPosteriores() throws SQLException {
        dao.cerrarConexion();
        assertThrows(
            "Operar sobre el DAO tras cerrarConexion() debe lanzar SQLException",
            SQLException.class,
            () -> dao.cargarBD()
        );
    }

    /**
     * Prueba el ciclo de vida del Singleton al cerrar la conexión y volver a pedir getInstancia().
     * FALLA EN ROJO: cerrarConexion() cierra el objeto Connection pero no reinicia la instancia
     * ni getInstancia() valida si la conexión está cerrada, dejando el Singleton inutilizable.
     */
    @Test
    public void testCerrarConexionYReobtenerInstanciaOperativa() throws SQLException {
        baseDeDatos.cerrarConexion();
        BaseDeDatos nuevaReferencia = BaseDeDatos.getInstancia();
        assertFalse(
            "Al solicitar getInstancia() tras cerrarConexion(), la conexión debería estar abierta y operativa",
            nuevaReferencia.getConexion().isClosed()
        );
    }

    /**
     * Verifica que al cerrar la conexión desde un DAO e instanciar un nuevo DataAccessObject,
     * el nuevo DAO sea operativo.
     * FALLA EN ROJO: Como BaseDeDatos.getInstancia() devuelve el mismo Singleton con la conexión
     * cerrada, el nuevo DataAccessObject hereda una conexión cerrada y lanza SQLException al consultar.
     */
    @Test
    public void testNuevoDAODespuesDeCerrarConexionDeberiaSerOperativo() throws SQLException {
        dao.cerrarConexion();
        DataAccessObject nuevoDao = new DataAccessObject();
        ArrayList<AsociadoDTO> lista = nuevoDao.cargarBD();
        assertNotNull("Un nuevo DAO debería poder cargar la BD", lista);
    }
}
