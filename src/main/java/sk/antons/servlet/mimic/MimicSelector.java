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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.BiPredicate;
import sk.antons.servlet.mimic.condition.Condition;

/**
 * Configuration helper class
 * @author antons
 */
public class MimicSelector {

    private BiPredicate<HttpServletRequest, HttpServletResponse> processor;
    private Condition<HttpServletRequest> condition;

    public static MimicSelector instance() { return new MimicSelector(); }
    public BiPredicate<HttpServletRequest, HttpServletResponse> processor() { return processor; }
    public MimicSelector processor(BiPredicate<HttpServletRequest, HttpServletResponse> value) { this.processor = value; return this; }
    public Condition<HttpServletRequest> condition() { return condition; }
    public MimicSelector condition(Condition<HttpServletRequest> value) { this.condition = value; return this; }

    public String configurationInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n---- case -------");
        if(condition != null) sb.append("\n  when request: ").append(condition);
        if(processor != null) sb.append("\n  do: ").append(processor);
        return sb.toString();
    }
}
