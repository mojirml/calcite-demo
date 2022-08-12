package pers.shezm.calcite.utils;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import pers.shezm.calcite.schema.CsvColumn;
import pers.shezm.calcite.schema.CustomTable;
import pers.shezm.calcite.schema.Functions;
import sun.misc.Contended;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */
public class CalciteBladeUtils {

  // 行缓存，进行行填充
  @Contended
  private volatile SchemaPlus schemaPlus;

  public CalciteBladeUtils() {
    this.schemaPlus = Frameworks.createRootSchema(true);
  }

  /**
   * step 1: 初始化表的列信息,这里可以自定义不同的获取schema方式
   *
   * @param filePaths
   */
  private void initScahema(String... filePaths) {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    addFunctions(rootSchema);
    for (int i = 0; i < filePaths.length; i++) {
      String filePath = filePaths[i];
      List<CsvColumn> columns = new ArrayList<>();
      String tableName = null;
      try {
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        tableName = file.getName();
        String line = reader.readLine();
        List<String> lines = Lists.newArrayList(line.split(","));
        AtomicInteger indx = new AtomicInteger();
        lines.forEach(column -> {
          String name = column.split(":")[0];
          String type = column.split(":")[1];
          CsvColumn csvColumn = new CsvColumn();
          csvColumn.setIndex(indx.getAndIncrement());
          csvColumn.setColumnName(name);
          csvColumn.setColumnType(type);
          columns.add(csvColumn);
        });
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (tableName == null) {
        throw new RuntimeException("not found table,table path is:" + filePath);
      }
      String[] split = tableName.split("\\.");
      CustomTable table = new CustomTable(split[0], columns);
      rootSchema.add(split[0], table);
      System.out.println(split[0]);
      System.out.println(columns);
    }
    this.schemaPlus = rootSchema;
  }

  /**
   * 创建自有函数，以便解析正常
   *
   * @param rootSchema
   */
  public static void addFunctions(SchemaPlus rootSchema) {
    rootSchema.add("__sys_convert_time",
        ScalarFunctionImpl.create(Functions.class, "sys_convert_time"));
    rootSchema.add("_sys_in", ScalarFunctionImpl.create(Functions.class, "sys_in"));
    rootSchema.add("unix_timestamp",
        ScalarFunctionImpl.create(Functions.class, "unix_timestamp"));
  }

  // step 2:
  public FrameworkConfig getFrameworkConfig(String... filePath) {

    initScahema(filePath);

    // 初始化解析sql的配置：
    // 1.词法规则走java
    // 2.in查询不要替换为OR查询

    // 第一步，构建这个SqlParserConfig，给parser使用
    SqlParser.ConfigBuilder scb = SqlParser.configBuilder();
    scb.setParserFactory(SqlParserImpl.FACTORY);
    scb.setLex(Lex.JAVA);
    scb.setConformance(SqlConformanceEnum.MYSQL_5);
    SqlParser.Config scfg = scb.build();

    // field in(1,2,3,4,5,6)这种查询不展开为or
    SqlToRelConverter.Config s2rcfg = SqlToRelConverter.configBuilder()
        .withInSubQueryThreshold(Integer.MAX_VALUE)
        .build();

    Frameworks.ConfigBuilder fcb = Frameworks.newConfigBuilder();
    fcb.parserConfig(scfg);
    fcb.defaultSchema(this.schemaPlus);
    fcb.traitDefs(ConventionTraitDef.INSTANCE, RelDistributionTraitDef.INSTANCE);
    fcb.operatorTable(SqlStdOperatorTable.instance());
    fcb.sqlToRelConverterConfig(s2rcfg);
    FrameworkConfig tmpCfg = fcb.build();

    Properties properties = new Properties();
    properties.put(CalciteConnectionProperty.TYPE_SYSTEM.camelName().toUpperCase(),
        RelDataTypeSystemImpl.DEFAULT);
    properties.put(CalciteConnectionProperty.CASE_SENSITIVE.camelName().toUpperCase(),
        Boolean.toString(scfg.caseSensitive()));
    properties.put(CalciteConnectionProperty.CONFORMANCE.camelName().toUpperCase(),
        scfg.conformance().toString());
    CalciteConnectionConfig calciteConnectionConfig = new CalciteConnectionConfigImpl(
        properties);

    CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
        CalciteSchema.from(this.schemaPlus),
        CalciteSchema.from(this.schemaPlus).path(null),
        new SqlTypeFactoryImpl(RelDataTypeSystemImpl.DEFAULT),
        calciteConnectionConfig);

    SqlOperatorTable sqlOperatorTable = ChainedSqlOperatorTable.of(tmpCfg.getOperatorTable(),
        calciteCatalogReader);

    fcb.operatorTable(sqlOperatorTable);

    fcb.context(Contexts.of(calciteConnectionConfig));
    return fcb.build();
  }

}