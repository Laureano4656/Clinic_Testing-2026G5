package units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistencia.AsociadoDTO;

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
    public void TestSetNombre() {
        asociado.setNombre("Enzo");
        assertEquals("Seteado correctamente", "Enzo", asociado.getNombre());
    }

    @Test
    public void TestSetNombreNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setNombre(null);
        });
    }

    @Test
    public void TestSetApellido() {
        asociado.setApellido("Peres");
        assertEquals("Seteado correctamente", "Peres", asociado.getApellido());
    }

    @Test
    public void TestSetApellidoNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setApellido(null);
        });
    }

    @Test
    public void TestSetDni() {
        asociado.setDni("12345678");
        assertEquals("Seteado correctamente", "12345678", asociado.getDni());
    }

    @Test
    public void TestSetDniNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setDni(null);
        });
    }

    @Test
    public void TestSetCiudad() {
        asociado.setCiudad("Cordoba");
        assertEquals("Seteado correctamente", "Cordoba", asociado.getCiudad());
    }

    @Test
    public void TestSetCiudadNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setCiudad(null);
        });
    }

    @Test
    public void TestSetCalle() {
        asociado.setCalle("Av Siempre Viva");
        assertEquals("Seteado correctamente", "Av Siempre Viva", asociado.getCalle());
    }

    @Test
    public void TestSetCalleNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setCalle(null);
        });
    }

    @Test
    public void TestSetNumero() {
        asociado.setNumero(10);
        assertEquals("Seteado correctamente", 10, asociado.getNumero());
    }

    @Test
    public void TestSetNumeroInvalido() {
        assertThrows(AssertionError.class, () -> {
            asociado.setNumero(-1);
        });
    }

    @Test
    public void TestSetTelefono() {
        asociado.setTelefono("3511234567");
        assertEquals("Seteado correctamente", "3511234567", asociado.getTelefono());
    }

    @Test
    public void TestSetTelefonoNull() {
        assertThrows(AssertionError.class, () -> {
            asociado.setTelefono(null);
        });
    }

}