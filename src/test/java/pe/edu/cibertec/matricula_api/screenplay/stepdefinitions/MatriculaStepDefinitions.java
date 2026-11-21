package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

public class MatriculaStepDefinitions {

    private static final String CLAVE_TOKEN = "accessToken";
    private static final String CLAVE_ID_MATRICULA = "idMatricula";

    private String obtenerBaseUrl() {
        String url = EnvironmentSpecificConfiguration
                .from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getProperty("restapi.baseurl");
        return (url != null && !url.isBlank()) ? url : "http://localhost:8080";
    }

    @Dado("que el alumno {string} inicia sesión con clave {string}")
    public void queElAlumnoIniciaSesionConClave(String usuario, String clave) {
        theActorCalled(usuario).whoCan(CallAnApi.at(obtenerBaseUrl()));

        Response response = SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of("username", usuario, "password", clave))
                .post("/api/auth/login");

        String token = response.jsonPath().getString("accessToken");
        theActorInTheSpotlight().remember(CLAVE_TOKEN, token);
    }

    @Cuando("cotiza el curso {string} para el alumno {string}")
    public void cotizaElCursoParaElAlumno(String curso, String alumno) {
        String token = theActorInTheSpotlight().recall(CLAVE_TOKEN);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", alumno, "cursos", List.of(curso)))
                .post("/api/matriculas/cotizacion");
    }

    @Cuando("se matricula en el curso {string} para el alumno {string}")
    public void seMatriculaEnElCursoParaElAlumno(String curso, String alumno) {
        String token = theActorInTheSpotlight().recall(CLAVE_TOKEN);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", alumno, "codigoCurso", curso))
                .post("/api/matriculas");
    }

    @Entonces("el total de la cotización es {double}")
    public void elTotalDeLaCotizacionEs(double total) {
        theActorInTheSpotlight().should(
                seeThatResponse("total de cotización",
                        res -> res.body("total", equalTo((float) total)))
        );
    }

    @Entonces("el estado de la matrícula es {string}")
    public void elEstadoDeLaMatriculaEs(String estado) {
        theActorInTheSpotlight().should(
                seeThatResponse("estado de matrícula",
                        res -> res.body("estado", equalTo(estado)))
        );
    }

    @Entonces("el monto de la matrícula es {double}")
    public void elMontoDeLaMatriculaEs(double monto) {
        theActorInTheSpotlight().should(
                seeThatResponse("monto de matrícula",
                        res -> res.body("monto", equalTo((float) monto)))
        );
    }

    @Entonces("guarda el id de la matrícula")
    public void guardaElIdDeLaMatricula() {
        Integer id = SerenityRest.lastResponse().jsonPath().getInt("id");
        theActorInTheSpotlight().remember(CLAVE_ID_MATRICULA, id);
    }

    @Cuando("paga la matrícula sin enviar cuerpo")
    public void pagaLaMatriculaSinEnviarCuerpo() {
        String token = theActorInTheSpotlight().recall(CLAVE_TOKEN);
        Integer idMatricula = theActorInTheSpotlight().recall(CLAVE_ID_MATRICULA);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .patch("/api/matriculas/{id}/pagar", idMatricula);
    }

    @Entonces("la fecha de pago no es nula")
    public void laFechaDePagoNoEsNula() {
        theActorInTheSpotlight().should(
                seeThatResponse("fecha de pago no nula",
                        res -> res.body("fechaPago", notNullValue()))
        );
    }

    @Cuando("consulta la lista de pagos")
    public void consultaLaListaDePagos() {
        String token = theActorInTheSpotlight().recall(CLAVE_TOKEN);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .get("/api/pagos");
    }

    @Entonces("existe el pago de la matrícula con método de pago por defecto y operación {string}")
    public void existeElPagoDeLaMatriculaConMetodoPorDefectoYOperacion(String formatoOperacion) {
        Integer idMatricula = theActorInTheSpotlight().recall(CLAVE_ID_MATRICULA);

        theActorInTheSpotlight().should(
                seeThatResponse("pago registrado",
                        res -> res
                                .body("find { it.idMatricula == " + idMatricula + " }.idMatricula", equalTo(idMatricula))
                                .body("find { it.idMatricula == " + idMatricula + " }.metodo", notNullValue())
                                .body("find { it.idMatricula == " + idMatricula + " }.numeroOperacion", matchesPattern("^OP-[0-9]{6}$"))
                )
        );
    }

    @Cuando("intenta pagar nuevamente la misma matrícula sin enviar cuerpo")
    public void intentaPagarNuevamenteLaMismaMatriculaSinEnviarCuerpo() {
        String token = theActorInTheSpotlight().recall(CLAVE_TOKEN);
        Integer idMatricula = theActorInTheSpotlight().recall(CLAVE_ID_MATRICULA);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .patch("/api/matriculas/{id}/pagar", idMatricula);
    }

    @Entonces("el código de respuesta es {int}")
    public void elCodigoDeRespuestaEs(int codigo) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(codigo))
        );
    }

    @Entonces("la respuesta cumple el esquema {string}")
    public void laRespuestaCumpleElEsquema(String esquema) {
        theActorInTheSpotlight().should(
                seeThatResponse("cumple esquema " + esquema,
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/" + esquema)))
        );
    }
}