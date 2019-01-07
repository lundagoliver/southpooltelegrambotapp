package com.systems.community.carpooling.southpool.utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.systems.community.carpooling.southpool.entities.Member;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberHomeToWork;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberWorkToHome;
import com.vdurmont.emoji.EmojiParser;

@Component
public class SouthPoolConstantMessage {
	
	public static final String ENTER_YOUR_NAME = "\n\nPlease enter your name :";
	public static final String ENTER_FACEBOOK_PROFILE_LINK = "Please enter your profile link :";
	public static final String ENTER_MOBILE_NUMBER = "Please enter your mobile number :";
	public static final String ENTER_CAR_PLATE_NUMBER = "Please enter your car plate number :";
	public static final String ENTER_WHAT_TYPE_OF_USER_YOU_ARE = "Are you a DRIVER or a PASSENGER ?";
	public static final String ENTER_PICK_UP_LOCATION = "Enter your pick up location :";
	public static final String ENTER_DROP_OFF_LOCATION = "Enter your drop off location :";
	public static final String ENTER_ROUTE = "Enter your route :";
	public static final String ENTER_SEAT = "How many seats would you like to offer/needed ?";
	public static final String ENTER_ETA = "Enter your estimated time of arrival to your pick up point :";
	public static final String ENTER_WAITING = "Enter your estimated waiting time before departure from your pick up point :";
	public static final String ENTER_SPECIAL_MESSAGE = "Enter your special message or instructions. Example: \"Please PM me for more questions.\" :";

	public static final String UPDATED = "Please review your information before posting. Thank you!";

	public static final String RESQUEST_POSTED = "Your request was successfully posted in SOUTHPOOL telegram group carpooling community.Thank you!\n\n";
	public static final String REPORT_POSTED = "Your report was successfully posted in SOUTHPOOL telegram group carpooling community.Thank you!\n\n";
	public static final String RESQUEST_MAX = "Sorry, you have reached the maximum allowable limit to post a request for this day.You may try again tomorrow.Thank you!\n\n";

	public static final String FEATURE_NOT_AVAILABLE_YET = "This feature will be available on future releases. Thank you for your patience.\n";
	
	public static final String VERIFY_MEMBER = "Please enter the username of the member that you want to verify :\n";
	public static final String REPORT_TRAFFIC_STATUS = "Please enter any traffic related information that you want to share in the group.\n\n Example:\n oil price hike/rollback, traffic status or any MMDA operations:\n";
	
	public static final String SEARCH_DRIVER = "Searching for DRIVER. Please wait...";
	public static final String SEARCH_PASSENGER = "Searching for PASSENGER. Please wait...";
	
	public static final String SELECT_INFO = "Please select which information to use:";
	public static final String PLEASE_CHOOSE_ACTION = "\nPlease Choose action:";

	public String showIntro() {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("What can this bot do?").append("\n");
		messageBuilder.append("@southpoolservicebot is responsible to help you find people who would like to share their car journeys so that more than one person travels in a car, and prevents the need for others to have to drive to a location themselves.").append("\n");
		messageBuilder.append("By having more people using one vehicle, carpooling reduces each person's travel costs such as: fuel costs, tolls, and the stress of driving. Carpooling is also a more environmentally friendly and sustainable way to travel as sharing journeys reduces air pollution, carbon emissions, traffic congestion on the roads, and the need for parking spaces.").append("\n\n");
		messageBuilder.append("Contact Technical/Developer if you have questions about the bot.\n");
		messageBuilder.append("Telegram : @OliverDela_cruzLundag\n");
		return messageBuilder.toString();
	}

	public String showThankYou() {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Thank you for using @southpoolservicebot").append("\n");
		messageBuilder.append("Contact Technical/Developer if you have questions about the bot.\n");
		messageBuilder.append("We'd love to hear all your feedback and suggestions.").append("\n");
		messageBuilder.append("Telegram : @OliverDela_cruzLundag\n");
		return messageBuilder.toString();
	}
	
	public String showThankUpdate() {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Hi SOUTHPOOL members,").append("\n");
		messageBuilder.append("Good AM :)\n");
		messageBuilder.append("Happy to share with you some of the @southpoolservicebot functionality updates.\n\n");
		messageBuilder.append("1. Verify feature is already available. You can now verify your account or other members account using the bot.\n\n");
		//messageBuilder.append("2. Fix duplicate Time when displaying member info after editing.\n");
		messageBuilder.append(" \n");
		messageBuilder.append("Contact Technical/Developer if you have any questions, concerns or issues about the bot.\n");
		messageBuilder.append("Telegram : @OliverDela_cruzLundag\n");
		messageBuilder.append("Thank you for your cooperation.\n");
		return messageBuilder.toString();
	}


