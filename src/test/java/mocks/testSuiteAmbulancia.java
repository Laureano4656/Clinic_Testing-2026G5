package mocks;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        testAmbulancia.class,
})

public class testSuiteAmbulancia {
    public static void main(String[] args) {

    }
}