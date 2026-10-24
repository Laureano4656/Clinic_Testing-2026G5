package integration;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BaseDeDatosTest.class,
})

public class BaseDeDatosTestingSuite {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(BaseDeDatosTestingSuite.class);
        if (result.wasSuccessful()) {
            System.out.println("Todos los tests pasaron exitosamente.");
        } else {
            System.out.println("Algunos tests fallaron. Número de fallos: " + result.getFailureCount());
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}