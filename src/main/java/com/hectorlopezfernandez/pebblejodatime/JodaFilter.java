package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.ScopeChain;

public class JodaFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>(4);

    public JodaFilter() {
        argumentNames.add("pattern");
        argumentNames.add("style");
        argumentNames.add("locale");
        argumentNames.add("timezone");
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public Object apply(Object inputObject, Map<String, Object> args) {
        if (inputObject == null || !(inputObject instanceof ReadableInstant || inputObject instanceof ReadablePartial)) {
            return inputObject;
        }
        
        EvaluationContext context = (EvaluationContext) args.get("_context");
        ScopeChain values = context.getScopeChain();

        // pattern param
        String pattern = (String) args.get("pattern");
        // style param
        String style = (String) args.get("style");
        // pattern is preferred to style, but only if they are both specified.
        // style, if specified, is preferred to default joda pattern.
        if (pattern == null && style == null) {
        	String defaultPattern = (String) values.get(JodaExtension.PATTERN_REQUEST_ATTRIBUTE);
			if (defaultPattern != null) pattern = defaultPattern;
        }

        // create formatter
        DateTimeFormatter formatter;
        if (pattern != null) {
            formatter = DateTimeFormat.forPattern(pattern);
        } else if (style != null) {
            formatter = DateTimeFormat.forStyle(style);
        } else {
            // use a medium date (no time) style by default; same as jstl
            formatter = DateTimeFormat.mediumDate();
        }

        // locale param
        Object localeParam = args.get("locale");
        Locale locale = null;
        if (localeParam == null) {
        	// try first the default joda locale and then resort to EvaluationContext's locale
			Locale defaultLocale = (Locale) values.get(JodaExtension.LOCALE_REQUEST_ATTRIBUTE);
			if (defaultLocale != null) locale = defaultLocale;
			else locale = context.getLocale();
        } else if (localeParam instanceof String) {
            locale = Locale.forLanguageTag((String) localeParam);
        } else if (localeParam instanceof Locale) {
        	locale = (Locale) localeParam;
        } else {
        	throw new IllegalArgumentException("JodaFilter only supports String and Locale locales. Actual argument was: " + localeParam.getClass().getName());
        }
        formatter = formatter.withLocale(locale);
        
        // shortcircuit if no timezone can be involved
        if (inputObject instanceof ReadablePartial) {
        	return formatter.print((ReadablePartial) inputObject);
        }

        // timezone param
        Object timezoneParam = args.get("timezone");
        DateTimeZone timezone = null;
        if (timezoneParam == null) {
        	// try default joda timezone
    		DateTimeZone defaultTimezone = (DateTimeZone) values.get(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE);
    		if (defaultTimezone != null) timezone = defaultTimezone;
        } else if (timezoneParam instanceof String) {
        	timezone = DateTimeZone.forID((String) timezoneParam);
        } else if (timezoneParam instanceof DateTimeZone) {
        	timezone = (DateTimeZone) timezoneParam;
        } else {
        	throw new IllegalArgumentException("JodaFilter only supports String and DateTimeZone timezones. Actual argument was: " + timezoneParam.getClass().getName());
        }
        if (timezone != null) {
        	formatter.withZone(timezone);
        }

        return formatter.print((ReadableInstant) inputObject);
    }

}