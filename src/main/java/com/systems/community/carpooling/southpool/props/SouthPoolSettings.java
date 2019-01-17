package com.systems.community.carpooling.southpool.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="southpool.telegram.service.props")
public class SouthPoolSettings {

	private String telegramBotToken;
	private String telegramBotUsername;
	
	private String groupChatId;
	private String telegramEndPoint;
	private String telegramApiUrl;
	private String groupchatSenderBotToken;
	
	
	private String groupChatIdAdmins;
	private String groupchatAdminsConcernSernderBot;
}
