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
package sk.antons.servlet.mimic.condition;

import java.util.function.Predicate;

/**
 *
 * @author antons
 */
public class AnyCondition<T> implements Condition<T> {

    Predicate<T> resolver;
    String name;

    public AnyCondition(Predicate<T> resolver, String name) {
        this.resolver = resolver;
        this.name = name;
    }

    public static <W> AnyCondition<W> instance(Predicate<W> resolver) { return new AnyCondition(resolver, "??"); }
    public static <W> AnyCondition<W> instance(Predicate<W> resolver, String name) { return new AnyCondition(resolver, name); }

    @Override
    public boolean check(T request) {
        return resolver.test(request);
    }

    @Override
    public String toString() {
        return "( " + name + " )";
    }

}
