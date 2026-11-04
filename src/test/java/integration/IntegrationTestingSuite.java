package integration;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite de pruebas de integración para el Punto 2 (Persistencia: BaseDeDatos, DataAccessObject y AsociadoDTO).
 */
@RunWith(Suite.class)
@SuiteClasses({
    BaseDeDatosTest.class,
    DataAccessObjectTest.class
})
public class IntegrationTestingSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(IntegrationTestingSuite.class);
        System.out.println("==========================================================");
        System.out.println("Resumen Suite de Integración (Punto 2):");
        System.out.println("Tests ejecutados: " + result.getRunCount());
        System.out.println("Tests exitosos (VERDE): " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Fallos detectados (ROJO): " + result.getFailureCount());
        System.out.println("==========================================================");
        result.getFailures().forEach(failure -> {
            System.out.println(" -> [ROJO] " + failure.getTestHeader() + ": " + failure.getMessage());
        });
    }
}
