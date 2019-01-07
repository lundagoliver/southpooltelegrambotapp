package com.systems.community.carpooling.southpool.persistence.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systems.community.carpooling.southpool.entities.Member;
import com.systems.community.carpooling.southpool.persistence.dao.MemberDAO;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PersistenceServiceImpl implements PersistenceService {

	private MemberDAO memberDao;
	
	
	public PersistenceServiceImpl(MemberDAO memberDao) {
		this.memberDao = memberDao;
	}

	@Override
	public <T extends Member> T getMember(String username, Class<T> clazz) {
		return memberDao.getMember(username, clazz);
	}

	@Override
	public <T extends Member> boolean findByUniqueConstraint(Map<String, String> uniqueConstraintNameValueMap, Class<T> clazz) {
		return memberDao.findByUniqueConstraint(uniqueConstraintNameValueMap, clazz);
	}

	@Override
	public Member persist(Member entity) {
		return memberDao.persist(entity);
	}

	@Override
	public Member merge(Member entity) {
		return memberDao.merge(entity);
	}

	@Override
	public <T extends Member> List<T> getCarPlateNumberByUserName(Map<String, String> predicatesMap, Class<T> clazz) {
		return memberDao.getCarPlateNumberByUserName(predicatesMap, clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Member> List<Member> getMembersBetweenDate(String dateColumn, Date startDate, Date endDate, Map<String,String> predicate,
			Class<T> clazz) {
		return (List<Member>) memberDao.getMembersBetweenDate(dateColumn, startDate, endDate, predicate, clazz);
	}

	@Override
	public <T extends Member> List<T> getMembersBy(Map<String, String> predicatesMap, Class<T> clazz) {
		return memberDao.getMembersBy(predicatesMap, clazz);
	}

	@Override
	public int updatetHistoryBy(String hqlFormat, Map<String, Object> parameters) {
		return memberDao.updatetHistoryBy(hqlFormat, parameters);
	}
}
