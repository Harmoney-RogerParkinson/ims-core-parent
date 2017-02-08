/**
 * 
 */
package com.harmoney.ims.core.database;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.InternalQueueEntry;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InternalQueueDAO {

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	
	@Transactional
	public void createEntry(InternalQueueEntry internalQueueEntry) {
		entityManager.persist(internalQueueEntry);
		entityManager.flush();
	}
	@Transactional(readOnly=true)
	public List<InternalQueueEntry> getUnprocessedQueueEntries()
	{
	return entityManager.createQuery("from com.harmoney.ims.core.instances.InternalQueueEntry", InternalQueueEntry.class).getResultList();
	}

	@Transactional
	public void deleteQueueEntry(InternalQueueEntry internalQueueEntry)
	{
	entityManager.remove(internalQueueEntry);
	}
	/**
	 * Get the id value for this object. Assumes there is a single Id field, not a composite.
	 * 
	 * @param object
	 */
	public Object getId(Object object) {
		assert object != null : "object must not be null";
		for (Method method: object.getClass().getMethods()) {
			if (method.isAnnotationPresent(Id.class)) {
				try {
					return method.invoke(object, new Object[]{});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}			
		}
		throw new RuntimeException("No Id field found on "+object.getClass().getName());
	}
	

}
