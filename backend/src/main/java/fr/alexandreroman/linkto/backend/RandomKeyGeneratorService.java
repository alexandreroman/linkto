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

import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomKeyGeneratorService {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random random = new Random();
    @Setter
    private int keyLength = 8;

    public String generateRandomKey() {
        final var buf = new StringBuilder(keyLength);
        for (int i = 0; i < keyLength; ++i) {
            final var c = CHARS.charAt(random.nextInt(CHARS.length()));
            buf.append(c);
        }
        return buf.toString();
    }
}
