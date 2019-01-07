package com.systems.community.carpooling.southpool.utility.menu.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemUpdate {

	private String name;
    private String action;
    
	public MenuItemUpdate(String name, String action) {
		super();
		this.name = name;
		this.action = action;
	}
    
}
