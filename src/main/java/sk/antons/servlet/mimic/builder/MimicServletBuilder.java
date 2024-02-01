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

import sk.antons.servlet.mimic.MimicServlet;

/**
 * Builder for mimic servlet.
 * @author antons
 */
public class MimicServletBuilder {

    MimicServlet servlet = MimicServlet.instance();
    String encoding = "utf-8";

    /**
     * New instance of builder.
     * @return
     */
    public static MimicServletBuilder instance() { return new MimicServletBuilder(); }
    /**
     * Builder for next servlet case configuration
     * @return
     */
    public MimicServletBuilder encoding(String value) { this.encoding = value; return this; }
    /**
     * Builder for next servlet case configuration
     * @return
     */
    public SelectorBuilder<MimicServletBuilder> inCase() { return SelectorBuilder.instance(encoding, this, f -> this.servlet.selector(f)); }

    /**
     * Creates servlet
     * @return
     */
    public MimicServlet build() { return servlet; }


}
