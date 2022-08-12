package pers.shezm.calcite.schema;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */

public class CustomTable extends AbstractTable {

  // <columName, CustomColumn>
  private Map<String, CustomColumn> detail = new HashMap<String, CustomColumn>();

  public CustomTable(String tableName, List<CsvColumn> columnList) {
    for (CsvColumn column : columnList) {
      SqlTypeName dataType = DataTypeConverter.configType2SqlType(column.getColumnType());
      if (dataType == null) {
        throw new RuntimeException(
            String.format("convert column:%s columnType: %s to sqlTypeName error",
                column.getColumnName(), column.getColumnType()));
      }
      detail.put(column.getColumnName(),
          new CustomColumn(tableName, column.getColumnName(), dataType));
    }
  }

  public static RelDataType createFieldType(RelDataTypeFactory typeFactory, SqlTypeName typeName,
      boolean isNullAble) {
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(typeName),
        isNullAble);
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    RelDataTypeFactory.Builder builder = typeFactory.builder();
    for (CustomColumn col : detail.values()) {
      builder.add(col.columnName, createFieldType(typeFactory, col.dataType, true));
    }
    return builder.build();
  }
}
