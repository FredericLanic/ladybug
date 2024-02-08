package com.kycox.game.message;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LadybugMessagesBundle {

    private final MessageSource messageSource;

    public LadybugMessagesBundle(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        return messageSource.getMessage(
                key,                    // clé du message
                null,                   // tableau des valeurs à substituer dans le message
                key,                    // message par défaut si la clé est absente
                Locale.getDefault());   // langue à utiliser
    }
}
