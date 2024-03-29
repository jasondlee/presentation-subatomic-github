package com.steeplesoft.quarkusbot.config;

import java.util.List;

public class BotConfigFile {
    public NestedConfig twilio = new NestedConfig();
    public List<String> canComment;

    public static class NestedConfig {
        public String one;
        public String two;
        public String three;
    }

}
