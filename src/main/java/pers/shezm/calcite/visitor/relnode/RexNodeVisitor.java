package pers.shezm.calcite.visitor.relnode;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexPatternFieldRef;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexTableInputRef;

import java.util.List;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/15
 * @Description RexNode的遍历，也称为行表达式
 */
public class RexNodeVisitor extends RexShuttle {

  @Override
  public RexNode visitTableInputRef(RexTableInputRef ref) {
    return ref;
  }

  @Override
  public RexNode visitPatternFieldRef(RexPatternFieldRef fieldRef) {
    return fieldRef;
  }

  @Override
  public RexNode visitCall(RexCall call) {
    boolean[] update = new boolean[]{false};
    List<RexNode> clonedOperands = this.visitList(call.operands, (boolean[]) update);
    return update[0] ? call.clone(call.getType(), clonedOperands) : call;
  }
}