	public String userNameNotYetSetMessage() {
		String android = EmojiParser.parseToUnicode(":robot_face:");
		String iphone = EmojiParser.parseToUnicode(":iphone:");
		StringBuilder sb = new StringBuilder();
		sb.append("Sorry, but you need to set your username first before you can use this service.\n\n");
		sb.append(android);
		sb.append("Steps on how to set username in android device >> https://www.wikihow.com/Change-Your-Name-on-Telegram-on-Android \n");
		sb.append(iphone);
		sb.append("Steps on How to set username in ios device https://www.wikihow.com/Know-a-Chat-ID-on-Telegram-on-iPhone-or-iPad \n");
		return sb.toString();
	}

	public String notRegisteredMemberMessage() {
		String greyExclamation = EmojiParser.parseToUnicode(":grey_exclamation:");
		StringBuilder sb = new StringBuilder();
		sb.append(greyExclamation);
		sb.append("Sorry, but you need to register first before you can use this service.\n\n");
		return sb.toString();
	}

	public InlineKeyboardMarkup shownOptions() {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		String register = EmojiParser.parseToUnicode(":information_source: Register");
		String cancel = EmojiParser.parseToUnicode(":iphone: Help");
		rowInline.add(new InlineKeyboardButton().setText(register).setCallbackData("register_member"));
		rowInline.add(new InlineKeyboardButton().setText(cancel).setCallbackData("cancel"));
		rowsInline.add(rowInline);// Set the keyboard to the markup
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

	public static InlineKeyboardMarkup shownOptionsForDriverOrPassenger() {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		String driver = EmojiParser.parseToUnicode(":formula_one: " + CallBackContants.DRIVER);
		String passenger = EmojiParser.parseToUnicode(":slightly_smiling:" + CallBackContants.PASSENGER);
		rowInline.add(new InlineKeyboardButton().setText(driver).setCallbackData(CallBackContants.DRIVER));
		rowInline.add(new InlineKeyboardButton().setText(passenger).setCallbackData(CallBackContants.PASSENGER));
		rowsInline.add(rowInline);// Set the keyboard to the markup
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

	public static InlineKeyboardMarkup shownOptionsForWorkAndHomeInfo() {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		String driver = EmojiParser.parseToUnicode(":house: :cityscape: " + "HOME to WORK");
		String passenger = EmojiParser.parseToUnicode(":cityscape: :house: " + "WORK to HOME");
		rowInline.add(new InlineKeyboardButton().setText(driver).setCallbackData(CallBackContants.HOME_TO_WORK_INFO));
		rowInline.add(new InlineKeyboardButton().setText(passenger).setCallbackData(CallBackContants.WORK_TO_HOME_INFO));
		rowsInline.add(rowInline);// Set the keyboard to the markup
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

	public String showCommandsForRegistration() {
		String setName = EmojiParser.parseToUnicode(":information_source: ");
		String setFaceBookProfile = EmojiParser.parseToUnicode(":computer: ");
		String setMobileNumber = EmojiParser.parseToUnicode(":iphone: ");
		String setCarPlateNumber = EmojiParser.parseToUnicode(":card_file_box: ");	
		String setYouAre = EmojiParser.parseToUnicode(":grey_question: ");
		String setPickUpLocation = EmojiParser.parseToUnicode(":taxi: ");
		String setDropOffLocation = EmojiParser.parseToUnicode(":thumbsup: ");
		String setRoute = EmojiParser.parseToUnicode(":highway: ");
		String setAvailableSeat = EmojiParser.parseToUnicode(":seat: ");
		String eta = EmojiParser.parseToUnicode(":airplane_arrival: ");
		String etd = EmojiParser.parseToUnicode(":airplane_departure: ");
		String myInfo = EmojiParser.parseToUnicode(":memo: ");
		String specialInstruction = EmojiParser.parseToUnicode(":heavy_exclamation_mark: ");
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("You can register or update your HOME to WORK information by sending these commands: \n\n");
		messageBuilder.append(setName).append(SouthPoolCommands.SET_NAME).append(" - register or update your name.\n\n");
		messageBuilder.append(setYouAre).append(SouthPoolCommands.SET_YOU_ARE).append(" - register or update whether you are a DRIVER or a PASSENGER.\n\n");
		messageBuilder.append(setFaceBookProfile).append(SouthPoolCommands.SET_FB_PROFILE_LINK).append(" - register or update your facebook profile link. You can get your facebook profile link by Openning Facebook on your mobile device, navigate to the profile page, and click More. Select Copy Link to Profile. Now that you have copied the URL, you can paste it here.\n\n");;
		messageBuilder.append(setMobileNumber).append(SouthPoolCommands.SET_MOBILE).append(" - register or update yout mobile contact number.\n\n");
		messageBuilder.append(setCarPlateNumber).append(SouthPoolCommands.SET_CAR_PLATE).append(" - register or update your car plate number.\n\n");
		messageBuilder.append(setPickUpLocation).append(SouthPoolCommands.SET_PICKUP_LOC).append("- register or update your pick up location.\n\n");
		messageBuilder.append(setDropOffLocation).append(SouthPoolCommands.SET_DROP_OFF_LOC).append(" - register or update your drop off location.\n\n");
		messageBuilder.append(setRoute).append(SouthPoolCommands.SET_ROUTE).append(" - register or update your route. Example route format :  Start Location > Route1 > Route2 > Route3 > End Location \n\n");
		messageBuilder.append(setAvailableSeat).append(SouthPoolCommands.SET_AVAILABLE_SEAT).append(" - register or update your unoccupied or needed available seat.\n\n");
		messageBuilder.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
		messageBuilder.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from the pickup point.\n\n");
		messageBuilder.append(specialInstruction).append(SouthPoolCommands.SET_CUSTOM_MESSAGE).append(" - register or update your special message or instruction.\n\n");
		messageBuilder.append(myInfo).append(SouthPoolCommands.MY_INFO).append(" - show your current information.\n\n");

		return messageBuilder.toString();
	}

	public String showInfoToUpdate(Member member) {
		
		String setName = EmojiParser.parseToUnicode(":information_source: ");
		String setFaceBookProfile = EmojiParser.parseToUnicode(":computer: ");
		String setMobileNumber = EmojiParser.parseToUnicode(":iphone: ");
		String setCarPlateNumber = EmojiParser.parseToUnicode(":card_file_box: ");	
		String setYouAre = EmojiParser.parseToUnicode(":grey_question: ");
		String setPickUpLocation = EmojiParser.parseToUnicode(":taxi: ");
		String setDropOffLocation = EmojiParser.parseToUnicode(":thumbsup: ");
		String setRoute = EmojiParser.parseToUnicode(":highway: ");
		String setAvailableSeat = EmojiParser.parseToUnicode(":seat: ");
		String eta = EmojiParser.parseToUnicode(":airplane_arrival: ");
		String etd = EmojiParser.parseToUnicode(":airplane_departure: ");
		String specialInstruction = EmojiParser.parseToUnicode(":heavy_exclamation_mark: ");
		
		StringBuilder sb = new StringBuilder();
		sb.append("I believe you need to set and complete your information.\n");
		sb.append("You can update your information by sending, clicking or tapping these commands:\n\n");
		if (member.getName() == null) {
			sb.append(setName).append(SouthPoolCommands.SET_NAME).append(" - register or update your name.\n\n");
		}
		if (member.getFacebookProfileLink() == null) {
			sb.append(setFaceBookProfile).append(SouthPoolCommands.SET_FB_PROFILE_LINK).append(" - register or update your facebook profile link. You can get your facebook profile link by Openning Facebook on your mobile device, navigate to the profile page, and click More. Select Copy Link to Profile. Now that you have copied the URL, you can paste it here.\n\n");;
		}
		if (member.getMobileNumber() == null) {
			sb.append(setMobileNumber).append(SouthPoolCommands.SET_MOBILE).append(" - register or update yout mobile contact number.\n\n");
		}
		if (member.getCarPlateNumber() == null) {
			sb.append(setCarPlateNumber).append(SouthPoolCommands.SET_CAR_PLATE).append(" - register or update your car plate number.\n\n");
		}
		if (member.getYouAre() == null) {
			sb.append(setYouAre).append(SouthPoolCommands.SET_YOU_ARE).append(" - register or update whether you are a DRIVER or a PASSENGER.\n\n");
		}
		if (member.getPicUpLoc() == null) {
			sb.append(setPickUpLocation).append(SouthPoolCommands.SET_PICKUP_LOC).append("- register or update your pick up locations.\n\n");
		}
		if (member.getDropOffLoc() == null) {
			sb.append(setDropOffLocation).append(SouthPoolCommands.SET_DROP_OFF_LOC).append(" - register or update your drop off locations.\n\n");
		}
		if (member.getRoute() == null) {
			sb.append(setRoute).append(SouthPoolCommands.SET_ROUTE).append(" - register or update your route. Example route format :  Start Location > Route1 > Route2 > Route3 > End Location \n\n");
		}
		if (member.getAvailableSlots() == null) {
			sb.append(setAvailableSeat).append(SouthPoolCommands.SET_AVAILABLE_SEAT).append(" - register or update your unoccupied or needed available seat.\n\n");
		}
		if (member.getEta() == null) {
			sb.append(eta).append(SouthPoolCommands.SET_ETA).append(" - register or update your estimated time of arrival to the pickup point.\n\n");
		}
		if (member.getEtd() == null) {
			sb.append(etd).append(SouthPoolCommands.SET_ETD).append(" - register or update your estimated time of departure from the pickup point.\n\n");
		}
		if (member.getCustomMessage() == null) {
			sb.append(specialInstruction).append(SouthPoolCommands.SET_CUSTOM_MESSAGE).append(" - register or update your special message or instruction.\n\n");
		}
		return sb.toString();
	}

	public static String showInfoToUpdateNext(Member member) {

		StringBuilder sb = new StringBuilder();
		if (member.getName() == null) {
			String setName = ENTER_YOUR_NAME;
			sb.append(setName);
			return sb.toString();
		}
		else if (member.getYouAre() == null) {
			String setYouAre = ENTER_WHAT_TYPE_OF_USER_YOU_ARE;
			sb.append(setYouAre);
			return sb.toString();
		}
		else if (member.getFacebookProfileLink() == null) {
			String setFaceBookProfile = ENTER_FACEBOOK_PROFILE_LINK;
			sb.append(setFaceBookProfile);
			return sb.toString();
		}
		else if (member.getMobileNumber() == null) {
			String setMobileNumber = ENTER_MOBILE_NUMBER;
			sb.append(setMobileNumber);
			return sb.toString();
		}
		else if (member.getCarPlateNumber() == null) {
			String setCarPlateNumber = ENTER_CAR_PLATE_NUMBER;
			sb.append(setCarPlateNumber);
			return sb.toString();
		}
		else if (member.getPicUpLoc() == null) {
			String setPickUpLocation = ENTER_PICK_UP_LOCATION;
			sb.append(setPickUpLocation);
			return sb.toString();
		}
		else if (member.getDropOffLoc() == null) {
			String setDropOffLocation = ENTER_DROP_OFF_LOCATION;
			sb.append(setDropOffLocation);
			return sb.toString();
		}
		else if (member.getRoute() == null) {
			String setRoute = ENTER_ROUTE;
			sb.append(setRoute);
			return sb.toString();
		}
		else if (member.getAvailableSlots() == null) {
			String setAvailableSeat = ENTER_SEAT;
			sb.append(setAvailableSeat);
			return sb.toString();
		}
		else if (member.getEta() == null) {
			String eta = ENTER_ETA;
			sb.append(eta);
			return sb.toString();
		}
		else if (member.getEtd() == null) {
			String etd = ENTER_WAITING;
			sb.append(etd);
			return sb.toString();
		}
		else if (member.getCustomMessage() == null) {
			String specialMessage = ENTER_SPECIAL_MESSAGE;
			sb.append(specialMessage);
			return sb.toString();
		}
		return UPDATED;
	}
	
	public static String showMyInformation(Member member) {

		String etaDate = DateUtility.toLocaDateTime(member.getEta()).format(DateUtility.FORMAT_DATETIME_INFO);
		String etdDate = DateUtility.toLocaDateTime(member.getEtd()).format(DateUtility.FORMAT_DATETIME_INFO);
		
		String message = etaDate.contains("PM") ? " here later!" : " here for today!";
		
		if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(DateUtility.toLocaDateTime(member.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
			message = " here for tomorrow!";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(member.getYouAre()).append(message).append("\n");
		sb.append("Name: ").append(member.getName()).append("\n");
		sb.append("Telegram: @").append(member.getUsername()).append("\n");
		sb.append("Profile : ").append(member.getFacebookProfileLink()).append("\n");	
		sb.append("Mobile: ").append(member.getMobileNumber()).append("\n");		
		if(CallBackContants.DRIVER.equals(member.getYouAre())) {
				sb.append("Plate: ").append(member.getCarPlateNumber()).append("\n");
		}
		sb.append("Pick Up: ").append(member.getPicUpLoc()).append("\n");
		sb.append("Drop Off: ").append(member.getDropOffLoc()).append("\n");
		sb.append("Route: ").append(member.getRoute()).append("\n");
		sb.append("Seat: ").append(member.getAvailableSlots()).append("\n");
		
		sb.append("Date: ").append(etaDate.split(" ")[0]).append("\n");
		String etaTime = TimeUtility.convertToStandardTime(etaDate.split(" ")[1],etaDate.split(" ")[2]);
		String etdTime = TimeUtility.convertToStandardTime(etdDate.split(" ")[1],etdDate.split(" ")[2]);
		sb.append("Time: ").append(etaTime +" - "+ etdTime).append("\n");
		sb.append("Instruction: ").append(member.getCustomMessage()).append("\n");
		
		return sb.toString();
	}
	
	public static String verifyMember(Member member) {
		StringBuilder sb = new StringBuilder();
		sb.append("This member is verified as a " + member.getYouAre()).append("\n\n");
		sb.append("Name: ").append(member.getName()).append("\n");
		sb.append("Telegram: @").append(member.getUsername()).append("\n");
		sb.append("Profile : ").append(member.getFacebookProfileLink()).append("\n");	
		sb.append("Mobile: ").append(member.getMobileNumber()).append("\n");		
		if(CallBackContants.DRIVER.equals(member.getYouAre())) {
				sb.append("Plate: ").append(member.getCarPlateNumber()).append("\n");
		}
		return sb.toString();
	}


	public static String showOrPostMyInformation(Member member) {
		
		List<String> notAvailable = new ArrayList<>();
		notAvailable.add("N/A");
		notAvailable.add("N/a");
		notAvailable.add("n/A");
		notAvailable.add("n/a");
		
		notAvailable.add("NA");
		notAvailable.add("na");
		notAvailable.add("Na");
		notAvailable.add("nA");
		
		notAvailable.add("NO");
		notAvailable.add("No");
		notAvailable.add("no");
		notAvailable.add("nO");
		
		notAvailable.add("None");
		notAvailable.add("none");
		notAvailable.add("Dont have");
		notAvailable.add("Not applicable");
		notAvailable.add(".");
		notAvailable.add("-");
		notAvailable.add("~");
		notAvailable.add("Null");
		notAvailable.add("null");
		notAvailable.add("No facebook");
		notAvailable.add("Not public on FB");
		notAvailable.add("Pass");
		notAvailable.add("I do not have facebook");
		
		String etaDate = DateUtility.toLocaDateTime(member.getEta()).format(DateUtility.FORMAT_DATETIME_INFO);
		String etdDate = DateUtility.toLocaDateTime(member.getEtd()).format(DateUtility.FORMAT_DATETIME_INFO);
		
		String message = etaDate.contains("PM") ? " here later!" : " here for today!";
		
		if (LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).isBefore(DateUtility.toLocaDateTime(member.getEta()).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
			message = " here for tomorrow!";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b>"+member.getYouAre()).append(message+"</b>").append("\n");
		sb.append("<b>Name: </b>").append("<i>"+member.getName()+"</i>").append("\n");
		sb.append("<b>Telegram: </b>@").append(member.getUsername()).append("\n");
		if (!notAvailable.contains(member.getMobileNumber())) {
			sb.append("<b>Mobile: </b>").append("<i>"+member.getMobileNumber()+"</i>").append("\n");	
		}
		if (!notAvailable.contains(member.getPicUpLoc())) {
			sb.append("\n<b>Pick Up: </b>").append("<i>"+member.getPicUpLoc()+"</i>").append("\n");	
		}
		if (!notAvailable.contains(member.getDropOffLoc())) {
			sb.append("\n<b>Drop Off: </b>").append("<i>"+member.getDropOffLoc()+"</i>").append("\n\n");	
		}
		if (!notAvailable.contains(member.getRoute())) {
			sb.append("<b>Route: </b>").append("<i>"+member.getRoute()+"</i>").append("\n\n");	
		}
		if(CallBackContants.DRIVER.equals(member.getYouAre())) {
			sb.append("<b>Seat: </b>").append("<i>"+member.getAvailableSlots()+"</i>").append("\n");	
		}
		String etaTime = TimeUtility.convertToStandardTime(etaDate.split(" ")[1],etaDate.split(" ")[2]);
		String etdTime = TimeUtility.convertToStandardTime(etdDate.split(" ")[1],etdDate.split(" ")[2]);
		sb.append("<b>Time: </b>").append("<i>"+etaTime +" - "+ etdTime+"</i>").append("\n");
		
		if (!notAvailable.contains(member.getCustomMessage())) {
			sb.append("<b>Instruction: </b>").append("<i>"+member.getCustomMessage()+"</i>").append("\n");	
		}
		return sb.toString();
	}
}


