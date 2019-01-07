package com.systems.community.carpooling.southpool.persistence.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systems.community.carpooling.southpool.entities.Member;

public interface PersistenceService {

	public <T extends Member> T getMember(String username, Class<T> clazz);
	public <T extends Member> boolean findByUniqueConstraint(Map<String, String> uniqueConstraintNameValueMap,Class<T> clazz);
	public Member persist(Member entity);
	public Member merge(Member entity);
	public <T extends Member> List<T> getCarPlateNumberByUserName(Map<String, String> predicatesMap, Class<T> clazz);
	public <T extends Member> List<Member> getMembersBetweenDate(String dateColumn, Date startDate, Date endDate, Map<String,String> predicate, Class<T> clazz);
	public <T extends Member> List<T> getMembersBy(Map<String, String> predicatesMap, Class<T> clazz);
	public int updatetHistoryBy(String hqlFormat, Map<String, Object> parameters);
}
