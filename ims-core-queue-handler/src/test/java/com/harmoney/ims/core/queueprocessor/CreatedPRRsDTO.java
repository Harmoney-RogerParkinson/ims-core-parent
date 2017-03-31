package com.harmoney.ims.core.queueprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmoney.ims.core.database.ConvertUtils;
import com.harmoney.ims.core.instances.ProtectRealisedRevenue;

public class CreatedPRRsDTO {
	
    private static final Logger log = LoggerFactory.getLogger(CreatedPRRsDTO.class);

	private BigDecimal protectRealised = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
	private BigDecimal managementFeeRealised = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
	private BigDecimal salesCommissionFeeRealised = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
	private BigDecimal protectWaived = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
	private boolean nullDates = true; 
	private int size=0;
	
	public CreatedPRRsDTO(List<ProtectRealisedRevenue> createdPRRs,int size, double protectWaived, double protectRealised, double managementFeeRealised, double salesCommissionFeeRealised,boolean nullDates) {
		this(createdPRRs);
		compare(size,nullDates,protectWaived,protectRealised,managementFeeRealised,salesCommissionFeeRealised);
	}

	public CreatedPRRsDTO(List<ProtectRealisedRevenue> createdPRRs) {
		for (ProtectRealisedRevenue prr : createdPRRs) {
			log.info("PRR: {} {} {} {} {} {} {}",
					String.format("%2d", size),ConvertUtils.printDate(prr.getDueDate()),
					prr.getProtectWaived(),prr.getProtectRealised(),
					prr.getManagementFeeRealised(),prr.getSalesCommissionFeeRealised(),ConvertUtils.printDate(prr.getProtectRealisedDate()));
			if (prr.getProtectRealised() != null) {
				protectRealised = protectRealised.add(prr.getProtectRealised());
			}
			if (prr.getManagementFeeRealised() != null) {
				managementFeeRealised = managementFeeRealised.add(prr.getManagementFeeRealised());
			}
			if (prr.getSalesCommissionFeeRealised() != null) {
				salesCommissionFeeRealised = salesCommissionFeeRealised.add(prr.getSalesCommissionFeeRealised());
			}
			if (prr.getProtectWaived() != null) {
				protectWaived = protectWaived.add(prr.getProtectWaived());
			}
			if (prr.getProtectRealisedDate() != null) {
				nullDates = false;
			}
			size++;
		}
		log.info("---: {} {} {} {} {} {} {}",String.format("%2d", size),"----------",protectWaived,protectRealised,managementFeeRealised,salesCommissionFeeRealised,nullDates);
	}

	public void compare(int size, boolean nullDates, double protectWaived, double protectRealised, double managementFeeRealised, double salesCommissionFeeRealised) {
		assertEquals(size,this.size);	
		assertEquals(makeBigDecimal(protectWaived),this.protectWaived);
		assertEquals(makeBigDecimal(protectRealised),this.protectRealised);
		assertEquals(makeBigDecimal(managementFeeRealised),this.managementFeeRealised);
		assertEquals(makeBigDecimal(salesCommissionFeeRealised),this.salesCommissionFeeRealised);
		assertTrue(nullDates == this.nullDates);
	}
	private BigDecimal makeBigDecimal(double d) {
		return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
	}
}
