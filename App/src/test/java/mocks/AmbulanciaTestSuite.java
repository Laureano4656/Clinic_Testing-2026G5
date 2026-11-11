package mocks;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite de tests de las transiciones de estado de la Ambulancia (con mocks).
 */
@RunWith(Suite.class)
@SuiteClasses({
    AmbulanciaTest.class
})
public class AmbulanciaTestSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AmbulanciaTestSuite.class);
        System.out.println("==========================================================");
        System.out.println("Resumen Suite de Estados de la Ambulancia (Mocks):");
        System.out.println("Tests ejecutados: " + result.getRunCount());
        System.out.println("Tests exitosos (VERDE): " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Fallos detectados (ROJO): " + result.getFailureCount());
        System.out.println("==========================================================");
        result.getFailures().forEach(failure -> {
            System.out.println(" -> [ROJO] " + failure.getTestHeader() + ": " + failure.getMessage());
        });
    }
}
