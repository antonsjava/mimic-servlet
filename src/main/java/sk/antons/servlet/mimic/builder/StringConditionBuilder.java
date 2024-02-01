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

import java.util.function.Consumer;
import java.util.function.Function;
import sk.antons.servlet.mimic.condition.Condition;
import sk.antons.servlet.mimic.condition.StringCondition;
import sk.antons.servlet.mimic.condition.StringCondition.Operation;

/**
 *
 * @author antons
 */
public class StringConditionBuilder<C, T> {
    C backReference;
    Function<T, String> resolver;
    Consumer<Condition<T>> consumer;
    String name;

    private StringConditionBuilder(C back, Function<T, String> resolver, Consumer<Condition<T>> consumer, String name) {
        this.backReference = back;
        this.resolver = resolver;
        this.consumer = consumer;
        this.name = name;
    }
    public static <V, W> StringConditionBuilder<V, W> instance(V back, Function<W, String> resolver, Consumer<Condition<W>> consumer, String name) { return new StringConditionBuilder(back, resolver, consumer, name); }

    private C contition(Operation op, String param) {
        if(consumer != null) consumer.accept(StringCondition.instance(op, param, resolver, name));
        return backReference;
    }

    public C contains(String param) { return contition(Operation.CONTAINS, param); }
    public C startsWith(String param) { return contition(Operation.STARTS_WITH, param); }
    public C endsWitn(String param) { return contition(Operation.ENDS_WITH, param); }
    public C equals(String param) { return contition(Operation.EQUALS, param); }
    public C equalsIgnoreCase(String param) { return contition(Operation.EQUALS_IGNORE_CASE, param); }
    public C exists() { return contition(Operation.EXISTS, null); }
    public C match(String param) { return contition(Operation.MATCH, param); }
    public C regexp(String param) { return contition(Operation.REGEXP, param); }


}
