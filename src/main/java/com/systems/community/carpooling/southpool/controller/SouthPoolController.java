package com.systems.community.carpooling.southpool.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.itextpdf.text.DocumentException;
import com.systems.community.carpooling.southpool.entities.Member;
import com.systems.community.carpooling.southpool.entities.MemberCars;
import com.systems.community.carpooling.southpool.entities.PreviousMessage;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberHomeToWork;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberWorkToHome;
import com.systems.community.carpooling.southpool.persistence.service.PersistenceService;
import com.systems.community.carpooling.southpool.props.SouthPoolSettings;
import com.systems.community.carpooling.southpool.service.SouthPoolService;
import com.systems.community.carpooling.southpool.service.SouthpoolSearchService;
import com.systems.community.carpooling.southpool.utility.CallBackContants;
import com.systems.community.carpooling.southpool.utility.DateUtility;
import com.systems.community.carpooling.southpool.utility.SouthPoolCommands;
import com.systems.community.carpooling.southpool.utility.SouthPoolConstantMessage;
import com.systems.community.carpooling.southpool.utility.TimeUtility;
import com.systems.community.carpooling.southpool.utility.menu.InlineKeyboardBuilder;
import com.systems.community.carpooling.southpool.utility.menu.MenuManager;
import com.systems.community.carpooling.southpool.utility.menu.post.InlineKeyboardBuilderPost;
import com.systems.community.carpooling.southpool.utility.menu.post.MenuManagerPost;
import com.systems.community.carpooling.southpool.utility.menu.search.InlineKeyboardBuilderSearch;
import com.systems.community.carpooling.southpool.utility.menu.search.MenuManagerSearch;
import com.systems.community.carpooling.southpool.utility.menu.update.InlineKeyboardBuilderUpdate;
import com.systems.community.carpooling.southpool.utility.menu.update.MenuManagerUpdate;
import com.vdurmont.emoji.EmojiParser;

@Component
public class SouthPoolController extends TelegramLongPollingBot {

	private static final Log log = LogFactory.getLog(SouthPoolController.class);

	private SouthPoolSettings southPoolSettings;
	private PersistenceService persistenceService;
	private SouthPoolConstantMessage constantMessage;
	private MenuManager menuManager;
	private MenuManagerUpdate menuManagerUpdate;
	private MenuManagerPost menuManagerPost;
	private MenuManagerSearch menuManagerSearch;
	private SouthPoolService southPoolService;
	private SouthpoolSearchService southpoolSearchService;

	private List<KeyboardRow> seatKeyBoard = new ArrayList<>();
	private List<KeyboardRow> timeKeyBoard = new ArrayList<>();
	private List<KeyboardRow> waitingTimeKeyBoard = new ArrayList<>();
	private List<String> waitingTime = new ArrayList<>();

	public SouthPoolController(SouthPoolSettings southPoolSettings, 
			PersistenceService persistenceService,
			SouthPoolConstantMessage constantMessage, 
			MenuManager menuManager, 
			MenuManagerUpdate menuManagerUpdate,
			MenuManagerPost menuManagerPost,
			MenuManagerSearch menuManagerSearch,
			SouthPoolService southPoolService,
			SouthpoolSearchService southpoolSearchService) {
		super();
		this.southPoolSettings = southPoolSettings;
		this.persistenceService = persistenceService;
		this.constantMessage = constantMessage;
		this.menuManager = menuManager;
		this.menuManagerUpdate = menuManagerUpdate;
		this.menuManagerPost = menuManagerPost;
		this.menuManagerSearch = menuManagerSearch;
		this.southPoolService = southPoolService;
		this.southpoolSearchService = southpoolSearchService;
	}

