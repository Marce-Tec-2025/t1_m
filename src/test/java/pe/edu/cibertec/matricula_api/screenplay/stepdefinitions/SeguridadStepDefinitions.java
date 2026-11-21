package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import pe.edu.cibertec.matricula_api.screenplay.task.*;
import pe.edu.cibertec.matricula_api.screenplay.questions.*;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

public class SeguridadStepDefinitions {

    private final String BASE_URL = "http://localhost:8080";
    private Actor actor = Actor.named("Estudiante");

    @Given("que el alumno {string} inicia sesion")
    public void iniciarSesion(String usuario) {
        actor.whoCan(CallAnApi.at(BASE_URL));
        actor.attemptsTo(IniciarSesion.conCredenciales(usuario, "Cibertec2026"));
    }

    @When("intenta renovar el token con su refresh token original")
    public void renovarToken() {
        actor.attemptsTo(RenovarToken.conTokenOriginal());
    }

    @When("intenta reutilizar el refresh token original")
    public void reutilizarToken() {
        actor.attemptsTo(RenovarToken.conTokenOriginal());
    }

    @When("consulta su perfil con el nuevo access token")
    public void consultarPerfilNuevo() {
        actor.attemptsTo(ConsultarPerfil.actual());
    }

    @When("consulta su perfil con el mismo access token")
    public void consultarPerfilMismo() {
        actor.attemptsTo(ConsultarPerfil.actual());
    }

    @When("cierra sesion")
    public void cerrarSesion() {
        actor.attemptsTo(CerrarSesion.actual());
    }

    @When("intenta listar todos los alumnos")
    public void listarAlumnos() {
        actor.attemptsTo(ListarAlumnos.todos());
    }

    @Given("que el usuario {string} intenta iniciar sesion con clave incorrecta por {int} vez")
    public void loginIncorrecto(String usuario, int intento) {
        if (intento == 1) {
            actor.whoCan(CallAnApi.at(BASE_URL));
        }
        actor.attemptsTo(IntentarLoginFallido.con(usuario, "ClaveMala123"));
    }

    @Given("que el usuario {string} intenta iniciar sesion con clave correcta")
    public void loginCorrecto(String usuario) {
        actor.whoCan(CallAnApi.at(BASE_URL));
        actor.attemptsTo(IntentarLoginFallido.con(usuario, "Cibertec2026"));
    }

    @Then("el codigo de error es {string}")
    public void verificarCodigoError(String codigoEsperado) {
        actor.should(
                seeThat(ElCodigoDeError.deLaRespuesta(), equalTo(codigoEsperado))
        );
    }
}
