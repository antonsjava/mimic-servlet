/*
 * Copyright 2023 Anton Straka
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
package sk.antons.servlet.mimic.builder;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import sk.antons.jaul.binary.Bytes;
import sk.antons.jaul.util.AsRuntimeEx;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;

/**
 * Helper class for mimic processor creation.
 * @author antons
 */
public class ProcessorBuilder {

    Processor processor = Processor.instance();

    public static ProcessorBuilder instance() { return new ProcessorBuilder(); }

    /**
     * Configuration name
     * @param value
     * @return this
     */
    public ProcessorBuilder name(String value) { processor.name = value; return this; }
    /**
     * Output encoding for text content. (default is utf-8)
     * @param value
     * @return this
     */
    public ProcessorBuilder encoding(String value) { processor.encoding = value; return this; }
    /**
     * response status.
     * @param value
     * @return this
     */
    public ProcessorBuilder status(int value) { processor.status = value; return this; }
    /**
     * response length.
     * @param value
     * @return this
     */
    public ProcessorBuilder length(int value) { processor.length = value; return this; }
    /**
     * response content type.
     * @param value
     * @return this
     */
    public ProcessorBuilder contentType(String value) { processor.contentType = value; return this; }
    /**
     * response header.
     * @param name
     * @param value
     * @return this
     */
    public ProcessorBuilder header(String name, String value) { processor.headers.add(Header.instance(name, value)); return this; }
    /**
     * response content as stream. (Also content length is set)
     * @param value
     * @return this
     */
    public ProcessorBuilder content(InputStream value) {
        processor.name("is");
        final byte[] data = Bytes.fromStream(value);
        processor.content = () -> new ByteArrayInputStream(data);
        processor.length = data.length;
        return this;
    }
    /**
     * response content as string. (Also content length is set)
     * @param value
     * @return this
     */
    public ProcessorBuilder content(String value) {
        processor.name("string");
        final byte[] data = processor.toBytes(value);
        processor.content = () -> new ByteArrayInputStream(data);
        processor.length = data.length;
        return this;
    }
    /**
     * response content as string supplier.
     * @param value
     * @return this
     */
    public ProcessorBuilder content(Supplier<String> value) {
        processor.name("string");
        processor.content = () -> {
            final byte[] data = processor.toBytes(value.get());
            return new ByteArrayInputStream(data);
        };
        processor.length = 0;
        return this;
    }
    /**
     * response content as file. (Also content length is set)
     * @param value
     * @return this
     */
    public ProcessorBuilder content(File value) {
        processor.name("file: " + value.getAbsolutePath());
        processor.length = (int)value.length();
        processor.content = () -> {
            try {
                return new FileInputStream(value);
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        };
        return this;
    }
    /**
     * response content as resource defined by url. (Also content length is set)
     * @param url (like /foo/bar for file and classpath:META_INF/data.json for classpath)
     * @return this
     */
    public ProcessorBuilder byteContentFromUrl(String url) {
        processor.name("url: " + url);
        processor.content = () -> {
            try {
                return Resource.url(url).inputStream();
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        };
        return this;
    }
    /**
     * response content as resource defined by url. (Also content length is set)
     * @param url (like /foo/bar for file and classpath:META_INF/data.json for classpath)
     * @return this
     */
    public ProcessorBuilder textContentFromUrl(String url) {
        processor.name("url: " + url);
        try {
            String text = TextFile.read(Resource.url(url).inputStream(), "utf-8");
            byte[] data = processor.toBytes(text);
            processor.content = () -> new ByteArrayInputStream(data);
            processor.length = data.length;
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    public BiPredicate<HttpServletRequest, HttpServletResponse> build() { return processor; }


    private static class Processor implements BiPredicate<HttpServletRequest, HttpServletResponse> {

        String name = null;
        String encoding = "utf-8";
        int status = 200;
        int length = 0;
        String contentType = null;
        Supplier<InputStream> content = null;
        List<Header> headers = new ArrayList<>();

        private Processor name(String value) { this.name = (this.name == null) ?  value : this.name; return this;}

        public static Processor instance() { return new Processor(); }

        @Override
        public boolean test(HttpServletRequest req, HttpServletResponse res) {
            if(contentType != null) res.setContentType(contentType);
            res.setContentLength(length);
            res.setStatus(status);
            for(Header header : headers) {
                res.addHeader(header.name, header.value);
            }
            if(contentType != null) {
                try {
                    InputStream is = content.get();
                    ServletOutputStream os = res.getOutputStream();
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                    os.flush();
                    is.close();
                } catch(Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return true;
        }

        private byte[] toBytes(String value) {
            if(value == null) value = "";
            try {
                return value.getBytes(encoding);
            } catch(Exception e) {
                throw new IllegalStateException(e);
            }
        }

        private InputStream toInputStream(String value) {
            if(value == null) value = "";
            try {
                return new ByteArrayInputStream(value.getBytes(encoding));
            } catch(Exception e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public String toString() {
            return name;
        }


    }

    private static class Header {
        private String name;
        private String value;

        public static Header instance(String name, String value) {
            Header h = new Header();
            h.name = name;
            h.value = value;
            return h;
        }
    }

    /**
     * Helper class for reading and storing text contents.
     */
    public static class ProcessorHelper {

        private String inputEncoding = "utf-8";
        private String outputEncoding = "utf-8";
        private HttpServletRequest request;
        private HttpServletResponse response;

        public static ProcessorHelper instance(HttpServletRequest req, HttpServletResponse res) {
            ProcessorHelper rv = new ProcessorHelper();
            rv.request = req;
            rv.response = res;
            return rv;
        }

        public String inputEncoding() { return inputEncoding; }
        public String outputEncoding() { return outputEncoding; }
        public HttpServletRequest request() { return request; }
        public HttpServletResponse response() { return response; }

        public ProcessorHelper inputEncoding(String value) { this.inputEncoding = value; return this; }
        public ProcessorHelper outputEncoding(String value) { this.outputEncoding = value; return this; }

        private String contentAsText = null;
        public String contentAsText() {
            if(contentAsText == null) {
                try {
                    contentAsText = TextFile.read(request.getInputStream(), inputEncoding);
                } catch(Exception e) {
                    throw AsRuntimeEx.argument(e);
                }
            }
            return contentAsText;
        }

        public ProcessorHelper contentAsText(String text) {
            if(text == null) text = "";
            try {
                InputStream is = new ByteArrayInputStream(text.getBytes(outputEncoding));
                ServletOutputStream os = response.getOutputStream();
                int size = 0;
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) != -1) {
                    size = size + len;
                    os.write(buf, 0, len);
                }
                os.flush();
                response.setContentLength(size);
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
            return this;
        }

        public ProcessorHelper contentAsStream(InputStream is) {
            if(is == null) is = new ByteArrayInputStream(new byte[]{});
            try {
                ServletOutputStream os = response.getOutputStream();
                int size = 0;
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) != -1) {
                    size = size + len;
                    os.write(buf, 0, len);
                }
                os.flush();
                response.setContentLength(size);
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
            return this;
        }

    }
}