	@PostConstruct
	private void init() {
		menuManager.setColumnsCount(2);
		menuManager.setButtonsPerPage(20);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":information_source: ") + "My Info", CallBackContants.SHOW_MEMBER_INFO);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit My info", CallBackContants.UPDATE_MEMBER_INFO);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":bird: ") + "Request", CallBackContants.POST_REQUEST);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":mag_right: ") + "Search", CallBackContants.SEARCH_POST);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":white_check_mark: ") + "Verify", CallBackContants.VERIFY_MEMBER);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":traffic_light: ") + "Report Traffic Status", CallBackContants.REPORT_TRAFFIC);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":no_mobile_phones: ") + "Ban Member", CallBackContants.BAN_MEMBER);
		menuManager.addMenuItem(EmojiParser.parseToUnicode(":interrobang: ") + "Complain a Member", CallBackContants.COMPLAIN_MEMBER);
		
		//menuManager.addMenuItem(EmojiParser.parseToUnicode(":bird: ") + "Bot Update", CallBackContants.BOT_UPDATE);
		menuManager.init();

		menuManagerUpdate.setColumnsCount(2);
		menuManagerUpdate.setButtonsPerPage(30);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Name", CallBackContants.SET_NAME);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Link", CallBackContants.SET_FB_PROFILE_LINK);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Mobile", CallBackContants.SET_MOBILE);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Car Plate", CallBackContants.SET_CAR_PLATE);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit You Are", CallBackContants.SET_YOU_ARE);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Pick Up", CallBackContants.SET_PICKUP_LOC);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Drop Off", CallBackContants.SET_DROP_OFF_LOC);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Route", CallBackContants.SET_ROUTE);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Seat", CallBackContants.SET_AVAILABLE_SEAT);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit ETA", CallBackContants.SET_ETA);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit ETD", CallBackContants.SET_ETD);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":lower_left_ballpoint_pen: ") + "Edit Instruction", CallBackContants.SET_CUSTOM_MESSAGE);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":bird: ") + "Request", CallBackContants.POST_REQUEST);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":mag_right: ") + "Search", CallBackContants.SEARCH_POST);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":white_check_mark: ") + "Verify Member", CallBackContants.VERIFY_MEMBER);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":information_source: ") + "My info", CallBackContants.SHOW_MEMBER_INFO);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":traffic_light: ") + "Report Traffic Status", CallBackContants.REPORT_TRAFFIC);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":no_mobile_phones: ") + "Ban Member", CallBackContants.BAN_MEMBER);
		menuManagerUpdate.addMenuItem(EmojiParser.parseToUnicode(":interrobang: ") + "Complain a Member", CallBackContants.COMPLAIN_MEMBER);
		
		menuManagerUpdate.init();

		menuManagerPost.setColumnsCount(2);
		menuManagerPost.setButtonsPerPage(20);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":taxi: ") + "Post as Driver", CallBackContants.POST_AS_DRIVER);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":male_office_worker: ") +"Post as Passenger", CallBackContants.POST_AS_PASSENGER);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":taxi: ") + "Driver Tomorrow", CallBackContants.POST_AS_DRIVER_TOMORROW);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":male_office_worker: ") +"Passenger Tomorrow", CallBackContants.POST_AS_PASSENGER_TOMORROW);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":information_source: ") + "My info", CallBackContants.SHOW_MEMBER_INFO);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":traffic_light: ") + "Report Traffic Status", CallBackContants.REPORT_TRAFFIC);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":no_mobile_phones: ") + "Ban Member", CallBackContants.BAN_MEMBER);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":white_check_mark: ") + "Verify Member", CallBackContants.VERIFY_MEMBER);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":interrobang: ") + "Complain a Member", CallBackContants.COMPLAIN_MEMBER);
		menuManagerPost.addMenuItem(EmojiParser.parseToUnicode(":mag_right: ") + "Search", CallBackContants.SEARCH_POST);
		menuManagerPost.init();

		menuManagerSearch.setColumnsCount(2);
		menuManagerSearch.setButtonsPerPage(20);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":taxi: :red_car: :mag: ") + " Search Driver", CallBackContants.TODAY_DRIVER);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":male_office_worker: :woman_office_worker: :mag: ") + " Search Passenger", CallBackContants.TODAY_PASSENGER);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":white_check_mark: ") + "Verify Member", CallBackContants.VERIFY_MEMBER);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":information_source: ") + "My Info", CallBackContants.SHOW_MEMBER_INFO);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":traffic_light: ") + "Report Traffic Status", CallBackContants.REPORT_TRAFFIC);
		menuManagerSearch.addMenuItem(EmojiParser.parseToUnicode(":interrobang: ") + "Complain a Member", CallBackContants.COMPLAIN_MEMBER);
		menuManagerSearch.init();


		for (int i = 1; i<=20; i++) {
			KeyboardRow key = new KeyboardRow();
			key.add(Integer.toString(i));
			seatKeyBoard.add(key);
		}

		for (Integer i = 0; i<24; i++) {
			KeyboardRow key = new KeyboardRow();
			key.add(i < 12 ? i == 0 ? "12" + ":00 AM" : i + ":00 AM" : TimeUtility.convertMilitaryToStandardTime(String.valueOf(i))+":00 PM");
			key.add(i < 12 ? i == 0 ? "12" + ":15 AM" : i + ":15 AM" : TimeUtility.convertMilitaryToStandardTime(String.valueOf(i))+":15 PM");
			key.add(i < 12 ? i == 0 ? "12" + ":30 AM" : i + ":30 AM" : TimeUtility.convertMilitaryToStandardTime(String.valueOf(i))+":30 PM");
			key.add(i < 12 ? i == 0 ? "12" + ":45 AM" : i + ":45 AM" : TimeUtility.convertMilitaryToStandardTime(String.valueOf(i))+":45 PM");
			timeKeyBoard.add(key);
		}

		waitingTime = new ArrayList<>();
		waitingTime.add("05-minutes");
		waitingTime.add("10-minutes");
		waitingTime.add("15-minutes");
		waitingTime.add("20-minutes");
		waitingTime.add("25-minutes");
		waitingTime.add("30-minutes");
		waitingTime.add("35-minutes");
		waitingTime.add("40-minutes");
		waitingTime.add("45-minutes");
		waitingTime.add("50-minutes");
		waitingTime.add("55-minutes");
		waitingTime.add("60-minutes");
		for (String wait : waitingTime) {
			KeyboardRow key = new KeyboardRow();
			key.add(wait);
			waitingTimeKeyBoard.add(key);
		}
	}

	@Transactional
	@Override
	public void onUpdateReceived(Update update) {

		InlineKeyboardBuilder builder = menuManager.createMenuForPage(0, true);
		InlineKeyboardBuilderUpdate builderUpdate = menuManagerUpdate.createMenuForPage(0, true);
		InlineKeyboardBuilderPost builderPost = menuManagerPost.createMenuForPage(0, true);
		InlineKeyboardBuilderSearch builderSearch = menuManagerSearch.createMenuForPage(0, true);

		if (update.hasMessage() && update.getMessage().hasText()) {
			SendMessage message = new SendMessage();	
			ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
			message.setReplyMarkup(replyKeyboardMarkup);
			replyKeyboardMarkup.setSelective(true);
			replyKeyboardMarkup.setResizeKeyboard(true);
			replyKeyboardMarkup.setOneTimeKeyboard(true);

			String username = update.getMessage().getChat().getUserName();
			String messageText = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();
			long messageId = update.getMessage().getMessageId();

			message.setChatId(chatId).setParseMode("HTML");
			//Check if username is set already. If username is not yet set, show some information how to do it.
			if (username == null) {
				message.setText(constantMessage.userNameNotYetSetMessage());
				sendMessage(message);
				return;
			}

			String botQuestion = "";
			PreviousMessage previousMessage = persistenceService.getMember(username, PreviousMessage.class);

			//Check if user is a registered member. If not, ask the user to register or exit the application.
			SouthPoolMemberHomeToWork southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
			SouthPoolMemberWorkToHome southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);

			if (southPoolMemberHomeToWork == null || southPoolMemberWorkToHome == null) {
				message.setText(constantMessage.notRegisteredMemberMessage());
				message.setReplyMarkup(constantMessage.shownOptions());
				sendMessage(message);
			}
			else if ("N".equalsIgnoreCase(southPoolMemberHomeToWork.getAllowed()) || "N".equalsIgnoreCase(southPoolMemberWorkToHome.getAllowed())) {
				String x = EmojiParser.parseToUnicode(":x:");
				message.setText(x+SouthPoolConstantMessage.BANNED_USER);
				sendMessage(message);
				return;
			}
			else {
				switch (messageText) {

				case SouthPoolCommands.START:
					message.setText(constantMessage.showIntro());
					sendMessage(message);
					//Check if the member's information is completed already, if not show message to complete the member's information details.
					if(isInfoNotComplete(southPoolMemberHomeToWork)) {
						message.setText("Hi! "+ username +"! How may I help you today?\n" + constantMessage.showInfoToUpdate(southPoolMemberHomeToWork));
						sendMessage(message);
						break;
					}
					//If the member information details are completed already, then show the options for info update and request posting.
					if(!isInfoNotComplete(southPoolMemberHomeToWork)) {
						builder.setChatId(chatId).setText("Hi! "+ username +"! How may I help you today?\nPlease Choose action:");
						sendMessage(builder.build());
					}
					break;


				case SouthPoolCommands.SET_NAME:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_YOUR_NAME, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_FB_PROFILE_LINK:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_FACEBOOK_PROFILE_LINK, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_MOBILE:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_MOBILE_NUMBER, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_CAR_PLATE:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_CAR_PLATE_NUMBER, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_YOU_ARE:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForDriverOrPassenger());
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_PICKUP_LOC:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_PICK_UP_LOCATION, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_DROP_OFF_LOC:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_DROP_OFF_LOCATION, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_ROUTE:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_ROUTE, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_AVAILABLE_SEAT:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_SEAT, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					replyKeyboardMarkup.setKeyboard(seatKeyBoard);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_ETA:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_ETA, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					replyKeyboardMarkup.setKeyboard(timeKeyBoard);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_ETD:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_WAITING, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					replyKeyboardMarkup.setKeyboard(waitingTimeKeyBoard);
					sendMessage(message);
					break;

				case SouthPoolCommands.SET_CUSTOM_MESSAGE:
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_SPECIAL_MESSAGE, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);
					break;

				case SouthPoolCommands.MY_INFO:
					message.setText(SouthPoolConstantMessage.SELECT_INFO);
					message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
					sendMessage(message);
					break;

				default:
					PreviousMessage previousUserMessage = persistenceService.getMember(username, PreviousMessage.class);

					if (previousUserMessage == null) {
						message.setText("Hi! "+ username +"! How may I help you?\n" + constantMessage.showInfoToUpdate(southPoolMemberHomeToWork));
						sendMessage(message);
					}

					if (SouthPoolConstantMessage.ENTER_YOUR_NAME.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setName(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setName(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_YOUR_NAME.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setName(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setName(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_FACEBOOK_PROFILE_LINK.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setFacebookProfileLink(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setFacebookProfileLink(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_FACEBOOK_PROFILE_LINK.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setFacebookProfileLink(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setFacebookProfileLink(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_MOBILE_NUMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setMobileNumber(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setMobileNumber(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_MOBILE_NUMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setMobileNumber(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setMobileNumber(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_CAR_PLATE_NUMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setCarPlateNumber(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
							//
							//							Map<String, String> uniqueConstraintNameValueMap = new HashMap<>();
							//							uniqueConstraintNameValueMap.put("username", username);
							//							uniqueConstraintNameValueMap.put("carPlateNumber", messageText);
							//							if(!persistenceService.findByUniqueConstraint(uniqueConstraintNameValueMap, MemberCars.class)) {
							//								MemberCars memberCars = new MemberCars();
							//								memberCars.setUsername(username);
							//								memberCars.setCarPlateNumber(messageText);
							//								persistenceService.persist(memberCars);
							//								message.setText("Addtional car with plate number " + messageText + " has been added to your account.");
							//								sendMessage(message);
							//							}
						}

						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setCarPlateNumber(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_CAR_PLATE_NUMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setCarPlateNumber(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setCarPlateNumber(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
							//
							//							Map<String, String> uniqueConstraintNameValueMap = new HashMap<>();
							//							uniqueConstraintNameValueMap.put("username", username);
							//							uniqueConstraintNameValueMap.put("carPlateNumber", messageText);
							//							if(!persistenceService.findByUniqueConstraint(uniqueConstraintNameValueMap, MemberCars.class)) {
							//								MemberCars memberCars = new MemberCars();
							//								memberCars.setUsername(username);
							//								memberCars.setCarPlateNumber(messageText);
							//								persistenceService.persist(memberCars);
							//								message.setText("Addtional car with plate number " + messageText + " has been added to your account.");
							//								sendMessage(message);
							//							}
						}
					}

					else if (SouthPoolConstantMessage.ENTER_PICK_UP_LOCATION.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setPicUpLoc(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_PICK_UP_LOCATION.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setPicUpLoc(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_DROP_OFF_LOCATION.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setDropOffLoc(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_DROP_OFF_LOCATION.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setDropOffLoc(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_ROUTE.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setRoute(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_ROUTE.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setRoute(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_SEAT.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setAvailableSlots(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_SEAT.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setAvailableSlots(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_ETA.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							LocalDateTime localDateTime = DateUtility.toLocaDateTime(DateUtility.convertDateToGMT(8));
							messageText = TimeUtility.convertStandardTimeToMilitaryTime(messageText);
							String etaTime = messageText.contains(" AM") ? messageText.replace(" AM", "") : messageText.contains(" PM") ? messageText.replace(" PM", "") : messageText;
							String[] etaHHmm = etaTime.split(":");
							String dateETA = localDateTime.withHour(0).withMinute(0).withSecond(0).plusHours(Long.valueOf(etaHHmm[0])).plusMinutes(Long.valueOf(etaHHmm[1])).format(DateUtility.FORMAT_DATETIME);
							String dateETD = localDateTime.withHour(0).withMinute(0).withSecond(0).plusHours(Long.valueOf(etaHHmm[0])).plusMinutes(Long.valueOf(etaHHmm[1])).plusMinutes(15).format(DateUtility.FORMAT_DATETIME);
							southPoolMemberHomeToWork.setEta(dateETA);
							southPoolMemberHomeToWork.setEtd(dateETD);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_ETA.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							LocalDateTime localDateTime = DateUtility.toLocaDateTime(DateUtility.convertDateToGMT(8));
							messageText = TimeUtility.convertStandardTimeToMilitaryTime(messageText);
							String etaTime = messageText.contains(" AM") ? messageText.replace(" AM", "") : messageText.contains(" PM") ? messageText.replace(" PM", "") : messageText;
							String[] etaHHmm = etaTime.split(":");
							String dateETA = localDateTime.withHour(0).withMinute(0).withSecond(0).plusHours(Long.valueOf(etaHHmm[0])).plusMinutes(Long.valueOf(etaHHmm[1])).format(DateUtility.FORMAT_DATETIME);
							String dateETD = localDateTime.withHour(0).withMinute(0).withSecond(0).plusHours(Long.valueOf(etaHHmm[0])).plusMinutes(Long.valueOf(etaHHmm[1])).plusMinutes(15).format(DateUtility.FORMAT_DATETIME);
							southPoolMemberWorkToHome.setEta(dateETA);
							southPoolMemberWorkToHome.setEtd(dateETD);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_WAITING.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							LocalDateTime localDateTime = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta());
							String date = localDateTime.plusMinutes(Integer.valueOf(messageText.split("-")[0])).format(DateUtility.FORMAT_DATETIME);
							southPoolMemberHomeToWork.setEtd(date);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_WAITING.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberHomeToWork != null) {
							LocalDateTime localDateTime = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta());
							String date = localDateTime.plusMinutes(Integer.valueOf(messageText.split("-")[0])).format(DateUtility.FORMAT_DATETIME);
							southPoolMemberWorkToHome.setEtd(date);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}

					else if (SouthPoolConstantMessage.ENTER_SPECIAL_MESSAGE.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							southPoolMemberHomeToWork.setCustomMessage(messageText);
							persistenceService.merge(southPoolMemberHomeToWork);
						}
					}
					else if (SouthPoolConstantMessage.ENTER_SPECIAL_MESSAGE.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							southPoolMemberWorkToHome.setCustomMessage(messageText);
							persistenceService.merge(southPoolMemberWorkToHome);
						}
					}
					
					else if (SouthPoolConstantMessage.VERIFY_MEMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						String user = messageText.contains("@") ? messageText.replaceAll("@", "") : messageText; 
						southPoolMemberHomeToWork = persistenceService.getMember(user, SouthPoolMemberHomeToWork.class);
						if (southPoolMemberHomeToWork != null) {
							builder.setChatId(chatId).setText(SouthPoolConstantMessage.verifyMember(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
							sendMessage(builder.build());	
						}else{
							message.setText("@"+user + " is not a SOUTHPOOL member!");
							sendMessage(message);
						}
					}
					else if (SouthPoolConstantMessage.VERIFY_MEMBER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						String user = messageText.contains("@") ? messageText.replaceAll("@", "") : messageText;
						southPoolMemberWorkToHome = persistenceService.getMember(user, SouthPoolMemberWorkToHome.class);
						if (southPoolMemberWorkToHome != null) {
							builder.setChatId(chatId).setText(SouthPoolConstantMessage.verifyMember(southPoolMemberWorkToHome)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
							sendMessage(builder.build());	
						}else{
							message.setText("@"+user + " is not a SOUTHPOOL member!");
							sendMessage(message);
						}
						
					}

					else if (SouthPoolConstantMessage.REPORT_TRAFFIC_STATUS.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag()) || 
							SouthPoolConstantMessage.REPORT_TRAFFIC_STATUS.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						try {
							StringBuilder report = new StringBuilder();
							report.append("<b>SOUTHPOOL FLASH REPORT</>").append("\n\n");
							report.append("Details : ").append("\n\n");
							report.append("<i>").append(messageText).append("</i>\n\n");
							report.append("Reported by: @"+username).append("\n");
							southPoolService.sendMessage(report.toString(),southPoolSettings.getGroupChatId(), southPoolSettings);
							String ok = EmojiParser.parseToUnicode(":white_check_mark:");
							message.setText(ok+SouthPoolConstantMessage.REPORT_POSTED);
							sendMessage(message);
							message.setText("Please select information to use, register or update :");
							message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
							sendMessage(message);
						} catch (UnsupportedEncodingException e) {
							log.error("",e);
						}
					}
					
					else if (SouthPoolConstantMessage.COMPLAIN_MEMBER_PASSENGER_OR_DRIVER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag()) || 
							SouthPoolConstantMessage.COMPLAIN_MEMBER_PASSENGER_OR_DRIVER.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						StringBuilder report = new StringBuilder();
						report.append("<b>MEMBER CONCERN</>").append("\n\n");
						report.append("Details : ").append("\n\n");
						report.append("<i>").append(messageText).append("</i>\n\n");
						report.append("Complainant: @"+username).append("\n");
						try {
							southPoolService.sendMessageToAdmin(report.toString(),southPoolSettings.getGroupChatIdAdmins(), southPoolSettings);
						} catch (UnsupportedEncodingException e) {
							log.error("{}", e);
						}
						String ok = EmojiParser.parseToUnicode(":white_check_mark:");
						message.setText(ok+SouthPoolConstantMessage.POSTED_COMPLAIN_MEMBER_PASSENGER_OR_DRIVER);
						sendMessage(message);
						message.setText("Please select information to use, register or update :");
						message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
						sendMessage(message);
					}
					
					else if (SouthPoolConstantMessage.BAN_MEMBER_TO_USE_THE_BOT.equals(previousUserMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag()) || 
							SouthPoolConstantMessage.BAN_MEMBER_TO_USE_THE_BOT.equals(previousUserMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						
							String user = messageText.contains("@") ? messageText.replaceAll("@", "") : messageText;
							southPoolMemberHomeToWork = persistenceService.getMember(user, SouthPoolMemberHomeToWork.class);
							southPoolMemberWorkToHome = persistenceService.getMember(user, SouthPoolMemberWorkToHome.class);
							if (southPoolMemberHomeToWork == null || southPoolMemberWorkToHome == null) {
								message.setText("User with username " + user + " does not exist!");
								sendMessage(message);	
							}
							else {
								southPoolMemberHomeToWork.setAllowed("N");
								persistenceService.merge(southPoolMemberHomeToWork);
								southPoolMemberWorkToHome.setAllowed("N");
								persistenceService.merge(southPoolMemberWorkToHome);
								
								StringBuilder report = new StringBuilder();
								report.append("<b>BANNED MEMBER</>").append("\n\n");
								report.append("Details : ").append("\n\n");
								report.append("<i>").append(user + " is banned from SOUTHPOOL community!").append("</i>\n\n");
								report.append("Banned by: @"+username).append("\n");
								try {
									southPoolService.sendMessageToAdmin(report.toString(),southPoolSettings.getGroupChatIdAdmins(), southPoolSettings);
								} catch (UnsupportedEncodingException e) {
									log.error("{}", e);
								}
								
								String ok = EmojiParser.parseToUnicode(":white_check_mark:");
								message.setText(ok+SouthPoolConstantMessage.BANNED);
								sendMessage(message);
							}
							message.setText("Please select information to use, register or update :");
							message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
							sendMessage(message);
					}
					
					if ( !SouthPoolConstantMessage.COMPLAIN_MEMBER_PASSENGER_OR_DRIVER.equals(previousUserMessage.getPrevMessage()) &&
							!SouthPoolConstantMessage.BAN_MEMBER_TO_USE_THE_BOT.equals(previousUserMessage.getPrevMessage()) &&
							!SouthPoolConstantMessage.REPORT_TRAFFIC_STATUS.equals(previousUserMessage.getPrevMessage()) 
							&& !SouthPoolConstantMessage.VERIFY_MEMBER.equals(previousUserMessage.getPrevMessage()) 
							&& CallBackContants.HOME_TO_WORK_INFO.equals(previousUserMessage.getTag())) {
						sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberHomeToWork, previousUserMessage, username, PreviousMessage.class));

						if (!isInfoNotComplete(southPoolMemberHomeToWork)) {
							message.setText("Please see your updated HOME to WORK Information below.");
							sendMessage(message);
							builderUpdate.setParse("HTML").setChatId(chatId).setText(SouthPoolConstantMessage.showMyInformation(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
							sendMessage(builderUpdate.build());

							if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).isAfter(DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEtd()))) {
								StringBuilder sb = new StringBuilder();
								String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
								String etd = EmojiParser.parseToUnicode(":airplane_departure:");
								sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
								sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
								sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
								message.setText(sb.toString());
								sendMessage(message);
							}
						}	
					}
					else if (!SouthPoolConstantMessage.COMPLAIN_MEMBER_PASSENGER_OR_DRIVER.equals(previousUserMessage.getPrevMessage()) 
							&& !SouthPoolConstantMessage.BAN_MEMBER_TO_USE_THE_BOT.equals(previousUserMessage.getPrevMessage()) 
							&& !SouthPoolConstantMessage.REPORT_TRAFFIC_STATUS.equals(previousUserMessage.getPrevMessage()) 
							&& !SouthPoolConstantMessage.VERIFY_MEMBER.equals(previousUserMessage.getPrevMessage()) 
							&& CallBackContants.WORK_TO_HOME_INFO.equals(previousUserMessage.getTag())) {
						sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberWorkToHome, previousUserMessage, username, PreviousMessage.class));

						if (!isInfoNotComplete(southPoolMemberWorkToHome)) {
							message.setText("Please see your updated WORK to HOME Information below.");
							sendMessage(message);
							builderUpdate.setChatId(chatId).setText(SouthPoolConstantMessage.showMyInformation(southPoolMemberWorkToHome)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
							sendMessage(builderUpdate.build());

							if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).isAfter(DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEtd()))) {
								StringBuilder sb = new StringBuilder();
								String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
								String etd = EmojiParser.parseToUnicode(":airplane_departure:");
								sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
								sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
								sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
								message.setText(sb.toString());
								sendMessage(message);
							}
						}	
					}
					break;
				}
			}
		}

		/*********************
		 * CallBack Commands *
		 *********************/
		if (update.hasCallbackQuery()) {

			SendMessage message = new SendMessage();
			ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
			message.setReplyMarkup(replyKeyboardMarkup);
			replyKeyboardMarkup.setSelective(true);
			replyKeyboardMarkup.setResizeKeyboard(true);
			replyKeyboardMarkup.setOneTimeKeyboard(true);

			CallbackQuery callBackQuery = update.getCallbackQuery();
			AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
			answerCallbackQuery.setCallbackQueryId(callBackQuery.getId());
			answerCallbackQuery.setShowAlert(true);

			String botQuestion;
			String username =  update.getCallbackQuery().getMessage().getChat().getUserName();
			String callData = update.getCallbackQuery().getData();
			long chatId = update.getCallbackQuery().getMessage().getChatId();

			message = new SendMessage().setChatId(chatId).setParseMode("HTML");
			//Check if username is set already. If username is not yet set, show some information how to do it.
			if (username == null) {
				message.setText(constantMessage.userNameNotYetSetMessage());
				sendMessage(message);
				return;
			}

			Map<String,String> predicatesMap = new HashMap<>();
			String carPlateCommand;
			String carPlateNumber = null;
			if (callData.contains("-")) {
				String[] value = callData.split("-");
				carPlateCommand = value[0];
				carPlateNumber = value[1];
				callData = carPlateCommand;
			}

			PreviousMessage previousMessage = persistenceService.getMember(username, PreviousMessage.class);
			SouthPoolMemberHomeToWork southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
			SouthPoolMemberWorkToHome southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
			
			if (southPoolMemberWorkToHome != null && southPoolMemberWorkToHome != null) {
				if ("N".equalsIgnoreCase(southPoolMemberHomeToWork.getAllowed()) || "N".equalsIgnoreCase(southPoolMemberWorkToHome.getAllowed())) {
					String x = EmojiParser.parseToUnicode(":x:");
					message.setText(x+SouthPoolConstantMessage.BANNED_USER);
					sendMessage(message);
					return;
				}	
			}

			switch (callData) {
			case CallBackContants.HOME_TO_WORK_INFO:

				if (!isInfoNotComplete(southPoolMemberHomeToWork)) {

					message.setText("Please see your HOME to WORK Information below.");
					sendMessage(message);


					builderUpdate.setChatId(chatId).setText(SouthPoolConstantMessage.showMyInformation(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
					sendMessage(builderUpdate.build());

					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).isAfter(DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEtd()))) {
						StringBuilder sb = new StringBuilder();
						String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
						String etd = EmojiParser.parseToUnicode(":airplane_departure:");
						sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
						sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
						sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
						message.setText(sb.toString());
						sendMessage(message);
					}
				}
				else {
					message.setText(constantMessage.showInfoToUpdate(southPoolMemberHomeToWork));
					sendMessage(message);
				}

				sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberHomeToWork, previousMessage, username, PreviousMessage.class));
				previousMessage = persistenceService.getMember(username, PreviousMessage.class);
				previousMessage.setTag(CallBackContants.HOME_TO_WORK_INFO);
				persistenceService.merge(previousMessage);
				break;

			case CallBackContants.WORK_TO_HOME_INFO:

				if (!isInfoNotComplete(southPoolMemberWorkToHome)) {

					message.setText("Please see your WORK to HOME Information below.");
					sendMessage(message);

					builderUpdate.setChatId(chatId).setText(SouthPoolConstantMessage.showMyInformation(southPoolMemberWorkToHome)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
					sendMessage(builderUpdate.build());

					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).isAfter(DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEtd()))) {
						StringBuilder sb = new StringBuilder();
						String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
						String etd = EmojiParser.parseToUnicode(":airplane_departure:");
						sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
						sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
						sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
						message.setText(sb.toString());
						sendMessage(message);
					}
				}
				else {
					message.setText(constantMessage.showInfoToUpdate(southPoolMemberWorkToHome));
					sendMessage(message);	
				}

				sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberWorkToHome, previousMessage, username, PreviousMessage.class));
				previousMessage = persistenceService.getMember(username, PreviousMessage.class);
				previousMessage.setTag(CallBackContants.WORK_TO_HOME_INFO);
				persistenceService.merge(previousMessage);
				break;

			case CallBackContants.CAR:
				predicatesMap = new HashMap<>();
				predicatesMap.put("username", username);
				predicatesMap.put("carPlateNumber", carPlateNumber);
				MemberCars memberCars = persistenceService.getCarPlateNumberByUserName(predicatesMap, MemberCars.class).get(0);

				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					southPoolMemberHomeToWork.setCarPlateNumber(memberCars.getCarPlateNumber());
					try {
						southPoolService.sendMessage(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberHomeToWork), southPoolSettings.getGroupChatId(), southPoolSettings);
					} catch (UnsupportedEncodingException e1) {
						log.error("",e1);
					}	
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					southPoolMemberWorkToHome.setCarPlateNumber(memberCars.getCarPlateNumber());
					try {
						southPoolService.sendMessage(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberWorkToHome), southPoolSettings.getGroupChatId(), southPoolSettings);
					} catch (UnsupportedEncodingException e1) {
						log.error("",e1);
					}
				}
				answerCallbackQuery.setText(SouthPoolConstantMessage.RESQUEST_POSTED);
				sendMessage(answerCallbackQuery);

				message.setText(SouthPoolConstantMessage.SELECT_INFO);
				message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
				sendMessage(message);
				break;

			case CallBackContants.SHOW_MEMBER_INFO:
				message.setText(SouthPoolConstantMessage.SELECT_INFO);
				message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
				sendMessage(message);
				break;

			case CallBackContants.REGISTER_MEMBER:
			case CallBackContants.UPDATE_MEMBER_INFO:
				if (southPoolMemberHomeToWork == null) {
					southPoolMemberHomeToWork = new SouthPoolMemberHomeToWork();
					southPoolMemberHomeToWork.setUsername(username);
					southPoolMemberHomeToWork.setPostCount(0);
					persistenceService.persist(southPoolMemberHomeToWork);	
				}
				if (southPoolMemberWorkToHome == null) {
					southPoolMemberWorkToHome = new SouthPoolMemberWorkToHome();
					southPoolMemberWorkToHome.setUsername(username);
					southPoolMemberWorkToHome.setPostCount(0);
					persistenceService.persist(southPoolMemberWorkToHome);	
				}
				message.setText("Please select information to use, register or update :");
				message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
				sendMessage(message);
				break;

			case CallBackContants.DRIVER:
				if (SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE.equals(previousMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					southPoolMemberHomeToWork.setYouAre(CallBackContants.DRIVER);
					persistenceService.merge(southPoolMemberHomeToWork);
					message.setText("Your account was successfully updated to a DRIVER!");
					sendMessage(message);
					sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberHomeToWork, previousMessage, username, PreviousMessage.class));
					if (!isInfoNotComplete(southPoolMemberHomeToWork)) {

						builder.setChatId(chatId).setParse("HTML").setText(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
						sendMessage(builder.build());	
					}
					break;
				}
				else if (SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE.equals(previousMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					southPoolMemberWorkToHome.setYouAre(CallBackContants.DRIVER);
					persistenceService.merge(southPoolMemberWorkToHome);
					message.setText("Your account was successfully updated to a DRIVER!");
					sendMessage(message);
					sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberWorkToHome, previousMessage, username, PreviousMessage.class));
					if (!isInfoNotComplete(southPoolMemberWorkToHome)) {

						builder.setChatId(chatId).setParse("HTML").setText(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
						sendMessage(builder.build());	
					}
					break;
				}
				break;

			case CallBackContants.PASSENGER:	
				if (SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE.equals(previousMessage.getPrevMessage()) && CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					southPoolMemberHomeToWork.setYouAre(CallBackContants.PASSENGER);
					persistenceService.merge(southPoolMemberHomeToWork);
					message.setText("Your account was successfully updated to a PASSENGER!");
					sendMessage(message);
					sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberHomeToWork, previousMessage, username, PreviousMessage.class));
					if (!isInfoNotComplete(southPoolMemberHomeToWork)) {

						builder.setChatId(chatId).setParse("HTML").setText(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberHomeToWork)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
						sendMessage(builder.build());	
					}
					break;
				}
				else if (SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE.equals(previousMessage.getPrevMessage()) && CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					southPoolMemberWorkToHome.setYouAre(CallBackContants.PASSENGER);
					persistenceService.merge(southPoolMemberWorkToHome);
					message.setText("Your account was successfully updated to a PASSENGER!");
					sendMessage(message);
					sendMessage(continuousSaveAndSendMessage(message, replyKeyboardMarkup, southPoolMemberWorkToHome, previousMessage, username, PreviousMessage.class));
					if (!isInfoNotComplete(southPoolMemberWorkToHome)) {

						builder.setChatId(chatId).setParse("HTML").setText(SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberWorkToHome)+SouthPoolConstantMessage.PLEASE_CHOOSE_ACTION);
						sendMessage(builder.build());	
					}
					break;
				}
				break;

			case CallBackContants.SET_NAME:

				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_YOUR_NAME, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_FB_PROFILE_LINK:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_FACEBOOK_PROFILE_LINK, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_MOBILE:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_MOBILE_NUMBER, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_CAR_PLATE:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_CAR_PLATE_NUMBER, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_YOU_ARE:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForDriverOrPassenger());
				sendMessage(message);
				break;

			case CallBackContants.SET_PICKUP_LOC:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_PICK_UP_LOCATION, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_DROP_OFF_LOC:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_DROP_OFF_LOCATION, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_ROUTE:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_ROUTE, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SET_AVAILABLE_SEAT:
				SendMessage seatMessage = new SendMessage().setChatId(chatId);
				ReplyKeyboardMarkup seatReplyKeyboardMarkup = new ReplyKeyboardMarkup();
				seatMessage.setReplyMarkup(seatReplyKeyboardMarkup);
				seatReplyKeyboardMarkup.setSelective(true);
				seatReplyKeyboardMarkup.setResizeKeyboard(true);
				seatReplyKeyboardMarkup.setOneTimeKeyboard(true);
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_SEAT, previousMessage, username, PreviousMessage.class);
				seatMessage.setText(botQuestion);
				seatReplyKeyboardMarkup.setKeyboard(seatKeyBoard);
				sendMessage(seatMessage);
				break;

			case CallBackContants.SET_ETA:
				SendMessage etaMessage = new SendMessage().setChatId(chatId);
				ReplyKeyboardMarkup etaReplyKeyboardMarkup = new ReplyKeyboardMarkup();
				etaMessage.setReplyMarkup(etaReplyKeyboardMarkup);
				etaReplyKeyboardMarkup.setSelective(true);
				etaReplyKeyboardMarkup.setResizeKeyboard(true);
				etaReplyKeyboardMarkup.setOneTimeKeyboard(true);
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_ETA, previousMessage, username, PreviousMessage.class);
				etaMessage.setText(botQuestion);
				etaReplyKeyboardMarkup.setKeyboard(timeKeyBoard);
				sendMessage(etaMessage);
				break;

			case CallBackContants.SET_ETD:
				SendMessage etdMessage = new SendMessage().setChatId(chatId);
				ReplyKeyboardMarkup etdReplyKeyboardMarkup = new ReplyKeyboardMarkup();
				etdMessage.setReplyMarkup(etdReplyKeyboardMarkup);
				etdReplyKeyboardMarkup.setSelective(true);
				etdReplyKeyboardMarkup.setResizeKeyboard(true);
				etdReplyKeyboardMarkup.setOneTimeKeyboard(true);
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_WAITING, previousMessage, username, PreviousMessage.class);
				etdMessage.setText(botQuestion);
				etdReplyKeyboardMarkup.setKeyboard(waitingTimeKeyBoard);
				sendMessage(etdMessage);
				break;

			case CallBackContants.SET_CUSTOM_MESSAGE:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.ENTER_SPECIAL_MESSAGE, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;

			case CallBackContants.SEARCH_POST:
				builderSearch.setChatId(chatId).setText("Search for Driver or Passenger \nPlease Choose action:");
				sendMessage(builderSearch.build());
				break;

			case CallBackContants.POST_REQUEST:
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					message.setText("HOME to WORK Information\n"+SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberHomeToWork));
					sendMessage(message);
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					message.setText("WORK to HOME Information\n"+SouthPoolConstantMessage.showOrPostMyInformation(southPoolMemberWorkToHome));
					sendMessage(message);	
				}
				builderPost.setChatId(chatId).setText("Please review your information above before posting your request.\nPlease Choose action:");
				sendMessage(builderPost.build());
				break;

			case CallBackContants.POST_AS_DRIVER:
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberHomeToWork.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
					postRequestAsDriver(message,predicatesMap,southPoolMemberHomeToWork,username, answerCallbackQuery);
					southPoolMemberHomeToWork.setPostCount(southPoolMemberHomeToWork.getPostCount() == 0 ? 1 : southPoolMemberHomeToWork.getPostCount() + 1);
					persistenceService.merge(southPoolMemberHomeToWork);
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberWorkToHome.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberWorkToHome.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberWorkToHome.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberWorkToHome.setEta(eta);
						southPoolMemberWorkToHome.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberWorkToHome.setEta(eta);
						southPoolMemberWorkToHome.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
					postRequestAsDriver(message,predicatesMap,southPoolMemberWorkToHome,username, answerCallbackQuery);
					southPoolMemberWorkToHome.setPostCount(southPoolMemberWorkToHome.getPostCount() == 0 ? 1 : southPoolMemberWorkToHome.getPostCount() + 1);
					persistenceService.merge(southPoolMemberWorkToHome);
				}
				break;

			case CallBackContants.POST_AS_PASSENGER:
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberHomeToWork.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
					postRequestAsPassenger(message,predicatesMap,southPoolMemberHomeToWork,username, answerCallbackQuery);
					southPoolMemberHomeToWork.setPostCount(southPoolMemberHomeToWork.getPostCount() == 0 ? 1 : southPoolMemberHomeToWork.getPostCount() + 1);
					persistenceService.merge(southPoolMemberHomeToWork);
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberWorkToHome.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberWorkToHome.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberWorkToHome.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberWorkToHome.setEta(eta);
						southPoolMemberWorkToHome.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberWorkToHome.setEta(eta);
						southPoolMemberWorkToHome.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
					postRequestAsPassenger(message,predicatesMap,southPoolMemberWorkToHome,username, answerCallbackQuery);
					southPoolMemberWorkToHome.setPostCount(southPoolMemberWorkToHome.getPostCount() == 0 ? 1 : southPoolMemberWorkToHome.getPostCount() + 1);
					persistenceService.merge(southPoolMemberWorkToHome);
				}
				break;

			case CallBackContants.POST_AS_DRIVER_TOMORROW:
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberHomeToWork.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);

					if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isEqual(DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						LocalDateTime localDateTimeETA = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta());
						LocalDateTime localDateTimeETD = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEtd());
						String dateETA = localDateTimeETA.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						String dateETD = localDateTimeETD.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						southPoolMemberHomeToWork.setEta(dateETA);
						southPoolMemberHomeToWork.setEtd(dateETD);
						persistenceService.merge(southPoolMemberHomeToWork);
					}

					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
					postRequestAsDriver(message,predicatesMap,southPoolMemberHomeToWork,username, answerCallbackQuery);
					southPoolMemberHomeToWork.setPostCount(southPoolMemberHomeToWork.getPostCount() == 0 ? 1 : southPoolMemberHomeToWork.getPostCount() + 1);
					persistenceService.merge(southPoolMemberHomeToWork);
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberWorkToHome.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberWorkToHome.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberWorkToHome.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberWorkToHome.setEta(eta);
						southPoolMemberWorkToHome.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);

					if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isEqual(DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						LocalDateTime localDateTimeETA = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta());
						LocalDateTime localDateTimeETD = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEtd());
						String dateETA = localDateTimeETA.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						String dateETD = localDateTimeETD.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						southPoolMemberWorkToHome.setEta(dateETA);
						southPoolMemberWorkToHome.setEtd(dateETD);
						persistenceService.merge(southPoolMemberWorkToHome);
					}

					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
					postRequestAsDriver(message,predicatesMap,southPoolMemberWorkToHome,username, answerCallbackQuery);
					southPoolMemberWorkToHome.setPostCount(southPoolMemberWorkToHome.getPostCount() == 0 ? 1 : southPoolMemberWorkToHome.getPostCount() + 1);
					persistenceService.merge(southPoolMemberWorkToHome);
				}
				break;

			case CallBackContants.POST_AS_PASSENGER_TOMORROW:
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberHomeToWork.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberHomeToWork.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberHomeToWork.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);

					if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isEqual(DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						LocalDateTime localDateTimeETA = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEta());
						LocalDateTime localDateTimeETD = DateUtility.toLocaDateTime(southPoolMemberHomeToWork.getEtd());
						String dateETA = localDateTimeETA.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						String dateETD = localDateTimeETD.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						southPoolMemberHomeToWork.setEta(dateETA);
						southPoolMemberHomeToWork.setEtd(dateETD);
						persistenceService.merge(southPoolMemberHomeToWork);
					}

					southPoolMemberHomeToWork = persistenceService.getMember(username, SouthPoolMemberHomeToWork.class);
					postRequestAsPassenger(message,predicatesMap,southPoolMemberHomeToWork,username, answerCallbackQuery);
					southPoolMemberWorkToHome.setPostCount(southPoolMemberWorkToHome.getPostCount() == 0 ? 1 : southPoolMemberWorkToHome.getPostCount() + 1);
					persistenceService.merge(southPoolMemberWorkToHome);
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					if (southPoolMemberWorkToHome.getPostCount() == 5) {
						String warning = EmojiParser.parseToUnicode(":warning:");
						answerCallbackQuery.setText(warning+SouthPoolConstantMessage.RESQUEST_MAX);
						sendMessage(answerCallbackQuery);
						break;
					}
					if (DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						String dateToday = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						String[] etaTime = southPoolMemberWorkToHome.getEta().split(" ");
						String eta = dateToday + " " + etaTime[1];

						String[] etdTime = southPoolMemberWorkToHome.getEta().split(" ");
						String etd = dateToday + " " + etdTime[1];

						southPoolMemberHomeToWork.setEta(eta);
						southPoolMemberHomeToWork.setEtd(etd);
						persistenceService.merge(southPoolMemberHomeToWork);
					}
					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);

					if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isEqual(DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
						LocalDateTime localDateTimeETA = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEta());
						LocalDateTime localDateTimeETD = DateUtility.toLocaDateTime(southPoolMemberWorkToHome.getEtd());
						String dateETA = localDateTimeETA.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						String dateETD = localDateTimeETD.plusDays(1).format(DateUtility.FORMAT_DATETIME);
						southPoolMemberWorkToHome.setEta(dateETA);
						southPoolMemberWorkToHome.setEtd(dateETD);
						persistenceService.merge(southPoolMemberWorkToHome);	
					}
					southPoolMemberWorkToHome = persistenceService.getMember(username, SouthPoolMemberWorkToHome.class);
					postRequestAsPassenger(message,predicatesMap,southPoolMemberWorkToHome,username, answerCallbackQuery);
					southPoolMemberWorkToHome.setPostCount(southPoolMemberWorkToHome.getPostCount() == 0 ? 1 : southPoolMemberWorkToHome.getPostCount() + 1);
					persistenceService.merge(southPoolMemberWorkToHome);
				}
				break;

			case CallBackContants.TODAY_DRIVER:
				message.setText(SouthPoolConstantMessage.SEARCH_DRIVER);
				sendMessage(message);
				predicatesMap = new HashMap<>();
				predicatesMap.put("username", username);
				predicatesMap.put("youAre", CallBackContants.DRIVER);
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					List<Member> members = southpoolSearchService.getSouthPoolMemberHomeToWorkMembers(predicatesMap);
					try {
						southpoolSearchService.sendResponseDetailsInTelegram("HOME to WORK DRIVER from " + southPoolMemberHomeToWork.getEta().replaceAll(":", "") + " to " + southPoolMemberHomeToWork.getEtd().replaceAll(":", ""), members, chatId, CallBackContants.TODAY_DRIVER);
					} catch (IOException | DocumentException e) {
						log.error("{}", e);
					}
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					List<Member> members = southpoolSearchService.getSouthPoolMemberWorkToHomeMembers(predicatesMap);
					try {
						southpoolSearchService.sendResponseDetailsInTelegram("WORK to HOME DRIVER from " + southPoolMemberWorkToHome.getEta().replaceAll(":", "") + " to " + southPoolMemberWorkToHome.getEtd().replaceAll(":", ""), members, chatId, CallBackContants.TODAY_DRIVER);
					} catch (IOException | DocumentException e) {
						log.error("{}", e);
					}
				}
				break;

			case CallBackContants.TODAY_PASSENGER:
				message.setText(SouthPoolConstantMessage.SEARCH_PASSENGER);
				sendMessage(message);
				predicatesMap = new HashMap<>();
				predicatesMap.put("username", username);
				predicatesMap.put("youAre", CallBackContants.PASSENGER);
				if (CallBackContants.HOME_TO_WORK_INFO.equals(previousMessage.getTag())) {
					List<Member> members = southpoolSearchService.getSouthPoolMemberHomeToWorkMembers(predicatesMap);
					try {
						southpoolSearchService.sendResponseDetailsInTelegram("HOME to WORK PASSENGER from " + southPoolMemberHomeToWork.getEta().replaceAll(":", "") + " to " + southPoolMemberHomeToWork.getEtd().replaceAll(":", ""), members, chatId, CallBackContants.TODAY_PASSENGER);
					} catch (IOException | DocumentException e) {
						log.error("{}", e);
					}
				}
				else if (CallBackContants.WORK_TO_HOME_INFO.equals(previousMessage.getTag())) {
					List<Member> members = southpoolSearchService.getSouthPoolMemberWorkToHomeMembers(predicatesMap);
					try {
						southpoolSearchService.sendResponseDetailsInTelegram("WORK to HOME PASSENGER from " + southPoolMemberWorkToHome.getEta().replaceAll(":", "") + " to " + southPoolMemberWorkToHome.getEtd().replaceAll(":", ""), members, chatId, CallBackContants.TODAY_PASSENGER);
					} catch (IOException | DocumentException e) {
						log.error("{}", e);
					}
				}
				break;

			case CallBackContants.VERIFY_MEMBER:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.VERIFY_MEMBER, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;
				
			case CallBackContants.REPORT_TRAFFIC:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.REPORT_TRAFFIC_STATUS, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;
				
			case CallBackContants.BAN_MEMBER:
				if ("Y".equalsIgnoreCase(southPoolMemberHomeToWork.getAdmin()) && "Y".equalsIgnoreCase(southPoolMemberWorkToHome.getAdmin())) {
					botQuestion = saveAndSendMessage(SouthPoolConstantMessage.BAN_MEMBER_TO_USE_THE_BOT, previousMessage, username, PreviousMessage.class);
					message.setText(botQuestion);
					sendMessage(message);	
				}
				else {
					String x = EmojiParser.parseToUnicode(":x:");
					answerCallbackQuery.setText(x+SouthPoolConstantMessage.ADMIN_ONLY);
					sendMessage(answerCallbackQuery);
				}
				break;
				
			case CallBackContants.COMPLAIN_MEMBER:
				botQuestion = saveAndSendMessage(SouthPoolConstantMessage.COMPLAIN_MEMBER_PASSENGER_OR_DRIVER, previousMessage, username, PreviousMessage.class);
				message.setText(botQuestion);
				sendMessage(message);
				break;
				
			case CallBackContants.TOMORROW_DRIVER:
				message.setText(SouthPoolConstantMessage.FEATURE_NOT_AVAILABLE_YET);
				sendMessage(message);
				break;

			case CallBackContants.TOMORROW_PASSENGER:
				message.setText(SouthPoolConstantMessage.FEATURE_NOT_AVAILABLE_YET);
				sendMessage(message);
				break;

			case CallBackContants.BOT_UPDATE:
				try {
					southPoolService.sendMessage(constantMessage.showThankUpdate(), southPoolSettings.getGroupChatId(), southPoolSettings);
				} catch (UnsupportedEncodingException e) {
					log.error("",e);
				}
				break;

			case CallBackContants.CANCEL:
				message.setText(constantMessage.showThankYou());
				sendMessage(message);
				break;

			default:
				message.setText("Invalid command!");
				sendMessage(message);
				break;
			}			
		}
	}

	@Override
	public String getBotUsername() {
		return southPoolSettings.getTelegramBotUsername();
	}

	@Override
	public String getBotToken() {
		return southPoolSettings.getTelegramBotToken();
	}

	protected void sendMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	protected void sendMessage(AnswerCallbackQuery answerCallbackQuery) {
		try {
			execute(answerCallbackQuery);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	protected void replaceMessage(long chatId, long messageId, SendMessage message) {
		EditMessageText newMessage = new EditMessageText();
		newMessage.setChatId(chatId);
		newMessage.setMessageId(Math.toIntExact(messageId));
		newMessage.setText(message.getText());
		try {
			execute(newMessage);
		} catch (TelegramApiException e) {
			log.error("{}",e);
		}
	}

	protected boolean isInfoNotComplete(Member southPoolMemberHomeToWork) {
		if(southPoolMemberHomeToWork.getName() == null || 
				southPoolMemberHomeToWork.getFacebookProfileLink() == null || 
				southPoolMemberHomeToWork.getMobileNumber() ==  null || 
				southPoolMemberHomeToWork.getCarPlateNumber() ==  null || 
				southPoolMemberHomeToWork.getYouAre() == null || 
				southPoolMemberHomeToWork.getPicUpLoc() == null || 
				southPoolMemberHomeToWork.getDropOffLoc() == null ||
				southPoolMemberHomeToWork.getRoute() == null ||
				southPoolMemberHomeToWork.getAvailableSlots() == null || 
				southPoolMemberHomeToWork.getEta() == null ||
				southPoolMemberHomeToWork.getEtd() == null || 
				southPoolMemberHomeToWork.getCustomMessage() == null) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected String saveAndSendMessage(String botQuestion, PreviousMessage previousMessage, String username, @SuppressWarnings("rawtypes") Class clazz) {
		previousMessage = (PreviousMessage) persistenceService.getMember(username, clazz);
		if (previousMessage == null) {
			previousMessage = new PreviousMessage();
			previousMessage.setUsername(username);
			previousMessage.setPrevMessage(botQuestion);
			persistenceService.persist(previousMessage);
		}
		else {
			previousMessage.setPrevMessage(botQuestion);
			persistenceService.merge(previousMessage);
		}
		return botQuestion;
	}

	private SendMessage continuousSaveAndSendMessage(SendMessage message, ReplyKeyboardMarkup replyKeyboardMarkup, Member member, PreviousMessage previousMessage, String username, @SuppressWarnings("rawtypes") Class clazz) {
		String botQuestion = saveAndSendMessage(SouthPoolConstantMessage.showInfoToUpdateNext(member), previousMessage, username, clazz);
		message.setText(botQuestion);
		if(SouthPoolConstantMessage.ENTER_WHAT_TYPE_OF_USER_YOU_ARE.equals(botQuestion)) {
			message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForDriverOrPassenger());
		}
		else if(SouthPoolConstantMessage.ENTER_SEAT.equals(botQuestion)) {
			replyKeyboardMarkup.setKeyboard(seatKeyBoard);
		}
		else if (SouthPoolConstantMessage.ENTER_ETA.equals(botQuestion)) {
			replyKeyboardMarkup.setKeyboard(timeKeyBoard);
		}
		else if (SouthPoolConstantMessage.ENTER_WAITING.equals(botQuestion)) {
			replyKeyboardMarkup.setKeyboard(waitingTimeKeyBoard);
		}
		return message;
	}

	private boolean postRequestAsDriver(SendMessage message, Map<String,String> predicatesMap, Member member, String username, AnswerCallbackQuery answerCallbackQuery) {
		if(isInfoNotComplete(member)) {
			message.setText("Sorry, you are not allowed to post your request as a DRIVER because your information is not yet complete.\n\n" + constantMessage.showInfoToUpdate(member));
			sendMessage(message);
			return false;
		}
		else if (CallBackContants.PASSENGER.equals(member.getYouAre())) {
			message.setText("Sorry, you are not allowed to post your request as a DRIVER because your account is registered as a PASSENGER. Kindly update your account to PASSENGER by using /setyouare command. Thank you!\n\n");
			sendMessage(message);
			return false;
		}
		else {
			//			predicatesMap = new HashMap<>();
			//			predicatesMap.put("username", username);
			//			List<MemberCars> memberCarPlateNumbers = persistenceService.getCarPlateNumberByUserName(predicatesMap, MemberCars.class);
			//
			//			if (memberCarPlateNumbers.size()>1) {
			//				InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
			//				List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
			//				List<InlineKeyboardButton> rowInline = new ArrayList<>();
			//				for (MemberCars plate : memberCarPlateNumbers) {
			//					rowInline.add(new InlineKeyboardButton().setText(CallBackContants.CAR+"-"+plate.getCarPlateNumber()).setCallbackData(CallBackContants.CAR+"-"+plate.getCarPlateNumber()));
			//				}
			//				rowsInline.add(rowInline);// Set the keyboard to the markup
			//				markupInline.setKeyboard(rowsInline);
			//				message.setText("Please select which of your car you would like to use in carpooling : \n");
			//				message.setReplyMarkup(markupInline);
			//				sendMessage(message);
			//			}
			//			else 
			if (DateUtility.toLocaDateTime(member.getEta()).isAfter(DateUtility.toLocaDateTime(member.getEtd()))) {
				StringBuilder sb = new StringBuilder();
				String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
				String etd = EmojiParser.parseToUnicode(":airplane_departure:");
				sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
				sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
				sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
				message.setText(sb.toString());
				sendMessage(message);
				return false;
			}
			else {
				try {
					southPoolService.sendMessage(SouthPoolConstantMessage.showOrPostMyInformation(member), southPoolSettings.getGroupChatId(), southPoolSettings);
				} catch (UnsupportedEncodingException e) {
					log.error("",e);
				}

				String ok = EmojiParser.parseToUnicode(":white_check_mark:");
				answerCallbackQuery.setText(ok+SouthPoolConstantMessage.RESQUEST_POSTED);
				sendMessage(answerCallbackQuery);

				message.setText(SouthPoolConstantMessage.SELECT_INFO);
				message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
				sendMessage(message);
				return true;
			}
		}
	}

	private boolean postRequestAsPassenger(SendMessage message, Map<String,String> predicatesMap, Member member, String username, AnswerCallbackQuery answerCallbackQuery) {
		if(isInfoNotComplete(member)) {
			message.setText("Sorry, you are not allowed to post your request as a PASSENGER because your information is not yet complete.\n\n" + constantMessage.showInfoToUpdate(member));
			sendMessage(message);
			return false;
		}
		else if (CallBackContants.DRIVER.equals(member.getYouAre())) {
			message.setText("Sorry, you are not allowed to post your request as a PASSENGER because your account is registered as a DRIVER. Kindly update your account to DRIVER by using /setyouare command. Thank you!\n\n");
			sendMessage(message);
			return false;
		}
		else if (DateUtility.toLocaDateTime(member.getEta()).isAfter(DateUtility.toLocaDateTime(member.getEtd()))) {
			StringBuilder sb = new StringBuilder();
			String eta = EmojiParser.parseToUnicode(":airplane_arrival:");
			String etd = EmojiParser.parseToUnicode(":airplane_departure:");
			sb.append("ETA is cannot be ahead than your ETD! Please update your ETA and ETD by sending these commands : \n\n");
			sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
			sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from pickup point.\n\n");
			message.setText(sb.toString());
			sendMessage(message);
			return false;
		}
		else {
			try {
				southPoolService.sendMessage(SouthPoolConstantMessage.showOrPostMyInformation(member), southPoolSettings.getGroupChatId(), southPoolSettings);
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}

			String ok = EmojiParser.parseToUnicode(":white_check_mark:");
			answerCallbackQuery.setText(ok+SouthPoolConstantMessage.RESQUEST_POSTED);
			sendMessage(answerCallbackQuery);

			message.setText(SouthPoolConstantMessage.SELECT_INFO);
			message.setReplyMarkup(SouthPoolConstantMessage.shownOptionsForWorkAndHomeInfo());
			sendMessage(message);
			return true;
		}
	}

}

