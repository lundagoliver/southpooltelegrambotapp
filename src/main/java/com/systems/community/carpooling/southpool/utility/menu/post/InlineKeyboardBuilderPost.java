package com.systems.community.carpooling.southpool.utility.menu.post;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Created by bvn13 on 21.02.2018.
 */
public class InlineKeyboardBuilderPost {

    private Long chatId;
    private String text;

    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    private InlineKeyboardBuilderPost() {}

    public static InlineKeyboardBuilderPost create() {
        InlineKeyboardBuilderPost builder = new InlineKeyboardBuilderPost();
        return builder;
    }

    public static InlineKeyboardBuilderPost create(Long chatId) {
        InlineKeyboardBuilderPost builder = new InlineKeyboardBuilderPost();
        builder.setChatId(chatId);
        return builder;
    }

    public InlineKeyboardBuilderPost setText(String text) {
        this.text = text;
        return this;
    }

    public InlineKeyboardBuilderPost setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public InlineKeyboardBuilderPost row() {
        this.row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardBuilderPost button(String text, String callbackData) {
        row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
        return this;
    }

    public InlineKeyboardBuilderPost endRow() {
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
