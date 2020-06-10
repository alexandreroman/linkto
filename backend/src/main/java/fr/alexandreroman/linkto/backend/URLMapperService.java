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

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class URLMapperService {
    private final RandomKeyGeneratorService rkg;
    private final StringRedisTemplate redis;

    private String sanitizeUrl(String url) throws MalformedURLException {
        final URL goodUrl;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            goodUrl = new URL("http://" + url);
        } else {
            goodUrl = new URL(url);
        }
        return goodUrl.toExternalForm();
    }

    public String mapUrlToKey(String url) throws URLMappingFailedException {
        final var key = rkg.generateRandomKey();
        final String urlToKeep;
        try {
            urlToKeep = sanitizeUrl(url);
        } catch (MalformedURLException e) {
            throw new URLMappingFailedException(url, e);
        }
        if (!Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, urlToKeep))) {
            throw new URLMappingFailedException(url);
        }
        return key;
    }

    public String mapKeyToUrl(String key) throws UnknownKeyException {
        final var url = redis.opsForValue().get(key);
        if (url == null) {
            throw new UnknownKeyException(key);
        }
        return url;
    }

    public void incrementVisitCount(String key) {
        final String visitCountKey = key + ".visits";
        redis.opsForValue().increment(visitCountKey);
    }
}
