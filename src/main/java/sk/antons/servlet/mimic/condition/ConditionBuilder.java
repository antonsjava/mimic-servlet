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

import java.util.Stack;

/**
 *
 * @author antons
 */
public class ConditionBuilder<C> {

    Stack<StackEntry> stack = new Stack<>();
    StringBuilder path = new StringBuilder();

    private ConditionBuilder() {
        stack.push(StackEntry.of(Type.Full));
    }

    public static <T> ConditionBuilder<T> instance(Class<T> clazz) { return new ConditionBuilder(); }

    public Condition<C> condition() {
        if(stack.size() != 1) throw new IllegalStateException("bad condition format at " + path);
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        return rebalance((Condition<C>)stack.peek().condition);
    }

    // mahke sure that combinations od and/or conditions will be evaluated from left to right
    private static <X> Condition<X> rebalance(Condition<X> condition) {
        if(condition == null) {
            return condition;
        } else if(condition instanceof NotCondition ) {
            NotCondition<X> cnd = (NotCondition<X>)condition;
            return NotCondition.instance(rebalance(cnd.right));
        } else if(condition instanceof OrCondition ) {
            OrCondition<X> cnd = (OrCondition<X>)condition;
            if(cnd.left instanceof OrCondition) {
                OrCondition<X> cndleft = (OrCondition<X>)cnd.left;
                return rebalance(OrCondition.instance(cndleft.left, OrCondition.instance(cndleft.right, cnd.right)));
            } else {
                return OrCondition.instance(rebalance(cnd.left), rebalance(cnd.right));
            }
        } else if(condition instanceof AndCondition ) {
            AndCondition<X> cnd = (AndCondition<X>)condition;
            if(cnd.left instanceof AndCondition) {
                AndCondition<X> cndleft = (AndCondition<X>)cnd.left;
                return rebalance(AndCondition.instance(cndleft.left, AndCondition.instance(cndleft.right, cnd.right)));
            } else {
                return AndCondition.instance(rebalance(cnd.left), rebalance(cnd.right));
            }
        } else {
            return condition;
        }
    }

    public ConditionBuilder<C> add(Condition<C> condition) {
        path.append(' ').append(condition);
        if(stack.peek().condition != null) throw new IllegalStateException("bad condition format at " + path);
        stack.peek().condition = condition;
        reduceStack(false);
        return this;
    }

    public ConditionBuilder<C> not() {
        path.append(" not");
        if(stack.peek().condition != null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.Not));
        return this;
    }

    public ConditionBuilder<C> and() {
        path.append(" and");
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.And));
        return this;
    }

    public ConditionBuilder<C> or() {
        path.append(" or");
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.Or));
        return this;
    }

    public ConditionBuilder<C> lb() {
        path.append(" (");
        stack.push(StackEntry.of(Type.Full));
        return this;
    }

    public ConditionBuilder<C> rb() {
        path.append(" )");
        if(stack.peek().type != Type.Full) throw new IllegalStateException("bad condition format at " + path);
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        reduceStack(true);
        return this;
    }

    private void reduceStack(boolean force) {
        if(stack.size() == 1) return;
        if(stack.peek().condition == null) return;
        StackEntry top = stack.pop();
        if(top.type == Type.Not) {
            stack.peek().condition = NotCondition.instance(top.condition);
            reduceStack(false);
        } else if(top.type == Type.And) {
            stack.peek().condition = AndCondition.instance((Condition<C>)stack.peek().condition, (Condition<C>)top.condition);
            reduceStack(false);
        } else if(top.type == Type.Or) {
            stack.peek().condition = OrCondition.instance((Condition<C>)stack.peek().condition, (Condition<C>)top.condition);
            reduceStack(false);
        } else if(top.type == Type.Full) {
            if(force) {
                if(stack.peek().condition == null) {
                    stack.peek().condition = top.condition;
                    reduceStack(false);
                }
            } else {
                stack.push(top);
            }
        } else {
            throw new IllegalStateException("bad condition format at " + path);
        }
    }

    private static enum Type {
        Full, Not, And, Or;
    }

    private static class StackEntry {
        Condition<?> condition = null;
        Type type = null;

        public static StackEntry of(Type type) {
            StackEntry entry = new StackEntry();
            entry.type = type;
            return entry;
        }

        @Override
        public String toString() {
            return type + "{" + condition + '}';
        }

    }

}
