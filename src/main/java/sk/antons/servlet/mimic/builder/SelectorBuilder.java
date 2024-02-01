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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import sk.antons.servlet.mimic.MimicSelector;
import sk.antons.servlet.mimic.condition.Condition;

/**
 * Builder for one cache of log filter usage. It defines combination of
 * {@code <li>} request condition (mandatory)
 * {@code <li>} response condition (optional) (if it is defined no request start message is printed)
 * {@code <li>} filter configuration (optional) By default each case inherits default configuration.
 *              You only set what you want chage from defaul.
 *
 * @author antons
 */
public class SelectorBuilder<C> {
    C backReference;
    Consumer<MimicSelector> consumer;
    String encoding;

    private Condition<HttpServletRequest> condition;

    private SelectorBuilder(String encoding, C back, Consumer<MimicSelector> consumer) {
        this.backReference = back;
        this.consumer = consumer;
        this.encoding = encoding;
    }
    /**
     * Instance of builder
     * @param back parent
     * @param consumer target of case combination
     * @return instance
     */
    public static <T> SelectorBuilder instance(String encoding, T back, Consumer<MimicSelector> consumer) { return new SelectorBuilder(encoding, back, consumer); }

    /**
     * Define case combination
     * @return parent
     */
    public C process(BiPredicate<HttpServletRequest, HttpServletResponse> processor) {
        if(condition == null) throw new IllegalStateException("no request condition");
        if(processor == null) throw new IllegalStateException("no request processor");
        if(consumer != null) consumer.accept(MimicSelector.instance().condition(condition).processor(processor));
        return backReference;
    }


    /**
     * Define request condition
     * @return condition builder
     */
    public RequestConditionBuilder<SelectorBuilder<C>> when() { return RequestConditionBuilder.instance(encoding, this, f -> this.condition = f); }



}
