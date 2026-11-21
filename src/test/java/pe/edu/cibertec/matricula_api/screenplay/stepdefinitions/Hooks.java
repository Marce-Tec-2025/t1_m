package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import java.time.LocalDateTime;

public class Hooks {
    @Before
    public void antesDeCadaEscenario() {
        System.out.println("==================================================");
        System.out.println("ESTUDIANTE: Marcello Gabriel Mattos Pantoja");
        System.out.println("CÓDIGO: i202506783");
        System.out.println("FECHA Y HORA: " + LocalDateTime.now());
        System.out.println("==================================================");
    }
}