package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final Integer idMatricula;
    private final double nota;

    private CalificarMatricula(Integer idMatricula, double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula deLaMatricula(Integer idMatricula, double nota) {
        return new CalificarMatricula(idMatricula, nota);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/nota")
                        .with(request -> request
                                .pathParam("id", idMatricula)
                                .header("Authorization", "Bearer " + token)
                                .contentType(ContentType.JSON)
                                .body(Map.of("nota", nota)))
        );
    }
}