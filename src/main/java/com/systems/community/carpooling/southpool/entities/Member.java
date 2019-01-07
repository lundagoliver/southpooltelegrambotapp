package com.systems.community.carpooling.southpool.entities;

import java.util.Date;

public interface Member {

	public Long getId();
	public String getName();
	public String getFacebookProfileLink();
	public String getMobileNumber();
	public String getCarPlateNumber();
	public String getUsername();
	public String getYouAre();
	public String getPicUpLoc();
	public String getDropOffLoc();
	public String getRoute();
	public String getAvailableSlots();
	public String getEta();
	public String getEtd();
	public Date getUpdateDate();
	public Date getRegisterDate();
	public String getCustomMessage();
	
	public void setId(Long id);
	public void setName(String name);
	public void setFacebookProfileLink(String facebookProfileLink);
	public void setMobileNumber(String mobileNumber);
	public void setCarPlateNumber(String carPlateNumber);
	public void setUsername(String username);
	public void setYouAre(String youAre);
	public void setPicUpLoc(String picUpLoc);
	public void setDropOffLoc(String dropOffLoc);
	public void setRoute(String route);
	public void setAvailableSlots(String availableSlots);
	public void setEta(String eta);
	public void setEtd(String etd);
	public void setUpdateDate(Date updateDate);
	public void setRegisterDate(Date registerDate);
	public void setCustomMessage(String custom_message);
}
