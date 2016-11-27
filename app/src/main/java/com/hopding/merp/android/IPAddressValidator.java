package com.hopding.merp.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to validate IP address strings:
 * <p>
 * <a href="https://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/">
 *     www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression
 * </a>
 */
public class IPAddressValidator {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public static boolean validate(final String ip){
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
