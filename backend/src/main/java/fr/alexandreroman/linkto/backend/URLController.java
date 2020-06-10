/*
 * Copyright (c) 2020 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.alexandreroman.linkto.backend;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class URLController {
    private final URLMapperService um;

    @PostMapping("/api/v1/url/new")
    ResponseEntity<?> mapToUrl(@RequestBody NewURLMappingRequest req) {
        final var url = StringUtils.trimWhitespace(req.url);
        if (StringUtils.isEmpty(url)) {
            return ResponseEntity.badRequest().body("Missing input parameter: url");
        }
        final var key = um.mapUrlToKey(url);
        final var resp = new URLMappingResponse();
        resp.shortUrl = MvcUriComponentsBuilder.fromMethodName(URLController.class, "redirect", key).toUriString();
        resp.longUrl = url;

        log.info("Mapped URL: {} -> {}", resp.longUrl, resp.shortUrl);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(resp);
    }

    @GetMapping("/go/{key}")
    ResponseEntity<?> redirect(@PathVariable("key") String key) {
        um.incrementVisitCount(key);
        final var url = um.mapKeyToUrl(key);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    @ExceptionHandler(UnknownKeyException.class)
    ResponseEntity<?> onUnknownKeyException(UnknownKeyException e) {
        return ResponseEntity.notFound().build();
    }

    @Data
    static class NewURLMappingRequest {
        String url;
    }

    @Data
    static class URLMappingResponse {
        String longUrl;
        String shortUrl;
    }
}
