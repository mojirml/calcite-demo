package pers.shezm.calcite.schema;

import org.apache.calcite.sql.type.SqlTypeName;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */

public class CustomColumn {

  public CustomColumn(String tableName, String columnName, SqlTypeName dataType) {
    this.tableName = tableName;
    this.columnName = columnName;
    this.dataType = dataType;
  }

  public String tableName;
  public String columnName;
  public int columnIndex;
  public SqlTypeName dataType;
  public int storageIndex;
}