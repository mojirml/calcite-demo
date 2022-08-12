package pers.shezm.calcite.schema;

import org.apache.calcite.sql.type.SqlTypeName;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */

public class DataTypeConverter {

  public static SqlTypeName configType2SqlType(String configType) {
    if (configType.startsWith("set")) {
      return SqlTypeName.MULTISET;
    }
    if (configType.startsWith("array")) {
      return SqlTypeName.ARRAY;
    }
    switch (configType) {
    case "string":
      return SqlTypeName.VARCHAR;
    case "int":
      return SqlTypeName.INTEGER;
    case "long":
      return SqlTypeName.BIGINT;
    case "float":
      return SqlTypeName.FLOAT;
    case "double":
      return SqlTypeName.DOUBLE;
    case "bool":
      return SqlTypeName.BOOLEAN;
    case "date":
      return SqlTypeName.INTEGER;
    case "VARCHAR":
      return SqlTypeName.VARCHAR;
    default:
      break;
    }
    return null;
  }

  public static DataType configType2DescType(String configType) {
    switch (configType) {
    case "string":
      return DataType.STRING;
    case "int":
      return DataType.INT;
    case "long":
      return DataType.LONG;
    case "float":
      return DataType.FLOAT;
    case "double":
      return DataType.DOUBLE;
    case "bool":
      return DataType.BOOLEAN;
    default:
      break;
    }
    return null;
  }

  public static DataType sqlType2DescType(SqlTypeName sqlType) {
    switch (sqlType) {
    case VARCHAR:
      return DataType.STRING;
    case INTEGER:
      return DataType.INT;
    case BIGINT:
      return DataType.LONG;
    case FLOAT:
      return DataType.FLOAT;
    case DOUBLE:
      return DataType.DOUBLE;
    case BOOLEAN:
      return DataType.BOOLEAN;
    case MULTISET:
      return DataType.SET;
    case ARRAY:
      return DataType.ARRAY;
    default:
      break;
    }
    return null;
  }
}
