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
package sk.antons.servlet.mimic;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import sk.antons.jaul.util.AsRuntimeEx;
import sk.antons.servlet.mimic.builder.MimicServletBuilder;
import sk.antons.servlet.mimic.builder.ProcessorBuilder;
import sk.antons.servlet.util.HttpServletRequestWrapper;

/**
 * Simple servlet for providing some dummy static content.
 * Useful for making som temporal mock services.
 * @author antons
 */
public class MimicServlet extends HttpServlet {

    private List<MimicSelector> selectors = new ArrayList<>();

    public static MimicServlet instance() { return new MimicServlet(); }
    public MimicServlet selector(MimicSelector selector) { this.selectors.add(selector); return this; }


    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {

            req = new HttpServletRequestWrapper(req);

            boolean something = false;
            for(MimicSelector selector : selectors) {
                if(selector.condition().check(req)) {
                    boolean rv = selector.processor().test(req, res);
                    if(rv) {
                        something = true;
                        break;
                    }
                }
            }

            if(!something) {
                res.setStatus(404);
                res.getOutputStream().print("unknown mimic request. path: " + req.getRequestURI());
            }

        } catch(Exception e) {
            throw AsRuntimeEx.state(e);
        } finally {
            res.getOutputStream().flush();
            //res.getOutputStream().close();
        }

    }

    /**
     * Builder for mimic servlet
     * @return builder
     */
    public static MimicServletBuilder builder() { return MimicServletBuilder.instance(); }
    /**
     * Builder for mimic servlet
     * @return builder
     */
    public static ProcessorBuilder processor() { return ProcessorBuilder.instance(); }


    /**
     * Filter configuration info
     * @return configuration info
     */
    public String configurationInfo() {
        StringBuilder sb = new StringBuilder();
        for(MimicSelector selector : selectors) {
            sb.append(selector.configurationInfo());
        }
        return sb.toString();
    }
}
