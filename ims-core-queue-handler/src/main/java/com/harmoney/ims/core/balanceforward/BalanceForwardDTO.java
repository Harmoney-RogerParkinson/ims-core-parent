package com.harmoney.ims.core.balanceforward;

import java.time.LocalDateTime;
import java.util.List;

public class BalanceForwardDTO {

	private final LocalDateTime start;
	private final LocalDateTime end;
	private final List<String> accountIds;

	public BalanceForwardDTO(LocalDateTime lastMomentOfLastMonth,
			LocalDateTime lastMomentOfMonth, List<String> accountIds) {
		this.start = lastMomentOfLastMonth;
		this.end = lastMomentOfMonth;
		this.accountIds = accountIds;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public List<String> getAccountIds() {
		return accountIds;
	}

}
