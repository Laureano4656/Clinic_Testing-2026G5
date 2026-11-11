package integrations;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite de tests de integración y concurrencia de la Ambulancia.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AmbulanciaConcurrenciaTest.class
})
public class AmbulanciaConcurrenciaTestSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AmbulanciaConcurrenciaTestSuite.class);
        System.out.println("==========================================================");
        System.out.println("Resumen Suite de Concurrencia de la Ambulancia:");
        System.out.println("Tests ejecutados: " + result.getRunCount());
        System.out.println("Tests exitosos (VERDE): " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Fallos detectados (ROJO): " + result.getFailureCount());
        System.out.println("==========================================================");
        result.getFailures().forEach(failure -> {
            System.out.println(" -> [ROJO] " + failure.getTestHeader() + ": " + failure.getMessage());
        });
    }
}
