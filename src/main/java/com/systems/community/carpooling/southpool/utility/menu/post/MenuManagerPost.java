package com.systems.community.carpooling.southpool.utility.menu.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MenuManagerPost {

	public static final String PREV_ACTION = "page-prev";
	public static final String NEXT_ACTION = "page-next";
	public static final String CANCEL_ACTION = "cancel";

	private int buttonsPerPage = 10;
	public void setButtonsPerPage(int buttonsPerPage) {
		this.buttonsPerPage = buttonsPerPage;
	}

	private int total;
	private int lastPage;

	private MenuItemPost btnPrev = new MenuItemPost("<<", PREV_ACTION);
	private MenuItemPost btnNext = new MenuItemPost(">>", NEXT_ACTION);
	private MenuItemPost btnCancel = new MenuItemPost("Help", CANCEL_ACTION);

	private List<MenuItemPost> menu = new ArrayList<>();

	private int columnsCount;
	public void setColumnsCount(int columnsCount) {
		this.columnsCount = columnsCount;
	}

	public void init() {
		this.total = menu.size();
		this.lastPage = (int) Math.ceil((double) total / buttonsPerPage) - 1;
	}

	public void addMenuItem(String name, String action) {
		this.menu.add(new MenuItemPost(name, action));
	}

	private List<MenuItemPost> getPage(int page) {
		List<MenuItemPost> pageMenu = new ArrayList<>();

		if (page > lastPage) {
			return pageMenu;
		}

		int start = page* buttonsPerPage;
		int end = (page+1)* buttonsPerPage -1;

		if (start < 0) start = 0;
		if (end >= total) end = total-1;

		for (int i = start; i <= end; i++) {
			pageMenu.add(menu.get(i));
		}

		return pageMenu;
	}

	private List<MenuItemPost> getControlButtonsForPage(int page, boolean hasCancel) {
		List<MenuItemPost> buttons = new ArrayList<>();
		if (page > 0) {
			buttons.add(btnPrev);
		}
		if (hasCancel) {
			buttons.add(btnCancel);
		}
		if (page < lastPage) {
			buttons.add(btnNext);
		}
		return buttons;
	}

	public InlineKeyboardBuilderPost createMenuForPage(int page, boolean hasCancel) {
		List<MenuItemPost> pageButtons = getPage(page);
		List<MenuItemPost> controlButtons = getControlButtonsForPage(page, hasCancel);

		InlineKeyboardBuilderPost builder = InlineKeyboardBuilderPost.create();
		int col = 0;
		int num = 0;
		builder.row();
		for (MenuItemPost button : pageButtons) {
			builder.button(button.getName(), button.getAction());
			if (++col >= columnsCount) {
				col = 0;
				builder.endRow();
				if (num++ <= pageButtons.size()) {
					builder.row();
				}
			}
		}
		builder.endRow();

		builder.row();
		for (MenuItemPost button : controlButtons) {
			if (button.getAction().equals(PREV_ACTION)) {
				builder.button(button.getName(), button.getAction()+":"+(page-1));
			} else if (button.getAction().equals(NEXT_ACTION)) {
				builder.button(button.getName(), button.getAction()+":"+(page+1));
			} else {
				builder.button(button.getName(), button.getAction());
			}
		}
		builder.endRow();

		return builder;
	}
}
