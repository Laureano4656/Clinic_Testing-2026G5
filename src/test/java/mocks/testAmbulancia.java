package mocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.AtendiendoEnDomicilioState;
import modelo.modeloDominio.ambulancia.DisponibleState;
import modelo.modeloDominio.ambulancia.EnTallerState;
import modelo.modeloDominio.ambulancia.RegresandoClinicaSinPacienteState;
import modelo.modeloDominio.ambulancia.RegresandoDelTallerState;
import modelo.modeloDominio.ambulancia.RetornoAutomatico;
import modelo.modeloDominio.ambulancia.TrasladandoPacienteClinicaState;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

/**
 * Tests de las transiciones de estado de la Ambulancia según la tabla del
 * enunciado. El asociado, el operario y el retorno automático son mocks.
 */
public class testAmbulancia {

    private Ambulancia ambulancia;

    @Before
    public void setUp() {
        ambulancia = new Ambulancia();
    }

    // 1) Disponible

    @Test
    public void testSolicitudAmbulanciaDisponibleParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new DisponibleState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe pasar a Atendiendo a domicilio", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaDisponibleParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new DisponibleState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe pasar a Trasladando paciente", TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaDisponibleParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new DisponibleState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe permanecer en Disponible", DisponibleState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaDisponibleParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new DisponibleState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe pasar a En taller", EnTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    // 2) Trasladando paciente

    @Test
    public void testSolicitudAmbulanciaTrasladandoPacienteClinicaParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe permanecer en Trasladando paciente", TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaTrasladandoPacienteClinicaParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe permanecer en Trasladando paciente", TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaTrasladandoPacienteClinicaParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe pasar a Disponible", DisponibleState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaTrasladandoPacienteClinicaParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe permanecer en Trasladando paciente", TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual().getClass());
    }

    // 3) Atendiendo a domicilio

    @Test
    public void testSolicitudAmbulanciaAtendiendoADomicilioParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe permanecer en Atendiendo a domicilio", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaAtendiendoADomicilioParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe permanecer en Atendiendo a domicilio", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaAtendiendoADomicilioParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe pasar a Regresando sin paciente", RegresandoClinicaSinPacienteState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaAtendiendoADomicilioParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe permanecer en Atendiendo a domicilio", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    // 4) Regresando sin paciente

    @Test
    public void testSolicitudAmbulanciaRegresandoSinPacienteParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe pasar a Atendiendo a domicilio", AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoSinPacienteParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe pasar a Trasladando paciente", TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoSinPacienteParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe pasar a Disponible", DisponibleState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoSinPacienteParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe permanecer en Regresando sin paciente", RegresandoClinicaSinPacienteState.class,
                ambulancia.getEstadoActual().getClass());
    }

    // 5) En taller

    @Test
    public void testSolicitudAmbulanciaEnTallerParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe permanecer en En taller", EnTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaEnTallerParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe permanecer en En taller", EnTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaEnTallerParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe permanecer en En taller", EnTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaEnTallerParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe pasar a Regresando del taller", RegresandoDelTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    // 6) Regresando del taller

    @Test
    public void testSolicitudAmbulanciaRegresandoTallerParaAtencionDomicilio() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.atenderDomicilio(asociado);
        assertEquals("Debe permanecer en Regresando del taller", RegresandoDelTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoTallerParaTrasladarPaciente() {
        Asociado asociado = mock(Asociado.class);
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.trasladarALaClinica(asociado);
        assertEquals("Debe permanecer en Regresando del taller", RegresandoDelTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoTallerParaRetornoAutomatico() {
        RetornoAutomatico retornoAutomatico = mock(RetornoAutomatico.class);
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.retornoAutomatico(retornoAutomatico);
        assertEquals("Debe pasar a Disponible", DisponibleState.class,
                ambulancia.getEstadoActual().getClass());
    }

    @Test
    public void testSolicitudAmbulanciaRegresandoTallerParaMantenimiento() {
        Operario operario = mock(Operario.class);
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.solicitarMantenimiento(operario);
        assertEquals("Debe permanecer en Regresando del taller", RegresandoDelTallerState.class,
                ambulancia.getEstadoActual().getClass());
    }
}
