package pe.edu.cibertec.matricula_api.screenplay.questions;

import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ElHistorial implements Question<Response> {

    private ElHistorial() {
    }

    public static ElHistorial consultado() {
        return new ElHistorial();
    }

    @Override
    public Response answeredBy(Actor actor) {
        return actor.recall("respuestaHistorial");
    }
}
