package com.steeplesoft.okcjug.config;

import java.util.List;

public class OkcJugBotConfigFile {
    public TwilioConfig twilio = new TwilioConfig();
    public List<String> canComment;

    public static class TwilioConfig {
        public String sid;
        public String token;
        public String phoneNumber;
    }

}
