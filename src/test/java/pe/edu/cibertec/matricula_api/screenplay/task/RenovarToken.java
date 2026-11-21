package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import java.util.Map;

public class RenovarToken implements Task {
    private final boolean usarTokenOriginal;

    public RenovarToken(boolean usarTokenOriginal) {
        this.usarTokenOriginal = usarTokenOriginal;
    }

    public static RenovarToken conTokenOriginal() {
        return new RenovarToken(true);
    }

    public static RenovarToken conTokenNuevo() {
        return new RenovarToken(false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String tokenAEnviar = usarTokenOriginal ? actor.recall("originalRefresh") : actor.recall("refreshToken");

        actor.attemptsTo(
                Post.to("/api/auth/refresh").with(
                        request -> request.body(Map.of("refreshToken", tokenAEnviar))
                )
        );

        if (!usarTokenOriginal && LastResponse.received().answeredBy(actor).statusCode() == 200) {
            String nuevoToken = LastResponse.received().answeredBy(actor).jsonPath().getString("accessToken");
            String nuevoRefresh = LastResponse.received().answeredBy(actor).jsonPath().getString("refreshToken");
            actor.remember("accessToken", nuevoToken);
            actor.remember("refreshToken", nuevoRefresh);
        }
    }
}
