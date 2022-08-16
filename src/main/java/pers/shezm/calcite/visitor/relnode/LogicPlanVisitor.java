package pers.shezm.calcite.visitor.relnode;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalMatch;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/15
 * @Description 关系表表达式遍历
 */
public class LogicPlanVisitor extends RelShuttleImpl {

  @Override
  public RelNode visit(LogicalAggregate aggregate) {
    return this.visitChild(aggregate, 0, aggregate.getInput());
  }

  @Override
  public RelNode visit(LogicalMatch match) {
    return this.visitChild(match, 0, match.getInput());
  }

  @Override
  public RelNode visit(TableScan scan) {
    return scan;
  }

}