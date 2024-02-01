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

import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * Simple ServletInputStream implementation.
 * Listener mechanism is not implemented.
 * @author antons
 */
public class SimpleServletInputStream extends ServletInputStream {
    private InputStream is;

    public SimpleServletInputStream(InputStream is) { this.is = is; }

    public static SimpleServletInputStream instance(InputStream is) { return new SimpleServletInputStream(is); }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }


}
