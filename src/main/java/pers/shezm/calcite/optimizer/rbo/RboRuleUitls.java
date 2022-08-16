package pers.shezm.calcite.optimizer.rbo;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.rules.FilterAggregateTransposeRule;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.FilterProjectTransposeRule;
import org.apache.calcite.rel.rules.ProjectJoinTransposeRule;
import org.apache.calcite.rel.rules.ProjectMergeRule;
import org.apache.calcite.rel.rules.ProjectRemoveRule;
import org.apache.calcite.rel.rules.ProjectSortTransposeRule;
import org.apache.calcite.util.ImmutableNullableList;

import java.util.List;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */
public class RboRuleUitls {

  /**
   * rbo rules
   *
   * @return
   */
  public static List<RelOptRule> initDefaultRules() {
    List<RelOptRule> defaultRuleList = ImmutableNullableList.of(
        /* Rule that pushes predicates from a Filter into the Join below them. */
        FilterJoinRule.FILTER_ON_JOIN,
        /* Rule that pushes predicates in a Join into the inputs to the join. */
        FilterJoinRule.JOIN,
        /* Planner rule that pushes a Filter past a Project */
        FilterProjectTransposeRule.INSTANCE,
        /* Planner rule that pushes a Filter past a Aggregate */
        FilterAggregateTransposeRule.INSTANCE,
        /* rule that pushes a Project past a Join */
        ProjectJoinTransposeRule.INSTANCE,
        ProjectRemoveRule.INSTANCE,
        /* Planner rule that pushes a Project past a Sort */
        ProjectSortTransposeRule.INSTANCE, ProjectMergeRule.INSTANCE);
    return defaultRuleList;
  }
}