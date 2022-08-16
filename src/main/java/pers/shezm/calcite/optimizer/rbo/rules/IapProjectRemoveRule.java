package pers.shezm.calcite.optimizer.rbo.rules;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.hep.HepRelVertex;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.tools.RelBuilderFactory;

/**
 * 锋刃自定义project移除规则
 * 比标准的 ProjectRemoveRule 要严格一点，参考isTrivial，即
 * 连续的project，能移除的可以移除，否则至少保留一个project，不能全移完了
 */
public class IapProjectRemoveRule extends RelOptRule {

  public static final IapProjectRemoveRule INSTANCE = new IapProjectRemoveRule(
      RelFactories.LOGICAL_BUILDER);

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a ProjectRemoveRule.
   *
   * @param relBuilderFactory Builder for relational expressions
   */
  public IapProjectRemoveRule(RelBuilderFactory relBuilderFactory) {
    // Create a specialized operand to detect non-matches early. This keeps
    // the rule queue short.
    super(operandJ(Project.class, null, IapProjectRemoveRule::isTrivial, any()),
        relBuilderFactory, null);
  }

  public static boolean isTrivial(Project project) {
    boolean ret = RexUtil.isIdentity(project.getProjects(), project.getInput().getRowType());
    if (!ret) {
      return ret;
    }

    if (project.getInput() instanceof HepRelVertex) {
      String dig = ((HepRelVertex) project.getInput()).getCurrentRel().getDigest();
      return dig.startsWith("LogicalProject");
    }
    return ret;
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    Project project = call.rel(0);
    assert isTrivial(project);
    RelNode stripped = project.getInput();
    if (stripped instanceof Project) {
      // Rename columns of child projection if desired field names are given.
      Project childProject = (Project) stripped;
      stripped = childProject.copy(childProject.getTraitSet(),
          childProject.getInput(), childProject.getProjects(),
          project.getRowType());
    }
    RelNode child = call.getPlanner().register(stripped, project);
    call.transformTo(child);
  }
}