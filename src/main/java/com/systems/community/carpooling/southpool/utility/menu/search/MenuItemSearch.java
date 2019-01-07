package com.systems.community.carpooling.southpool.utility.menu.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemSearch {

	private String name;
    private String action;
    
	public MenuItemSearch(String name, String action) {
		super();
		this.name = name;
		this.action = action;
	}
    
}
