package com.camel.clinic.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserStatisticsDto {
    private long patientsCount;
    private long doctorsCount;
    private long adminsCount;
    private long staffsCount;
    private long totalCount;

    public long calculateTotalCount() {
        return patientsCount + doctorsCount + adminsCount + staffsCount;
    }
}