package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        argumentNames.add("locale");
        argumentNames.add("timezone");
        argumentNames.add("style");
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
		DateTimeZone timezone = ResolveUtils.resolveTimezone(args.get("timezone"), values);

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
        		String pattern = ResolveUtils.resolvePattern(patternParam, values);
        		if (pattern != null) {
                    formatter = DateTimeFormat.forPattern(pattern);
                } else {
                    formatter = DateTimeFormat.fullDateTime();
                }
        	}
            formatter = formatter.withLocale(ResolveUtils.resolveLocale(args.get("locale"), values));
            formatter = formatter.withZone(timezone);
            d = formatter.parseDateTime((String) value);
        }
    	return d;
    }

}