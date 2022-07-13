package com.insyde.flink.statefun.api;

import com.insyde.flink.statefun.FunctionRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


/**
 * A {@link FunctionRouteController} is the point of API to interact with this module.
 * Endpoint {@code /v1/functions} that is used in the controller should be used by Apache Flink SF engine to invoke
 * the module
 */
@Slf4j
@RestController
@RequestMapping("/v1/functions")
@RequiredArgsConstructor
public class FunctionRouteController {

    private final FunctionRouter router;

    @PostMapping(consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public CompletableFuture<byte[]> onRequest(@RequestBody byte[] request) {
        return router.route(request);
    }
}
