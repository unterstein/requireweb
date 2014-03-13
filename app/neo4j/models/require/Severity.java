package neo4j.models.require;

import helper.ViewEnumModel;
import java.io.Serializable;

public enum Severity implements Serializable {
  LOW, //
  MEDIUM, //
  HIGH, //
  CRITICAL, //
  ;

  public static ViewEnumModel enumModel() {
    helper.ViewEnumModel result = new ViewEnumModel();
    for (Severity value : values()) {
      result.keys.add(value.name());
      result.values.put(value.name(), value.name());
    }
    return result;
  }
}
