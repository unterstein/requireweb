package neo4j.models.require;

import helper.ViewEnumModel;

import java.io.Serializable;

public enum ProjectState implements Serializable {
  IN_PLANNING, //
  IN_COMMISSION, //
  IN_DEVELOPMENT, //
  IN_TESTING, //
  IN_DEPLOYMENT, //
  ;

  public static ViewEnumModel enumModel() {
    ViewEnumModel result = new ViewEnumModel();
    for (ProjectState value : values()) {
      result.keys.add(value.name());
      result.values.put(value.name(), value.name());
    }
    return result;
  }
}
