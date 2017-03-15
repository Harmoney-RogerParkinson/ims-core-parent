package com.harmoney.ims.core.balanceforward;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceForwardDTO {

	private final LocalDateTime start;
	private final LocalDateTime end;
	private final List<String> accountIds;
	private final Map<String,Integer> balfwdCountMap = new HashMap<>();

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

	public void put(String accountId, int i) {
		balfwdCountMap.put(accountId, i);
	}
	public int get(String accountId) {
		return balfwdCountMap.get(accountId);
	}

}
