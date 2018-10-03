package com.mycorp;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.thorntail.test.ThorntailTestRunner;

@RunWith(ThorntailTestRunner.class)
public class MyTest {

    @Test
    public void test() {
        when().get("/").then()
                .statusCode(200)
                .body(containsString("Bon Jour, World"));
    }
}

