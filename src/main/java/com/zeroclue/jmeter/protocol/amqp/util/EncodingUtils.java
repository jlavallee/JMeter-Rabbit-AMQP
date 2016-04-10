/**
 *
 */
package com.zeroclue.jmeter.protocol.amqp.util;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * @author jorge.matos
 *
 */
public class EncodingUtils {

    public static Locale getDefaultLocale() {
        return Locale.US;
    }

    public static Charset getDefaultCharset() {
        return Charset.forName("UTF-8");
    }

}
