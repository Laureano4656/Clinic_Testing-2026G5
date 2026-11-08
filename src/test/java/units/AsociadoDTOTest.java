package units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistencia.AsociadoDTO;

/**
 * Tests unitarios de los setters y getters de AsociadoDTO.
 */
public class AsociadoDTOTest {

    private AsociadoDTO asociado;

    @Before
    public void setUp() {
        asociado = new AsociadoDTO();
    }

    @After
    public void tearDown() {
        asociado = null;
    }

    @Test
    public void testSetNombre() {
        asociado.setNombre("Enzo");
        assertEquals("El nombre debe ser el seteado", "Enzo", asociado.getNombre());
    }

    @Test
    public void testSetApellido() {
        asociado.setApellido("Peres");
        assertEquals("El apellido debe ser el seteado", "Peres", asociado.getApellido());
    }

    @Test
    public void testSetDni() {
        asociado.setDni("12345678");
        assertEquals("El DNI debe ser el seteado", "12345678", asociado.getDni());
    }

    @Test
    public void testSetCiudad() {
        asociado.setCiudad("Cordoba");
        assertEquals("La ciudad debe ser la seteada", "Cordoba", asociado.getCiudad());
    }

    @Test
    public void testSetCalle() {
        asociado.setCalle("Av Siempre Viva");
        assertEquals("La calle debe ser la seteada", "Av Siempre Viva", asociado.getCalle());
    }

    @Test
    public void testSetNumero() {
        asociado.setNumero(10);
        assertEquals("El numero debe ser el seteado", 10, asociado.getNumero());
    }

    @Test
    public void testSetNumeroInvalido() {
        assertThrows("Un numero negativo debe lanzar AssertionError", AssertionError.class, () -> {
            asociado.setNumero(-1);
        });
    }

    @Test
    public void testSetTelefono() {
        asociado.setTelefono("3511234567");
        assertEquals("El telefono debe ser el seteado", "3511234567", asociado.getTelefono());
    }
}
