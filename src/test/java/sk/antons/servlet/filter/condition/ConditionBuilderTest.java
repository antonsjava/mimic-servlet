/*
 * Copyright 2018 Anton Straka
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
package sk.antons.servlet.filter.condition;


import sk.antons.servlet.mimic.condition.ConditionBuilder;
import sk.antons.servlet.mimic.condition.StringCondition;
import sk.antons.servlet.mimic.condition.NamedCondition;
import sk.antons.servlet.mimic.condition.Condition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class ConditionBuilderTest {
	private static Logger log = Logger.getLogger(ConditionBuilderTest.class.getName());


    @Test
	public void parseTest() throws Exception {
        Condition<String> conditon = ConditionBuilder.instance(String.class)
            .add(StringCondition.instance(StringCondition.Operation.STARTS_WITH, "po", s -> s))
            .and()
            .lb()
                .add(s -> s.length() > 1000)
                .or()
                .add(s -> s.length() < 10)
            .rb()
            .condition();
        System.out.println(" ---- " + conditon);
        Assert.assertNotNull(conditon);
        Assert.assertTrue(conditon.check("pokus"));
        Assert.assertFalse(conditon.check("pokuspokus"));
    }

    @Test
	public void rebalancetest() throws Exception {
        final List<Integer> order = new ArrayList<>();
        Condition<String> conditon = ConditionBuilder.instance(String.class)
            .add(NamedCondition.instance(s -> {order.add(1); return true;}, "111"))
            .and().add(NamedCondition.instance(s -> {order.add(2); return true;}, "222"))
            .and().add(NamedCondition.instance(s -> {order.add(3); return true;}, "333"))
            .and().add(NamedCondition.instance(s -> {order.add(4); return true;}, "444"))
            .condition();
        System.out.println(" rebalancetest: " + conditon);
        Assert.assertNotNull(conditon);
        Assert.assertTrue(conditon.check("pokuspokus"));
        System.out.println(" rebalancetest result: " + order);
        Assert.assertEquals(1, order.get(0).intValue());
        Assert.assertEquals(2, order.get(1).byteValue());
        Assert.assertEquals(3, order.get(2).intValue());
    }

}
