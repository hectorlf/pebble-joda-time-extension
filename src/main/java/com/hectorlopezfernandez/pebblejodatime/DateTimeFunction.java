package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeZone;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;

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

        // resolve timezone, it's used in both parsing and creating
        Object timezoneParam = args.get("timezone");
		DateTimeZone timezone = null;
		if (timezoneParam == null) {
		    // try default joda timezone
		 	DateTimeZone defaultTimezone = (DateTimeZone) getFromContext(context, JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE);
		 	if (defaultTimezone != null) timezone = defaultTimezone;
		} else if (timezoneParam instanceof String) {
		 	timezone = DateTimeZone.forID((String) timezoneParam);
		} else if (timezoneParam instanceof DateTimeZone) {
		 	timezone = (DateTimeZone) timezoneParam;
		} else {
		 	throw new IllegalArgumentException("JodaFilter only supports String and DateTimeZone timezones. Actual argument was: " + timezoneParam.getClass().getName());
		}

    	// if value is null, a DateTime is created and pattern/style ignored
    	Object value = args.get("value");
    	if (value == null) {
    		
        }
    	return null;
    }

	private Object getFromContext(EvaluationContext context, String key) {
		try {
	        return context.get(key);
		} catch(AttributeNotFoundException anfe) { return null; }
	}

}
