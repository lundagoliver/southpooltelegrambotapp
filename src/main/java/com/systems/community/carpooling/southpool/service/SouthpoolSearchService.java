package com.systems.community.carpooling.southpool.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.systems.community.carpooling.southpool.entities.Member;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberHomeToWork;
import com.systems.community.carpooling.southpool.entities.SouthPoolMemberWorkToHome;
import com.systems.community.carpooling.southpool.net.rest.RESTHttpClient;
import com.systems.community.carpooling.southpool.persistence.service.PersistenceService;
import com.systems.community.carpooling.southpool.props.SouthPoolSettings;
import com.systems.community.carpooling.southpool.utility.CallBackContants;
import com.systems.community.carpooling.southpool.utility.DateUtility;
import com.systems.community.carpooling.southpool.utility.TimeUtility;

@Service
public class SouthpoolSearchService {

	private static final Log log = LogFactory.getLog(SouthpoolSearchService.class);

	private PersistenceService persistenceService;
	private RESTHttpClient restHttpClient;
	private SouthPoolSettings southPoolSettings;
	
	
	public SouthpoolSearchService(PersistenceService persistenceService, RESTHttpClient restHttpClient,
			SouthPoolSettings southPoolSettings) {
		super();
		this.persistenceService = persistenceService;
		this.restHttpClient = restHttpClient;
		this.southPoolSettings = southPoolSettings;
	}

	@Async
	public void sendResponseDetailsInTelegram(String fileName, List<Member> members, long chatId, String search) throws IOException, DocumentException {
		// Send document
		String file = convertToPDFFormat(fileName, getMemberDetails(members, search));
		String pattern = "{0}{1}/sendDocument?chat_id={2}&caption={3}";
		String telegramUrl = MessageFormat.format(pattern, southPoolSettings.getTelegramEndPoint(), southPoolSettings.getTelegramBotToken(), String.valueOf(chatId), fileName, getFile(file));
		log.info(telegramUrl);
		try {
			restHttpClient.getDefaultRestTemplate().postForEntity(telegramUrl, getRequestEntity(fileName), String.class).getBody();
		} catch (RestClientException e) {
			log.error("Exception {}", e);
		}
		File f = getFile(fileName);
		if(f.delete()) { 
			log.info(fileName + " File was successfully deleted!");
		}
	}
	
	private HttpEntity<MultiValueMap<String, Object>> getRequestEntity(String fileName){
		return new HttpEntity<>(getMultiValueMap(fileName), getHeader());
	}
	
	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return headers;
	}
	
	private MultiValueMap<String, Object> getMultiValueMap(String fileName){
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		FileSystemResource value = new FileSystemResource(getFile(fileName));
		body.add("document", value);
		return body;
	}
	
	private File getFile(String fileName) {
		File file = new File(fileName);
		String filePath = file.getAbsolutePath();
		return new File(filePath+".pdf");
	}

	private String getMemberDetails(List<Member> members, String search) {
		
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
		notAvailable.add("-n/a-");
		
		String toSearch =  CallBackContants.TODAY_DRIVER.equals(search) ? "drivers" : "passengers"; 
		StringBuilder telegramNotif = new StringBuilder();
		telegramNotif.append("Search results as of " + DateUtility.convertDateToGMT(8)).append("\n");
		telegramNotif.append("Total of " + toSearch + " found base on your ETA and ETD: " + members.size()).append("\n\n");
		for (Member member : members) {
			String etaDate = DateUtility.toLocaDateTime(member.getEta()).format(DateUtility.FORMAT_DATETIME_INFO);
			String etdDate = DateUtility.toLocaDateTime(member.getEtd()).format(DateUtility.FORMAT_DATETIME_INFO);
			
			if (!notAvailable.contains(member.getName())) {
				telegramNotif.append("Name: ").append(member.getName()).append("\n");	
			}
			
			telegramNotif.append("Telegram: ").append("@"+member.getUsername()).append("\n");
			
			if (!notAvailable.contains(member.getFacebookProfileLink())) {
				telegramNotif.append("Profile: ").append(member.getFacebookProfileLink()).append("\n");	
			}
			
			if (!notAvailable.contains(member.getMobileNumber())) {
				telegramNotif.append("Mobile: ").append(member.getMobileNumber()).append("\n");	
			}
			
			if(CallBackContants.DRIVER.equals(member.getYouAre())) {
				if (!notAvailable.contains(member.getCarPlateNumber())) {
					telegramNotif.append("Car Plate: ").append(member.getCarPlateNumber()).append("\n");	
				}	
			}
			
			telegramNotif.append("\nPick Up: ").append(member.getPicUpLoc()).append("\n");
			telegramNotif.append("\nDrop Off: ").append(member.getDropOffLoc()).append("\n");
			telegramNotif.append("\nRoute: ").append(member.getRoute()).append("\n\n");
			
			if (!notAvailable.contains(member.getAvailableSlots())) {
				telegramNotif.append("Seat: ").append(member.getAvailableSlots()).append("\n");	
			}
			
			String etaTime = TimeUtility.convertToStandardTime(etaDate.split(" ")[1],etaDate.split(" ")[2]);
			String etdTime = TimeUtility.convertToStandardTime(etdDate.split(" ")[1],etdDate.split(" ")[2]);
			telegramNotif.append("Time: ").append(etaTime +" - "+ etdTime).append("\n");
			telegramNotif.append("Instruction: ").append(member.getCustomMessage()).append("\n");
			telegramNotif.append("------------------------------------------------------------------------------------------------------------");
			telegramNotif.append("\n\n");
		}
		return telegramNotif.toString();
	}

	private String convertToPDFFormat(String fileName, String memberDetails) throws IOException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(fileName+".pdf"));

		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);
		Paragraph paragraph = new Paragraph(memberDetails, font);
		try {
			document.add(paragraph);
		} catch (DocumentException e) {
			log.error("{}",e);
		}
		document.close();
		return fileName;
	}

	public List<Member> getSouthPoolMemberHomeToWorkMembers(Map<String,String> predicate) {
		Member member = persistenceService.getMember(predicate.get("username"), SouthPoolMemberHomeToWork.class);
		return persistenceService.getMembersBetweenDate("eta", DateUtility.covertLocaDateTimeToDate(member.getEta()), DateUtility.covertLocaDateTimeToDate(member.getEtd()), predicate, SouthPoolMemberHomeToWork.class);
	}

	public List<Member> getSouthPoolMemberWorkToHomeMembers(Map<String,String> predicate) {
		Member member = persistenceService.getMember(predicate.get("username"), SouthPoolMemberWorkToHome.class);
		return persistenceService.getMembersBetweenDate("eta", DateUtility.covertLocaDateTimeToDate(member.getEta()), DateUtility.covertLocaDateTimeToDate(member.getEtd()), predicate, SouthPoolMemberWorkToHome.class);
	}
}
