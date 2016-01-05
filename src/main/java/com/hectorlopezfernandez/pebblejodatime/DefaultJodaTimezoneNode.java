package com.hectorlopezfernandez.pebblejodatime;

import java.io.Writer;

import org.joda.time.DateTimeZone;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

public class DefaultJodaTimezoneNode extends AbstractRenderableNode {

    private final Expression<?> value;

    public DefaultJodaTimezoneNode(int lineNumber, Expression<?> value) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    	// evaluate and parse timezone
    	Object evaluatedTimezone = value.evaluate(self, context);
    	DateTimeZone timezone = null;
    	if (evaluatedTimezone instanceof String) {
    		try {
    			timezone = DateTimeZone.forID((String) evaluatedTimezone);
            } catch (IllegalArgumentException iae) {
            	throw new IllegalArgumentException("DefaultJodaTimezone could not parse a timezone for the input string: " + evaluatedTimezone.toString());
            }
    	} else if (evaluatedTimezone instanceof DateTimeZone) {
    		timezone = (DateTimeZone) evaluatedTimezone;
    	} else {
    		throw new IllegalArgumentException("DefaultJodaTimezone only supports String and DateTimeZone timezones. Actual argument was: " + (evaluatedTimezone == null ? "null" : evaluatedTimezone.getClass().getName()));
    	}
    	ScopeChain values = context.getScopeChain();
    	// if this context has another timezone, push a new scope
    	if (values.get(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE) != null) values.pushScope();
    	// put the new timezone in the context
    	values.put(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE, timezone);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}