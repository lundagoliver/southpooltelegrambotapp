package com.systems.community.carpooling.southpool.utility.menu.update;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Created by bvn13 on 21.02.2018.
 */
public class InlineKeyboardBuilderUpdate {

    private Long chatId;
    private String text;
    private String parse;

    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    private InlineKeyboardBuilderUpdate() {}

    public static InlineKeyboardBuilderUpdate create() {
        InlineKeyboardBuilderUpdate builder = new InlineKeyboardBuilderUpdate();
        return builder;
    }

    public static InlineKeyboardBuilderUpdate create(Long chatId) {
        InlineKeyboardBuilderUpdate builder = new InlineKeyboardBuilderUpdate();
        builder.setChatId(chatId);
        return builder;
    }

    public InlineKeyboardBuilderUpdate setText(String text) {
        this.text = text;
        return this;
    }
    
    public InlineKeyboardBuilderUpdate setParse(String parse) {
        this.parse = parse;
        return this;
    }

    public InlineKeyboardBuilderUpdate setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public InlineKeyboardBuilderUpdate row() {
        this.row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardBuilderUpdate button(String text, String callbackData) {
        row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
        return this;
    }

    public InlineKeyboardBuilderUpdate endRow() {
        this.keyboard.add(this.row);
        this.row = null;
        return this;
    }


    public SendMessage build() {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode(parse);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

}
