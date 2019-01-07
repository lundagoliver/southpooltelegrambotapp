package com.systems.community.carpooling.southpool;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.systems.community.carpooling.southpool.persistence.service.PersistenceService;

@ConditionalOnExpression("${resetPostCount.enabled}")
@Component
public class ResetPostCount {
	
	private final Logger log = LoggerFactory.getLogger(ResetPostCount.class);
	
	private PersistenceService persistenceService;
	
	@Autowired
	public ResetPostCount(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
	
	@Scheduled(cron = "${cron.resetPostCount}" )
	@Transactional
	public void clearGspErrorLogs() {
		String sqlUpdateSouthPoolMemberHomeToWork = "UPDATE SouthPoolMemberHomeToWork t SET t.postCount = 0";
		String sqlUpdateSouthPoolMemberWorkToHome = "UPDATE SouthPoolMemberWorkToHome t SET t.postCount = 0";
		persistenceService.updatetHistoryBy(sqlUpdateSouthPoolMemberHomeToWork, null);
		persistenceService.updatetHistoryBy(sqlUpdateSouthPoolMemberWorkToHome, null);
	}
}
