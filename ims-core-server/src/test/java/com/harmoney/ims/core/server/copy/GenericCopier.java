/**
 * 
 */
package com.harmoney.ims.core.server.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.AbstractDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class GenericCopier {
	
    private static final Logger log = LoggerFactory.getLogger(GenericCopier.class);
	@Autowired
	private PartnerConnection partnerConnection;

    public List<Result> copy(AbstractDAO<?> DAO) throws ConnectionException {
    	List<Result> ret = new ArrayList<>();
		List<String> fields = DAO.getSalesforceFields();
		StringBuilder f = new StringBuilder("SELECT ");
		for (String fieldName : fields) {
			f.append(fieldName);
			f.append(',');
		}
		int lastComma = f.lastIndexOf(",");
		f.deleteCharAt(lastComma);
		f.append(" FROM "+DAO.getSalesforceTableName());
		QueryResult qr = partnerConnection.query(f.toString());
		SObject[] records = qr.getRecords();
		for (SObject r : records) {
			Map<String, Object> fieldMap = new HashMap<>();
			for (String fieldName : fields) {
				fieldMap.put(fieldName, r.getField(fieldName));
			}
			Result result = DAO.unpack(fieldMap);
			log.debug("{}", result);
		}
		return ret;
	}
}


