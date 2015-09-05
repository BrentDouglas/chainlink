/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.spi;

import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Messages {

    private Messages(){}

    private static final ResourceBundle MESSAGES;

    static {
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("ChainlinkMessages", Locale.getDefault(), Messages.class.getClassLoader());
        } catch (final Exception e) {
            bundle = ResourceBundle.getBundle("ChainlinkMessages", Locale.ENGLISH, Messages.class.getClassLoader());
        }
        MESSAGES = bundle;
    }

    public static String get(final String key) {
        return key.split("\\.")[0] + ": " + MESSAGES.getString(key);
    }

    public static String format(final String key, final Object... args) {
        return new Formatter().format(get(key), args).toString();
    }

    public static String raw(final String key) {
        return MESSAGES.getString(key);
    }
}
