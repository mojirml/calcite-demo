package pers.shezm.calcite.test;

import org.apache.calcite.plan.RelOptUtil;
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
    String sql = "select * from TEST01 where TEST01.NAME1='hello'";
    String[] filePaths = {"/TEST01.csv", "/TEST02.csv"};
    for (int i = 0; i < 2; i++) {
      URL url = Test7.class.getResource(filePaths[i]);
      String str = URLDecoder.decode(url.toString(), "UTF-8");
      str = str.replace("file:", "");
      System.out.println(str);
      filePaths[i] = str;
    }
    FrameworkConfig frameworkConfig = calciteBladeUtils.getFrameworkConfig(filePaths);
    System.out.println(frameworkConfig.getDefaultSchema());
    Planner planner = Frameworks.getPlanner(frameworkConfig);
    RelRoot root = null;
    try {
      SqlNode parse1 = planner.parse(sql);
      System.out.println(parse1);
      SqlNode validate = planner.validate(parse1);
      root = planner.rel(validate);
      RelNode rel = root.rel;
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(RelOptUtil.toString(root.rel, SqlExplainLevel.ALL_ATTRIBUTES));
  }

  /**
   * RBO 优化
   */
  private void rbo() {

  }

  /**
   * CBO优化
   */
  private void cbo() {

  }

}