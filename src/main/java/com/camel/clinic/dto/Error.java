package com.camel.clinic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;

@Validated
public class Error {
    @JsonProperty("code")
    private String code = null;
    @JsonProperty("reason")
    private String reason = null;
    @JsonProperty("message")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String message = null;
    @JsonProperty("status")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String status = null;
    @JsonProperty("referenceError")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String referenceError = null;
    @JsonProperty("@baseType")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String _atBaseType = null;
    @JsonProperty("@schemaLocation")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String _atSchemaLocation = null;
    @JsonProperty("@type")
    @JsonInclude(Include.NON_ABSENT)
    @JsonSetter(
        nulls = Nulls.FAIL
    )
    private String _atType = null;

    public Error() {
    }

    public Error code(String code) {
        this.code = code;
        return this;
    }

    public @NotNull String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Error reason(String reason) {
        this.reason = reason;
        return this;
    }

    public @NotNull String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Error message(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error status(String status) {
        this.status = status;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Error referenceError(String referenceError) {
        this.referenceError = referenceError;
        return this;
    }

    public String getReferenceError() {
        return this.referenceError;
    }

    public void setReferenceError(String referenceError) {
        this.referenceError = referenceError;
    }

    public Error _atBaseType(String _atBaseType) {
        this._atBaseType = _atBaseType;
        return this;
    }

    public String getAtBaseType() {
        return this._atBaseType;
    }

    public void setAtBaseType(String _atBaseType) {
        this._atBaseType = _atBaseType;
    }

    public Error _atSchemaLocation(String _atSchemaLocation) {
        this._atSchemaLocation = _atSchemaLocation;
        return this;
    }

    public String getAtSchemaLocation() {
        return this._atSchemaLocation;
    }

    public void setAtSchemaLocation(String _atSchemaLocation) {
        this._atSchemaLocation = _atSchemaLocation;
    }

    public Error _atType(String _atType) {
        this._atType = _atType;
        return this;
    }

    public String getAtType() {
        return this._atType;
    }

    public void setAtType(String _atType) {
        this._atType = _atType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Error error = (Error)o;
            return Objects.equals(this.code, error.code) && Objects.equals(this.reason, error.reason) && Objects.equals(this.message, error.message) && Objects.equals(this.status, error.status) && Objects.equals(this.referenceError, error.referenceError) && Objects.equals(this._atBaseType, error._atBaseType) && Objects.equals(this._atSchemaLocation, error._atSchemaLocation) && Objects.equals(this._atType, error._atType);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.code, this.reason, this.message, this.status, this.referenceError, this._atBaseType, this._atSchemaLocation, this._atType});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Error {\n");
        sb.append("    code: ").append(this.toIndentedString(this.code)).append("\n");
        sb.append("    reason: ").append(this.toIndentedString(this.reason)).append("\n");
        sb.append("    message: ").append(this.toIndentedString(this.message)).append("\n");
        sb.append("    status: ").append(this.toIndentedString(this.status)).append("\n");
        sb.append("    referenceError: ").append(this.toIndentedString(this.referenceError)).append("\n");
        sb.append("    _atBaseType: ").append(this.toIndentedString(this._atBaseType)).append("\n");
        sb.append("    _atSchemaLocation: ").append(this.toIndentedString(this._atSchemaLocation)).append("\n");
        sb.append("    _atType: ").append(this.toIndentedString(this._atType)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
