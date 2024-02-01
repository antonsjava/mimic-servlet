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

import java.util.function.Function;
import java.util.regex.Pattern;
import sk.antons.web.path.PathMatcher;

/**
 *
 * @author antons
 */
public class StringCondition<T> implements Condition<T> {

    Function<T, String> resolver;
    String param;
    Operation operation;
    String name;

    public StringCondition(Operation operation, String param, Function<T, String> resolver, String name) {
        this.operation = operation;
        this.param = param;
        this.resolver = resolver;
        this.name = name;
    }

    public static <W> StringCondition<W> instance(Operation operation, String param, Function<W, String> resolver) { return new StringCondition(operation, param, resolver, "?"); }
    public static <W> StringCondition<W> instance(Operation operation, String param, Function<W, String> resolver, String name) { return new StringCondition(operation, param, resolver, name); }

    @Override
    public boolean check(T request) {
        String value = resolver.apply(request);
        return check(value);
    }

    private PathMatcher pathMatcher = null;
    private Pattern regexpPattern = null;
    private boolean check(String value) {
        if(operation == Operation.EXISTS) {
            return (value != null) && (value.length() != 0);
        } else if(operation == Operation.EQUALS) {
            return (value != null) && (value.equals(param));
        } else if(operation == Operation.EQUALS_IGNORE_CASE) {
            return (value != null) && (value.equalsIgnoreCase(param));
        } else if(operation == Operation.STARTS_WITH) {
            return (value != null) && (value.startsWith(param));
        } else if(operation == Operation.ENDS_WITH) {
            return (value != null) && (value.endsWith(param));
        } else if(operation == Operation.CONTAINS) {
            return (value != null) && (value.contains(param));
        } else if(operation == Operation.MATCH) {
            if(pathMatcher == null) pathMatcher = PathMatcher.instance(param);
            return (value != null) && pathMatcher.match(value);
        } else if(operation == Operation.REGEXP) {
            if(regexpPattern == null) regexpPattern = Pattern.compile(param);
            return (value != null) && regexpPattern.matcher(value).matches();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + "->" + operation.displayName + "(" + (param == null?"":param) + ")";
    }

    public static enum Operation {
        EXISTS("exists"), EQUALS("eq"), STARTS_WITH("starts"), ENDS_WITH("ends")
        , CONTAINS("contains"), MATCH("match"), REGEXP("regexp")
        , EQUALS_IGNORE_CASE("eqic")
        ;
        private String displayName;
        private Operation(String displayName) { this.displayName = displayName; }
    }
}
