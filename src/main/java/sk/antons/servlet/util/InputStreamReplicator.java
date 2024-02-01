/*
 * Copyright 2019 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.servlet.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Helper class for wrapping InputStream instances. It enable
 * to read request content two more than once.
 * @author antons
 */
public class InputStreamReplicator {
    private InputStream is;

    public InputStreamReplicator(InputStream is) { this.is = is; }

    public static InputStreamReplicator instance(InputStream is) { return new InputStreamReplicator(is); }

    private byte[] cache = null;

    /**
     * Returns InputStream with exact content as InputStream which creates
     * replicator
     * @return
     */
    public InputStream getInputStream() {
        if(is == null) return null;
        if(cache == null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int num;
                byte[] data = new byte[1024];
                while ((num = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, num);
                }
                buffer.flush();
                cache = buffer.toByteArray();
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return new ByteArrayInputStream(cache);

    }
}
