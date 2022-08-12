package pers.shezm.calcite.schema;

import lombok.Data;

/**
 * @author yannbai
 * @group blade_db
 * @date 2022/8/12
 * @Description
 */
@Data
public class CsvColumn {
  private String columnName;
  private int index;
  private String columnType;
}