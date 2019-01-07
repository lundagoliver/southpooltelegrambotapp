package com.systems.community.carpooling.southpool.utility.menu.search;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Created by bvn13 on 21.02.2018.
 */
public class InlineKeyboardBuilderSearch {

    private Long chatId;
    private String text;

    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    private InlineKeyboardBuilderSearch() {}

    public static InlineKeyboardBuilderSearch create() {
        InlineKeyboardBuilderSearch builder = new InlineKeyboardBuilderSearch();
        return builder;
    }

    public static InlineKeyboardBuilderSearch create(Long chatId) {
        InlineKeyboardBuilderSearch builder = new InlineKeyboardBuilderSearch();
        builder.setChatId(chatId);
        return builder;
    }

    public InlineKeyboardBuilderSearch setText(String text) {
        this.text = text;
        return this;
    }

    public InlineKeyboardBuilderSearch setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public InlineKeyboardBuilderSearch row() {
        this.row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardBuilderSearch button(String text, String callbackData) {
        row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
        return this;
    }

    public InlineKeyboardBuilderSearch endRow() {
        this.keyboard.add(this.row);
        this.row = null;
        return this;
    }


    public SendMessage build() {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

}
