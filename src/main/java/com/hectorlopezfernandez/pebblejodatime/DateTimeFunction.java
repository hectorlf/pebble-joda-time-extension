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

        //TODO datetime creation should take into account default timezones set with "defaultJodaTimezone"
        // resolve timezone, it's used in both parsing and creating datetimes
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

		//TODO datetime creation should take into account known defaults for locale, pattern
    	// if value is null, a DateTime is created and pattern/style ignored
    	Object value = args.get("value");
    	DateTime d = null;
    	if (value == null) {
    		d = new DateTime();
    		if (timezone != null) d = d.withZone(timezone);
        } else if (value instanceof Date) {
    		d = new DateTime((Date) value);
    		if (timezone != null) d = d.withZone(timezone);
        } else if (value instanceof String) {
        	Object pattern = args.get("pattern");
        	Object style = args.get("style");
        	Object locale = args.get("locale");
        	// parse string into a DateTime
            DateTimeFormatter formatter;
            if (pattern != null) {
                formatter = DateTimeFormat.forPattern(pattern.toString());
            } else if (style != null) {
                formatter = DateTimeFormat.forStyle(style.toString());
            } else {
                formatter = DateTimeFormat.fullDateTime();
            }
            if (locale instanceof Locale) {
                formatter = formatter.withLocale((Locale) locale);
            } else if (locale instanceof String) {
            	formatter = formatter.withLocale(Locale.forLanguageTag(locale.toString()));
            }
            if (timezone != null) {
                formatter = formatter.withZone(timezone);
            }
            d = formatter.parseDateTime((String) value);
        }
    	return d;
    }

}