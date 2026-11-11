package units;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite de tests unitarios de AsociadoDTO.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AsociadoDTOTest.class
})
public class AsociadoDTOTestSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AsociadoDTOTestSuite.class);
        System.out.println("==========================================================");
        System.out.println("Resumen Suite Unitaria de AsociadoDTO:");
        System.out.println("Tests ejecutados: " + result.getRunCount());
        System.out.println("Tests exitosos (VERDE): " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Fallos detectados (ROJO): " + result.getFailureCount());
        System.out.println("==========================================================");
        result.getFailures().forEach(failure -> {
            System.out.println(" -> [ROJO] " + failure.getTestHeader() + ": " + failure.getMessage());
        });
    }
}
