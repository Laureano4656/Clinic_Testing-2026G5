package mocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.DisponibleState;
import modelo.modeloDominio.ambulancia.EnTallerState;
import modelo.modeloDominio.ambulancia.State;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

/**
 * Tests de Ambulancia aislada: el State se reemplaza por un mock para
 * verificar que la ambulancia delega correctamente en su estado.
 */
public class testAmbulancia {

    private Ambulancia ambulancia;
    private Asociado asociado;
    private Operario operario;

    @Before
    public void setUp() {
        ambulancia = new Ambulancia();

        asociado = mock(Asociado.class);
        operario = mock(Operario.class);
    }

    @Test
    public void testSolicitudAmbulanciaDisponible() {
        Operario operario = mock(Operario.class);
        DisponibleState disponibleState = mock(DisponibleState.class);
        ambulancia.setState(disponibleState);
        when(ambulancia.getEstadoActual()).thenReturn(disponibleState);
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Se paso al estado de mantenimiento", EnTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }
}
