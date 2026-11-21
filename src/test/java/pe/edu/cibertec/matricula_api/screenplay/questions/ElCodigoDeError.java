package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class ElCodigoDeError implements Question<String> {
    public static ElCodigoDeError deLaRespuesta() {
        return new ElCodigoDeError();
    }

    @Override
    public String answeredBy(Actor actor) {
        return LastResponse.received().answeredBy(actor).jsonPath().getString("codigo");
    }
}
