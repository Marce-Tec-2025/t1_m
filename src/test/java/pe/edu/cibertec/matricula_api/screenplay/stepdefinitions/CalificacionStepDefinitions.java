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
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

public class CalificacionStepDefinitions {

    private static final String CLAVE_TOKEN = "accessToken";
    private static final String CLAVE_ID_MATRICULA = "idMatricula";
    private static final String CLAVE_PASSWORD = "Cibertec2026";

    private String codigoAlumna;
    private String codigoDocente;
    private String codigoCurso;

    private String obtenerBaseUrl() {
        String url = EnvironmentSpecificConfiguration
                .from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getProperty("restapi.baseurl");
        return (url != null && !url.isBlank()) ? url : "http://localhost:8080";
    }

    private void iniciarSesion(String usuario) {
        theActorCalled(usuario).whoCan(CallAnApi.at(obtenerBaseUrl()));

        Response response = SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of("username", usuario, "password", CLAVE_PASSWORD))
                .post("/api/auth/login");

        String token = response.jsonPath().getString("accessToken");
        theActorInTheSpotlight().remember(CLAVE_TOKEN, token);
    }

    private void obtenerMatricula(String curso, String estado) {
        codigoCurso = curso;
        String token = theActorCalled(codigoAlumna).recall(CLAVE_TOKEN);

        SerenityRest.given()
                .baseUri(obtenerBaseUrl())
                .header("Authorization", "Bearer " + token)
                .queryParam("alumno", codigoAlumna)
                .queryParam("curso", curso)
                .queryParam("estado", estado)
                .get("/api/matriculas");

        theActorInTheSpotlight().should(
                seeThatResponse("matrícula encontrada",
                        res -> res.body("size()", greaterThan(0)))
        );

        List<Integer> ids = SerenityRest.lastResponse().jsonPath().getList("id", Integer.class);
        theActorCalled(codigoAlumna).remember(CLAVE_ID_MATRICULA, ids.get(0));
    }

    @Dado("que la alumna {string} inicia sesión")
    public void queLaAlumnaIniciaSesion(String codigo) {
        codigoAlumna = codigo;
        iniciarSesion(codigo);
    }

    @Dado("que el docente {string} inicia sesión")
    public void queElDocenteIniciaSesion(String codigo) {
        codigoDocente = codigo;
        iniciarSesion(codigo);
    }

    @Cuando("se obtiene la matrícula pagada del curso {string}")
    public void seObtieneLaMatriculaPagadaDelCurso(String curso) {
        obtenerMatricula(curso, "PAGADA");
    }

    @Cuando("se obtiene la matrícula no pagada del curso {string}")
    public void seObtieneLaMatriculaNoPagadaDelCurso(String curso) {
        obtenerMatricula(curso, "REGISTRADA");
    }

    @Cuando("el docente registra la nota {double}")
    public void elDocenteRegistraLaNota(double nota) {
        Integer idMatricula = theActorCalled(codigoAlumna).recall(CLAVE_ID_MATRICULA);

        theActorCalled(codigoDocente).attemptsTo(
                CalificarMatricula.deLaMatricula(idMatricula, nota)
        );
    }

    @Cuando("se consulta el historial académico de la alumna")
    public void seConsultaElHistorialAcademicoDeLaAlumna() {
        theActorCalled(codigoAlumna).attemptsTo(
                ConsultarHistorial.delAlumno(codigoAlumna)
        );
    }

    @Entonces("el historial muestra la condición {string}")
    public void elHistorialMuestraLaCondicion(String condicion) {
        Response historial = theActorCalled(codigoAlumna).asksFor(ElHistorial.consultado());

        assertThat(historial.jsonPath()
                        .getString("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.condicion"),
                equalTo(condicion));
    }

    @Entonces("los créditos aprobados son {int}")
    public void losCreditosAprobadosSon(int creditos) {
        Response historial = theActorCalled(codigoAlumna).asksFor(ElHistorial.consultado());

        assertThat(historial.jsonPath().getInt("creditosAprobados"), equalTo(creditos));
    }

    @Entonces("el promedio ponderado es {double}")
    public void elPromedioPonderadoEs(double promedio) {
        Response historial = theActorCalled(codigoAlumna).asksFor(ElHistorial.consultado());

        assertThat(historial.jsonPath().getDouble("promedioPonderado"), closeTo(promedio, 0.01));
    }

    @Entonces("la API rechaza la calificación")
    public void laApiRechazaLaCalificacion() {
        theActorInTheSpotlight().should(
                seeThatResponse("calificación rechazada",
                        res -> res.statusCode(allOf(greaterThanOrEqualTo(400), lessThan(500))))
        );
    }
}