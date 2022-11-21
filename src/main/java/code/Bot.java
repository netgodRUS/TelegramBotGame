package code;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;


import java.util.ArrayList;
import java.util.List;

public class Bot {
    final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));
    private static final String PROCESSING_LABEL = "Processing ....";
    private final static List<String> opponentWins = new ArrayList<>(){{
        add("01");
        add ("12");
        add ("20");

    }};

    public void serve() {
        // Create your bot passing the token received from @BotFather


// Register for updates
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);


            // ... process updates
            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

// Send messages


    private void process(Update update) {
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        InlineQuery inlineQuery = update.inlineQuery();

        BaseRequest request = null;
        if (message != null && message.viaBot() != null && message.viaBot().username().equals("TelegramBotGame")) {
            InlineKeyboardMarkup replyMarkup = message.replyMarkup();
            if (replyMarkup == null) {
                return;
            }
            InlineKeyboardButton[][] buttons = replyMarkup.inlineKeyboard();
            if (buttons == null) {
                return;
            }
            InlineKeyboardButton button = buttons[0][0];
            String buttonLabel = buttons[0][0].text();
            if (buttonLabel.equals(PROCESSING_LABEL)) {
                return;
            }
            long chatId = message.chat().id();
            String senderName = message.from().firstName();
            String senderChoose = button.callbackData();
            Integer messageId = message.messageId();

            request = new EditMessageText(chatId, messageId, message.text())
                    .replyMarkup(
                            new InlineKeyboardMarkup(new InlineKeyboardButton("\uDC46 ")
                                    .callbackData(String.format("$d $s $s $s", chatId, senderName, senderChoose, "0")),

                                    new InlineKeyboardButton("✌")
                                            .callbackData(String.format("$d $s $s $s", chatId, senderName, senderChoose, "1")),

                                            new InlineKeyboardButton("\uDD0F ")
                                                    .callbackData(String.format("$d $s $s $s", chatId, senderName, senderChoose, "2"))
                                            ));
        }


        if (message != null) {
            InlineQueryResultArticle pipka = buildInlineButton("pipka", "\uDC46 Pipka", "0");
            InlineQueryResultArticle scissors = buildInlineButton("scissors", "✌ Scissors", "1");
            InlineQueryResultArticle ruler = buildInlineButton("ruler", "\uDD0F Ruler", "2");
            request = new AnswerInlineQuery(inlineQuery.id(), pipka, scissors, ruler).cacheTime(1);

        }else if (callbackQuery !=null){
            String [] data = callbackQuery.data().split(" ");
            long chatId= Long.parseLong(data [0])  ;
            String senderName = data[1];
            String senderChoose = data [2];
            String opponentChoose = data[3];
            String opponentName = callbackQuery.from().firstName() + "(!)";
            if (senderChoose.equals(opponentChoose)) {
                request = new SendMessage(chatId, "Nobody wins =(");
            } else if (opponentWins.contains(senderChoose+opponentChoose)){
                request = new SendMessage(chatId, String.format(
                        "$s ($s) was beaten by $s ($s)",
                        senderName, senderChoose, opponentName, opponentChoose
                )
                );
            } else {
                request = new SendMessage(chatId, String.format(
                        "$s ($s) was beaten by $s ($s)",
                        opponentName, opponentChoose,
                        senderName, senderChoose
                )
                );
            }

        }

//            BaseRequest request = null;
//        } else if (message != null) {
//            long chatId = update.message().chat().id();
//            request = new SendMessage(chatId, "Здравствуйте!  Идите отсюда");
//        }

        if (request != null) {
            bot.execute(request);
        }
    }

    private static InlineQueryResultArticle buildInlineButton(String id, String title, String callbackData) {
        return new InlineQueryResultArticle(id, title, "I'm ready to fight")
                .replyMarkup(
                        new InlineKeyboardMarkup(
                                new InlineKeyboardButton(PROCESSING_LABEL).callbackData(callbackData)
                        )
                );
    }
}

