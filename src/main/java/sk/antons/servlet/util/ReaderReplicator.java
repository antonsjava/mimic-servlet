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

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Reader;

/**
 * Helper class for wrapping InputStream instances. It enable
 * to read request content two more than once.
 * @author antons
 */
public class ReaderReplicator {
    private Reader reader;

    public ReaderReplicator(Reader is) { this.reader = reader; }

    public static ReaderReplicator instance(Reader reader) { return new ReaderReplicator(reader); }

    private char[] cache = null;

    /**
     * Returns Reader with exact content as Reader which creates
     * replicator
     * @return
     */
    public Reader getReader() {
        if(reader == null) return null;
        if(cache == null) {
            try {
                CharArrayWriter buffer = new CharArrayWriter();
                int num;
                char[] data = new char[1024];
                while ((num = reader.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, num);
                }
                cache = buffer.toCharArray();
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return new CharArrayReader(cache);

    }
}
