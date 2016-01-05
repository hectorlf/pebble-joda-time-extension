package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public class JodaExtension extends AbstractExtension {
	
	public static final String LOCALE_REQUEST_ATTRIBUTE = "joda_locale";
	public static final String TIMEZONE_REQUEST_ATTRIBUTE = "joda_timezone";
	public static final String PATTERN_REQUEST_ATTRIBUTE = "joda_pattern";

    private final JodaFilter filter;
    private final DateTimeFunction dateTimeFunction;

    public JodaExtension() {
        this.filter = new JodaFilter();
        this.dateTimeFunction = new DateTimeFunction();
    }

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>(2);
        filters.put("joda", filter);
        return filters;
    }
    
    @Override
    public Map<String, Function> getFunctions() {
    	Map<String, Function> functions = new HashMap<>(2);
    	functions.put("dateTime", dateTimeFunction);
        return functions;
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>(6);
        parsers.add(new DefaultJodaLocaleTokenParser());
        parsers.add(new DefaultJodaPatternTokenParser());
        parsers.add(new DefaultJodaTimezoneTokenParser());
        parsers.add(new JodaLocaleTokenParser());
        parsers.add(new JodaPatternTokenParser());
        parsers.add(new JodaTimezoneTokenParser());
        return parsers;
    }

}