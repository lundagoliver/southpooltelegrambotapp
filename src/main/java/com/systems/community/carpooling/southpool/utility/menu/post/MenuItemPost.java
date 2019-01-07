package com.systems.community.carpooling.southpool.utility.menu.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemPost {

	private String name;
    private String action;
    
	public MenuItemPost(String name, String action) {
		super();
		this.name = name;
		this.action = action;
	}
    
}
