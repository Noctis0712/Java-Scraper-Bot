import io.github.cdimascio.dotenv.Dotenv;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;


public class JavaTestScraperBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("TOKEN");
    }

    @Override
    public String getBotUsername() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("BOT_USRNAME");
    }

    @Override
    public void onUpdateReceived(Update update)  {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();
                if(messageText.contains("/start")) {
                    String toSend = "Hello, welcome to the bot!";
                    SendMessage message = new SendMessage()
                            .setChatId(chatID)
                            .setText(toSend);

                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else if(messageText.contains("/help")) {
                    String toSend = "This bot uses the Marvel API " +
                            "to get you information related to Marvel Comics.";
                    SendMessage message = new SendMessage()
                            .setChatId(chatID)
                            .setText(toSend);

                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String charname = update.getMessage().getText();
                    StringBuffer s = new StringBuffer();
                    try {
                        s.append(Scraper.JSONParsing(charname));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String toSend = s.toString();
                    SendMessage message = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(toSend);

                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

        }
        else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText("Nice picture, but I can't understand images.");
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}