package pers.shezm.calcite.visitor.sqlnode;

import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/15
 * @Description sqlNode的visitor模式，SqlVistor
 */
public class SqlVistorCsv extends SqlShuttle {

  @Override
  public SqlNode visit(SqlLiteral literal) {
    System.out.println(literal);
    return literal;
  }

  @Override
  public SqlNode visit(SqlIdentifier id) {
    return id;
  }

  @Override
  public SqlNode visit(SqlDataTypeSpec type) {
    return type;
  }

  @Override
  public SqlNode visit(SqlDynamicParam param) {
    return param;
  }

  @Override
  public SqlNode visit(SqlIntervalQualifier intervalQualifier) {
    return intervalQualifier;
  }

}