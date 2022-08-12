package pers.shezm.calcite.test;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import pers.shezm.calcite.optimizer.converter.CSVTableScanConverter;
import pers.shezm.calcite.optimizer.cost.DefaultRelMetadataProvider;
import pers.shezm.calcite.optimizer.rbo.RboRuleUitls;
import pers.shezm.calcite.utils.CalciteBladeUtils;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */
public class Test7 {

  public static void main(String[] args) throws UnsupportedEncodingException {
    CalciteBladeUtils calciteBladeUtils = new CalciteBladeUtils();
    // ID1:VARCHAR,NAME3:VARCHAR,NAME4:VARCHAR
    String sql = "select * from TEST01,TEST02 where TEST01.NAME1='hello' and TEST01.ID = TEST02.ID1 and __sys_convert_time(TEST02.NAME4,'yyyymm') = 100 ";
    String[] filePaths = {"/TEST01.csv", "/TEST02.csv"};
    for (int i = 0; i < 2; i++) {
      URL url = Test7.class.getResource(filePaths[i]);
      String str = URLDecoder.decode(url.toString(), "UTF-8");
      str = str.replace("file:", "");
      System.out.println("schema path: " + str);
      filePaths[i] = str;
    }
    FrameworkConfig frameworkConfig = calciteBladeUtils.getFrameworkConfig(filePaths);
    Planner planner = Frameworks.getPlanner(frameworkConfig);
    RelRoot root = null;
    try {
      SqlNode parse1 = planner.parse(sql);
      System.out.println("SqlNode is : " + parse1 + "\n");
      SqlNode validate = planner.validate(parse1);
      root = planner.rel(validate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(RelOptUtil.toString(root.rel, SqlExplainLevel.ALL_ATTRIBUTES));
    rbo(root.rel);
//    cbo(root.rel);
  }


  /**
   * RBO 优化
   */
  private static void rbo(RelNode rel) {
    System.out.println("rbo : ");
    RelNode optimizedLogicalPlan = getOptimizedLogicalPlan(rel, RboRuleUitls.initDefaultRules());
    System.out.println(RelOptUtil.toString(optimizedLogicalPlan, SqlExplainLevel.ALL_ATTRIBUTES));
    cbo(optimizedLogicalPlan);
  }

  private static RelNode getOptimizedLogicalPlan(RelNode project, List<RelOptRule> rules) {
    try {
      HepProgramBuilder builder = new HepProgramBuilder();
      for (RelOptRule rule : rules) {
        builder.addRuleInstance(rule);
      }
      HepPlanner hepPlanner = new HepPlanner(builder.build());
      hepPlanner.setRoot(project);
      return hepPlanner.findBestExp();
    } finally {

    }
  }

  /**
   * CBO优化
   */
  private static void cbo(RelNode rel) {
    System.out.println("cbo : ");
    DefaultRelMetadataProvider defaultRelMetadataProvider = new DefaultRelMetadataProvider();
    RelNode relNode = Test5.hepPlan(rel, false, defaultRelMetadataProvider.getMetadataProvider(), null, null, CSVTableScanConverter.INSTANCE);
    System.out.println(RelOptUtil.toString(relNode, SqlExplainLevel.ALL_ATTRIBUTES));

  }

}