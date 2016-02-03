package io.finin.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import io.finin.Plan;
import io.finin.Plan.SelectorStep;
import io.finin.RemoteDataAccessors;
import io.finin.Session;

public class PlanTest {

  @Test
  public void testSrcDefaults() {
    Plan plan = RemoteDataAccessors.plan("src?url=http://src1.com&type=type1");
    SelectorStep step = plan.selectors().get(0);
    assertEquals(step.mandatory, true);
    assertEquals(step.order, -1);
    assertEquals(step.other.isEmpty(), true);

  }

  @Test
  public void testSnkDefaults() {
    Plan plan = RemoteDataAccessors.plan("src?url=http://src1.com&type=type1");

  }

  @Test
  public void testOrder() {
    Plan plan;
    Random random = new Random();
    for (int i = 0; i < 1000; i++) {
      plan = RemoteDataAccessors//
          .plan( //
              IntStream//
                  .generate(() -> random.nextInt(1000))//
                  .distinct()//
                  .limit(100)//
                  .mapToObj(j -> "src?url=http://src1.com&type=type1&order=" + j + "&mandatory=true") //
                  .toArray(j -> new String[j]));

      int order = Integer.MIN_VALUE;
      for (SelectorStep step : plan.selectors()) {
        assertTrue(order < step.order);
        order = step.order;
      }
    }
  }

  @Test
  public void microBench() {
    System.out.println("Running");
    long duration = System.nanoTime();
    int runs = 1000000;
    for (int i = 0; i < runs; i += 2) {
      RemoteDataAccessors.plan( //
          "src?url=http://src1.com&type=type1&order=1&mandatory=true", //
          "src?url=http://src1.com&type=type2&order=2&mandatory=true", //
          "snk?url=http://snk1.com&type=type2&order=1&mandatory=true" //
      );
    }
    duration = System.nanoTime() - duration;
    System.out.println("took " + (duration / 1000000) + " ms to run " + runs + " times ( " + (duration / 1000000.0 / runs) + " ms in average)");
  }

  public void testOrderWithParallelism() {

    Plan plan = RemoteDataAccessors.plan(new String[] { //
        "src?url=http://src1.com&type=type1&order=1&mandatory=true", //
        "src?url=http://src2.com&type=type2&order=5&mandatory=true", //
        "src?url=http://src3.com&type=type2&order=0&mandatory=true", //
        "src?url=http://src4.com&type=type2&order=8&mandatory=true", //
        "snk?url=http://snk1.com&type=type1&order=1&mandatory=true", //
        "snk?url=http://snk2.com&type=type2&order=2&mandatory=true", //
    });

    // Selecting
    List<Session> sessions = plan.selectors().stream().map(s -> s.selector.fetch("id")).collect(Collectors.toList());

    // Merging
    Map<String, String> map = new HashMap<>();
    for (Session s : sessions) {
      map.putAll(s.all());
    }
    Session merged = new Session(map);

    // Updating
    plan.updaters().stream().forEach(u -> u.updater.push(merged));

  }
}
