package integrations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.AtendiendoEnDomicilioState;
import modelo.modeloDominio.ambulancia.RetornoAutomatico;
import modelo.modeloDominio.ambulancia.State;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;
import modelo.modeloDominio.util.Domicilio;

/**
 * Tests de integración y concurrencia de la Ambulancia con hilos reales.
 * El timeout de cada test hace que un hilo trabado lo haga fallar.
 */
public class AmbulanciaConcurrenciaTest {

    private Ambulancia ambulancia;
    private Asociado asociado;

    @Before
    public void setUp() {
        ambulancia = new Ambulancia();
        asociado = mock(Asociado.class);
    }

    /**
     * Espera hasta que el hilo llegue al estado indicado.
     */
    private void esperarEstado(Thread hilo, Thread.State estado) throws InterruptedException {
        while (hilo.getState() != estado) {
            Thread.sleep(10);
        }
    }

    @Test(timeout = 5000)
    public void testSegundoAsociadoEsperaYSeDespierta() throws InterruptedException {
        ambulancia.atenderDomicilio(asociado);

        Thread segundo = new Thread(() -> ambulancia.atenderDomicilio(asociado));
        segundo.start();
        esperarEstado(segundo, Thread.State.WAITING);

        ambulancia.regresarSinPaciente();
        segundo.join();

        assertTrue("El segundo asociado debería tener la ambulancia al despertarse", ambulancia.isOcupado());
        assertEquals("La ambulancia debería estar atendiendo a domicilio al segundo asociado", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test(timeout = 10000)
    public void testNuncaUsanLaAmbulanciaDosALaVez() throws InterruptedException {
        AtomicInteger dentro = new AtomicInteger();
        AtomicInteger maximo = new AtomicInteger();
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Thread hilo = new Thread(() -> {
                for (int j = 0; j < 20; j++) {
                    ambulancia.atenderDomicilio(asociado);
                    maximo.accumulateAndGet(dentro.incrementAndGet(), Math::max);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                    dentro.decrementAndGet();
                    ambulancia.regresarSinPaciente();
                }
            });
            hilos.add(hilo);
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }

        assertEquals("La ambulancia no puede ser usada por más de un asociado a la vez", 1, maximo.get());
        assertFalse("Cuando todos los asociados terminan, la ambulancia debería quedar libre", ambulancia.isOcupado());
    }

    @Test(timeout = 5000)
    public void testFinalizarSimulacionLiberaHilosEnEspera() throws InterruptedException {
        ambulancia.atenderDomicilio(asociado);

        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread hilo = new Thread(() -> ambulancia.atenderDomicilio(asociado));
            hilos.add(hilo);
            hilo.start();
            esperarEstado(hilo, Thread.State.WAITING);
        }

        ambulancia.setSimulacionActiva(false);
        for (Thread hilo : hilos) {
            hilo.join();
        }
    }

    @Test(timeout = 15000)
    public void testRetornoAutomaticoLlamaALaAmbulancia() {
        State estadoMock = mock(State.class);
        ambulancia.setState(estadoMock);

        Thread hilo = new Thread(new RetornoAutomatico(ambulancia));
        hilo.setDaemon(true);
        hilo.start();

        verify(estadoMock, timeout(12000).description("RetornoAutomatico debería hacer volver a la ambulancia a los 10 segundos, pero no lo hizo")).RetornoClinica();

        ambulancia.setSimulacionActiva(false);
        hilo.interrupt();
    }

    @Test(timeout = 120000)
    public void testSimulacionCompletaTermina() throws InterruptedException {
        Domicilio domicilio = new Domicilio("Calle", 1, "Ciudad");
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            hilos.add(new Thread(new Asociado("Nombre" + i, "Apellido", "" + i, "1", domicilio, 2, ambulancia)));
        }
        hilos.add(new Thread(new Operario("Operario", "Apellido", "9", "1", domicilio, ambulancia)));

        for (Thread hilo : hilos) {
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }

        assertFalse("La simulación debería finalizar cuando los asociados completan sus solicitudes", ambulancia.isSimulacionActiva());
        assertFalse("Al finalizar la simulación, la ambulancia debería quedar libre", ambulancia.isOcupado());
    }
}
