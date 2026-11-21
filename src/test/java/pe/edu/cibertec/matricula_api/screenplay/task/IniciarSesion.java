package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import java.util.Map;

public class IniciarSesion implements Task {
    private final String usuario;
    private final String clave;

    public IniciarSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public static IniciarSesion conCredenciales(String usuario, String clave) {
        return new IniciarSesion(usuario, clave);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login").with(
                        request -> request.body(Map.of("username", usuario, "password", clave))
                )
        );
        String token = LastResponse.received().answeredBy(actor).jsonPath().getString("accessToken");
        String refresh = LastResponse.received().answeredBy(actor).jsonPath().getString("refreshToken");

        actor.remember("accessToken", token);
        actor.remember("refreshToken", refresh);
        actor.remember("originalRefresh", refresh);
    }
}