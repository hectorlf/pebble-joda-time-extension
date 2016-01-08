package com.hectorlopezfernandez.pebblejodatime;

import java.util.Locale;

import org.joda.time.DateTimeZone;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.ScopeChain;

final class ResolveUtils {

	private ResolveUtils() {
		// not instantiable
	}

    public static DateTimeZone resolveTimezone(Object timezoneParam, ScopeChain values) {
		DateTimeZone timezone = null;
		if (timezoneParam == null) {
		    // try default joda timezone
		 	DateTimeZone defaultTimezone = (DateTimeZone) values.get(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE);
		 	if (defaultTimezone != null) timezone = defaultTimezone;
		 	else timezone = DateTimeZone.getDefault();
		} else if (timezoneParam instanceof String) {
		 	timezone = DateTimeZone.forID((String) timezoneParam);
		} else if (timezoneParam instanceof DateTimeZone) {
		 	timezone = (DateTimeZone) timezoneParam;
		} else {
		 	throw new IllegalArgumentException("JodaExtension only supports String and DateTimeZone timezones. Actual argument was: " + timezoneParam.getClass().getName());
		}
		return timezone;
    }

    public static String resolvePattern(Object patternParam, ScopeChain values) {
    	String pattern = null;
		if (patternParam == null) {
		    // try default joda pattern
		 	String defaultPattern = (String) values.get(JodaExtension.PATTERN_REQUEST_ATTRIBUTE);
		 	if (defaultPattern != null) pattern = defaultPattern;
		} else  {
			pattern = patternParam.toString();
		}
		return pattern;
    }

    public static Locale resolveLocale(Object localeParam, EvaluationContext context) {
    	Locale locale = null;
    	if (localeParam == null) {
		    // try default joda locale, and resort to pebble locale if everything fails
		 	Locale defaultLocale = (Locale) context.getScopeChain().get(JodaExtension.LOCALE_REQUEST_ATTRIBUTE);
		 	if (defaultLocale != null) locale = defaultLocale;
		 	else locale = context.getLocale();
    	} else if (localeParam instanceof Locale) {
    		locale = (Locale)localeParam;
        } else if (localeParam instanceof String) {
        	locale = new Locale.Builder().setLanguageTag(localeParam.toString()).build();
        } else {
        	throw new IllegalArgumentException("JodaExtension only supports String and Locale locales. Actual argument was: " + localeParam.getClass().getName());
        }
        return locale;
    }

}