package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.ScopeChain;

public class DateTimeFunction implements Function {

    private final List<String> argumentNames = new ArrayList<>();

    public DateTimeFunction() {
        argumentNames.add("value");
        argumentNames.add("pattern");
        argumentNames.add("style");
        argumentNames.add("locale");
        argumentNames.add("timezone");
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        EvaluationContext context = (EvaluationContext) args.get("_context");
        ScopeChain values = context.getScopeChain();

        // resolve timezone, it's used in both parsing and creating datetimes
		DateTimeZone timezone = resolveTimezone(args.get("timezone"), values);

    	// process datetime
    	Object value = args.get("value");
    	DateTime d = null;
    	if (value == null) {
    		d = new DateTime(timezone);
        } else if (value instanceof Date) {
    		d = new DateTime((Date) value, timezone);
        } else if (value instanceof String) {
        	DateTimeFormatter formatter;
        	Object patternParam = args.get("pattern");
        	Object styleParam = args.get("style");
        	// if pattern is specified, it is preferred to style; but if not specified, style is preferred to default joda pattern
        	if (patternParam == null && styleParam != null) {
        		formatter = DateTimeFormat.forStyle(styleParam.toString());
        	} else {
        		String pattern = resolvePattern(patternParam, values);
        		if (pattern != null) {
                    formatter = DateTimeFormat.forPattern(pattern);
                } else {
                    formatter = DateTimeFormat.fullDateTime();
                }
        	}
            formatter = formatter.withLocale(resolveLocale(args.get("locale"), values));
            formatter = formatter.withZone(timezone);
            d = formatter.parseDateTime((String) value);
        }
    	return d;
    }

    private DateTimeZone resolveTimezone(Object timezoneParam, ScopeChain values) {
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
		 	throw new IllegalArgumentException("DateTimeFunction only supports String and DateTimeZone timezones. Actual argument was: " + timezoneParam.getClass().getName());
		}
		return timezone;
    }

    private String resolvePattern(Object patternParam, ScopeChain values) {
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

    private Locale resolveLocale(Object localeParam, ScopeChain values) {
    	Locale locale = null;
    	if (localeParam == null) {
		    // try default joda locale
		 	Locale defaultLocale = (Locale) values.get(JodaExtension.LOCALE_REQUEST_ATTRIBUTE);
		 	if (defaultLocale != null) locale = defaultLocale;
    	} else if (localeParam instanceof Locale) {
    		locale = (Locale)localeParam;
        } else if (localeParam instanceof String) {
        	locale = Locale.forLanguageTag(localeParam.toString());
        } else {
        	throw new IllegalArgumentException("DateTimeFunction only supports String and Locale locales. Actual argument was: " + localeParam.getClass().getName());
        }
        return locale;
    }

}