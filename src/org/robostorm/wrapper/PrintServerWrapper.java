package org.robostorm.wrapper;

import org.json.simple.JSONObject;

public class PrintServerWrapper {

    JSONObject json;
    String status;
    String action;
    Integer code;

    public PrintServerWrapper(JSONObject json) {
        this.json = json;
        status = (String) json.get("status");
        if(action != null && !action.equals(""))
            action = (String) json.get("action");
        else
            action = "No Action";
        code = (Integer) json.get("code");
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getStatus() {
        return status;
    }

    public String getAction() {
        return action;
    }

    public Integer getCode() {
        return code;
    }
}
