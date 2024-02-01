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

/**
 *
 * @author antons
 */
public class OrCondition<T> implements Condition<T> {

    Condition<T> left;
    Condition<T> right;

    public OrCondition(Condition<T> left, Condition<T> right) {
        this.left = left;
        this.right = right;
    }

    public static <W> OrCondition<W> instance(Condition<W> left, Condition<W> right) { return new OrCondition(left, right); }

    @Override
    public boolean check(T request) {
        return left.check(request) || right.check(request);
    }

    @Override
    public String toString() {
        return "( " + left + " OR " + right + " )";
    }
}
