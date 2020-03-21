package com.demo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder({"error_code", "error_message", "data"})
public class DataResponse {
    @JsonProperty("error_code")
    private final String errorCode;
    @JsonProperty("error_message")
    private final String errorMessage;
    @JsonProperty("data")
    private Object data;
    private DataResponse.DataType dataType = DataResponse.DataType.NORMAL;


    public DataResponse(String error, String message) {
        this.errorCode = error;
        this.errorMessage = message;
    }

    public DataResponse(String error, String message, String data) {
        this.errorCode = error;
        this.errorMessage = message;
        this.data = data;
    }

    public DataResponse(Object data) {
        this.errorCode = ResponseCode.SUCCESSFUL;
        this.errorMessage = "";
        this.data = data;
    }

    public DataResponse(String errorCode, String errorMessage, Object data, DataType dataType) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.dataType = dataType;
    }

    @JsonIgnore
    public String getError() {
        return this.errorCode;
    }

    @JsonIgnore
    public String getMessage() {
        return this.errorMessage;
    }

    @JsonIgnore
    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.setData(data, DataResponse.DataType.NORMAL);
    }

    public void setData(Object data, DataResponse.DataType dataType) {
        this.data = data;
        this.dataType = dataType;
    }

    @JsonIgnore
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return DataResponse.toJsonString(this);
    }

    public static final DataResponse SUCCESSFUL = new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL");
    public static final DataResponse FAILED = new DataResponse(ResponseCode.FAILED, "FAILED");

    public static String toJsonString(DataResponse dataResponse) {
        try {
            ObjectMapper mapper = null;

            if (dataResponse.getDataType() == DataResponse.DataType.JSON_STR) {
                return "{\"error_code\":\"" + dataResponse.getError() + "\",\"error_message\":\"" + dataResponse.getMessage() + "\", \"data\":" + dataResponse.getData() + "}";
            }
            // DATA_TYPE_NORMAL
            return mapper.writeValueAsString(dataResponse);
        } catch (Exception e) {
        }

        return "";
    }

    public enum DataType {
        NORMAL, JSON_STR, UNSURE
    }

}
