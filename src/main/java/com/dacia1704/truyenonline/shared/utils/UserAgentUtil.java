package com.dacia1704.truyenonline.shared.utils;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public final class UserAgentUtil {

    private static final UserAgentAnalyzer ANALYZER =
            UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();

    private UserAgentUtil() {}

    public static String getDeviceName(String userAgentString) {

        UserAgent ua = ANALYZER.parse(userAgentString);

        String device = ua.getValue(UserAgent.DEVICE_NAME);
        String os = ua.getValue(UserAgent.OPERATING_SYSTEM_NAME);
        String browser = ua.getValue(UserAgent.AGENT_NAME);

        return browser + " - " + os + " (" + device + ")";
    }
}
