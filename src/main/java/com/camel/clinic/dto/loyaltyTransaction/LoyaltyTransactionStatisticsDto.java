package com.camel.clinic.dto.loyaltyTransaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoyaltyTransactionStatisticsDto {
    private long totalPointsEarned;
    private long totalPointsRedeemed;
    private long totalPointsExpired;
    private long currentPointsBalance;

    public static LoyaltyTransactionStatisticsDto from(long totalPointsEarned, long totalPointsRedeemed, long totalPointsExpired) {
        LoyaltyTransactionStatisticsDto dto = LoyaltyTransactionStatisticsDto.builder()
                .totalPointsEarned(totalPointsEarned)
                .totalPointsRedeemed(totalPointsRedeemed)
                .totalPointsExpired(totalPointsExpired)
                .build();
        dto.calculateCurrentBalance();
        return dto;
    }

    private void calculateCurrentBalance() {
        this.currentPointsBalance = totalPointsEarned - totalPointsRedeemed - totalPointsExpired;
    }
}