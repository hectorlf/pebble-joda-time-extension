package com.hectorlopezfernandez.pebblejodatime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.coverity.security.Escape;
import com.mitchellbosecke.pebble.extension.Filter;

public class JodaFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public JodaFilter() {
        argumentNames.add("strategy");
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public Object apply(Object inputObject, Map<String, Object> args) {
        return inputObject;
    }

}