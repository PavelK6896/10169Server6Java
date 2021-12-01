package app.web.pavelk.server6;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@RestController
public class MainController {

    List<User> users;
    Integer id = 4;

    public MainController() {
        users = new ArrayList<>();
        users.add(User.builder().id(1).name("Bob").email("d@g").state("N").code("432").zip(133).build());
        users.add(User.builder().id(2).name("Jon").email("d@y").state("F").code("465").zip(534).build());
        users.add(User.builder().id(3).name("Ford").email("a@r").state("H").code("654").zip(654).build());
        users.add(User.builder().id(4).name("Tom").email("d@t").state("G").code("23").zip(23).build());
    }

    public Mono<ServerResponse> getOne(ServerRequest request) {
        log.info(request.toString());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(users));
    }

    public Mono<ServerResponse> postOne(ServerRequest request) {
        log.info(request.toString());
        return request.bodyToMono(User.class).
                flatMap(book -> {
                    book.setId(++id);
                    book.setCode(String.valueOf(ThreadLocalRandom.current().nextDouble()));
                    book.setZip(ThreadLocalRandom.current().nextInt());
                    users.add(book);

                    log.info(book.toString());
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(users));
                });
    }

    @Bean
    public RouterFunction<ServerResponse> route(MainController mainController) {
        return RouterFunctions.route()
                .GET("/1", RequestPredicates.contentType(MediaType.APPLICATION_JSON), mainController::getOne)
                .POST("/1", RequestPredicates.contentType(MediaType.APPLICATION_JSON), mainController::postOne)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/index1.html") final Resource indexHtml) {
        return RouterFunctions.route(RequestPredicates.GET("/"), request -> ok()
                .contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
    }
}

