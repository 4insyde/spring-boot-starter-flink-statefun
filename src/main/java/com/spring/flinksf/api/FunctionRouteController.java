package com.spring.flinksf.api;

import com.spring.flinksf.FunctionRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

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
