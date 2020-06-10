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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class URLControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private URLMapperService um;

    @Test
    void testNewUrl() throws Exception {
        final var req = new URLController.NewURLMappingRequest();
        req.url = "http://foobar.com";
        final var jsonBody = om.writer().writeValueAsString(req);
        final var result = mvc.perform(post("/api/v1/url/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk()).andReturn();
        final var resp = om.reader().readValue(result.getResponse().getContentAsString(), URLController.URLMappingResponse.class);
        assertThat(resp.longUrl).isEqualTo(req.url);
        assertThat(resp.shortUrl).isNotEmpty();
    }

    @Test
    void testNewHttpsArgsUrl() throws Exception {
        final var req = new URLController.NewURLMappingRequest();
        req.url = "https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.3.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web,data-jpa";
        final var jsonBody = om.writer().writeValueAsString(req);
        final var result = mvc.perform(post("/api/v1/url/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk()).andReturn();
        final var resp = om.reader().readValue(result.getResponse().getContentAsString(), URLController.URLMappingResponse.class);
        assertThat(resp.longUrl).isEqualTo(req.url);
        assertThat(resp.shortUrl).isNotEmpty();
    }

    @Test
    void testNewUrlEmpty() throws Exception {
        final var req = new URLController.NewURLMappingRequest();
        req.url = "";
        final var jsonBody = om.writer().writeValueAsString(req);
        mvc.perform(post("/api/v1/url/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRedirect() throws Exception {
        final var url = "http://omg.foo";
        final var key = um.mapUrlToKey(url);
        mvc.perform(get("/go/{key}", key))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, url));
    }

    @Test
    void testRedirectUnknownKey() throws Exception {
        mvc.perform(get("/go/{key}", "NOTFOUND"))
                .andExpect(status().isNotFound());
    }
}
