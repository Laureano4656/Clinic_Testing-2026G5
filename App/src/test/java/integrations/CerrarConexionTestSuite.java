package integrations;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite aislada para ejecutar únicamente las pruebas de cierre de conexión
 * ({@link CerrarConexionTest}) sin afectar la conexión del Singleton en {@link PersistenciaTestSuite}.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CerrarConexionTest.class
})
public class CerrarConexionTestSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(CerrarConexionTestSuite.class);
        System.out.println("==========================================================");
        System.out.println("Resumen Suite Aislada de Cierre de Conexión (Punto 2):");
        System.out.println("Tests ejecutados: " + result.getRunCount());
        System.out.println("Tests exitosos (VERDE): " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Fallos detectados (ROJO): " + result.getFailureCount());
        System.out.println("==========================================================");
        result.getFailures().forEach(failure -> {
            System.out.println(" -> [ROJO] " + failure.getTestHeader() + ": " + failure.getMessage());
        });
    }
}
