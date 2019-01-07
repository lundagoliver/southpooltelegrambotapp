package com.systems.community.carpooling.southpool.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.systems.community.carpooling.southpool.entities.Member;

@Repository
public class MemberDAO {

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Get member info by username
	 * @param username
	 * @param clazz
	 * @return
	 */
	public <T extends Member> T getMember(String username, Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> member = cq.from(clazz);
		Predicate gspCodePredicate = cb.equal(member.get("username"), username);
		cq.where(gspCodePredicate);
		TypedQuery<T> query = em.createQuery(cq).setMaxResults(1);
		return !query.getResultList().isEmpty() ? query.getResultList().get(0) : null;
	}
	
	public <T extends Member> boolean findByUniqueConstraint(Map<String, String> uniqueConstraintNameValueMap,Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> from = cq.from(clazz);
		ArrayList<Predicate> predicateList = new ArrayList<>();
		for (Map.Entry<String, String> entry : uniqueConstraintNameValueMap.entrySet()) {
			Predicate id = cb.equal(from.get(entry.getKey()), entry.getValue());
			predicateList.add(id);
		}
		CriteriaQuery<Long> select = cq.select(cb.count(from));
		cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Long> typedQuery = em.createQuery(select);
		return typedQuery.getSingleResult() > 0;
	}
	
	public Member persist(Member entity) {
		try {
			em.persist(entity);	
		} catch (RuntimeException re) {
			throw re;
		}
		return entity;
	}
	
	public Member merge(Member entity) {
		try {
			em.merge(entity);	
		} catch (RuntimeException re) {
			throw re;
		}
		return entity;
	}
	
	public <T extends Member> List<T> getCarPlateNumberByUserName(Map<String, String> predicatesMap, Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> from = cq.from(clazz);
		
		List<Predicate> predicateList = new ArrayList<>();
		for(Entry<String, String> entry : predicatesMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			
			predicateList.add(cb.equal(from.get(key), value));
		}
		
		cq.where(cb.and(predicateList.toArray(new Predicate[predicateList.size()])));
		CriteriaQuery<T> select = cq.select(from);
		TypedQuery<T> typedQuery = em.createQuery(select);
		return typedQuery.getResultList();
	}
	
	public <T extends Member> List<T> getMembersBy(Map<String, String> predicatesMap, Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> from = cq.from(clazz);
		
		List<Predicate> predicateList = new ArrayList<>();
		for(Entry<String, String> entry : predicatesMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			
			predicateList.add(cb.equal(from.get(key), value));
		}
		
		cq.where(cb.and(predicateList.toArray(new Predicate[predicateList.size()])));
		CriteriaQuery<T> select = cq.select(from);
		TypedQuery<T> typedQuery = em.createQuery(select);
		return typedQuery.getResultList();
	}
	
	public <T extends Member> List<T> getMembersBetweenDate(String dateColumn, Date startDate, Date endDate, Map<String, String> predicatesMap, Class<T> clazz) {
		if (predicatesMap.containsKey("username")) {
			predicatesMap.remove("username");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> from = cq.from(clazz);
		List<Predicate> predicateList = new ArrayList<>();
		if (startDate != null && endDate != null) {
			Predicate dateTimeBetween = cb.between(from.<Date> get(dateColumn), startDate, endDate);
			predicateList.add(dateTimeBetween);	
		}
		
		for(Entry<String, String> entry : predicatesMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			
			predicateList.add(cb.equal(from.get(key), value));
		}
		if ((startDate != null && endDate != null) || predicatesMap != null) {
			cq.where(cb.and(predicateList.toArray(new Predicate[predicateList.size()])));	
		}
		CriteriaQuery<T> select = cq.select(from);
		TypedQuery<T> typedQuery = em.createQuery(select);
		return typedQuery.getResultList();
	}
	
	public int updatetHistoryBy(String hqlFormat, Map<String, Object> parameters) {
		Query query = em.createQuery(hqlFormat);
		if (parameters != null) {
			for(Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}	
		}
		return query.executeUpdate();
	}
}
