package com.systems.community.carpooling.southpool.utility.menu;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItem {

	private String name;
    private String action;
    
	public MenuItem(String name, String action) {
		super();
		this.name = name;
		this.action = action;
	}
    
}
