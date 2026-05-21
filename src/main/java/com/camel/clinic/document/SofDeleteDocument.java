package com.camel.clinic.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
public class SofDeleteDocument extends BaseDocument {
    @Field("deleted_at")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    public Date deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
