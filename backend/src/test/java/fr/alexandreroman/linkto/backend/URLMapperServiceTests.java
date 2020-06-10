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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class URLMapperServiceTests {
    @Autowired
    private URLMapperService um;

    @Test
    void testMapUrlToKey() {
        final var url = "http://myblog.com";
        final var key1 = um.mapUrlToKey(url);
        final var key2 = um.mapUrlToKey(url);
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    void testMapHttpsArgsUrlToKey() {
        final var url = "https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.3.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web,data-jpa";
        final var key1 = um.mapUrlToKey(url);
        final var key2 = um.mapUrlToKey(url);
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    void testMapUrlToKeyWithNoPrefix() {
        final var url = "myblog.com";
        assertThat(um.mapUrlToKey(url)).isNotEmpty();
    }

    @Test
    void testMapKeyToUrl() {
        final var url = "http://myblog.com";
        final var key = um.mapUrlToKey(url);
        final var url2 = um.mapKeyToUrl(key);
        assertThat(url2).isEqualTo(url);
    }

    @Test
    void testMapKeyToHttpsUrl() {
        final var url = "https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.3.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web,data-jpa";
        final var key = um.mapUrlToKey(url);
        final var url2 = um.mapKeyToUrl(key);
        assertThat(url2).isEqualTo(url);
    }

    @Test
    void testMapKeyToUrlWithUnknownKey() {
        assertThrows(UnknownKeyException.class, () -> um.mapKeyToUrl("RANDOMKEY"));
    }

    @Test
    void testIncrementVisitCount() {
        final var url = "http://mycompany.com";
        final var key = um.mapUrlToKey(url);
        um.incrementVisitCount(key);
    }
}
