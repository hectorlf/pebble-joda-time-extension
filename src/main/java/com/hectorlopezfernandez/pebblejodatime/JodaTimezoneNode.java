package com.hectorlopezfernandez.pebblejodatime;

import java.io.IOException;
import java.io.Writer;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

public class JodaTimezoneNode extends AbstractRenderableNode {

	private static final Logger logger = LoggerFactory.getLogger(JodaTimezoneNode.class);

    private final Expression<?> value;
    private final BodyNode body;

    public JodaTimezoneNode(int lineNumber, Expression<?> value, BodyNode body) {
        super(lineNumber);
        this.value = value;
        this.body = body;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    	// evaluate and parse timezone
    	Object evaluatedTimezone = value.evaluate(self, context);
    	DateTimeZone timezone = null;
    	if (evaluatedTimezone instanceof String) {
    		try {
    			timezone = DateTimeZone.forID((String) evaluatedTimezone);
            } catch (IllegalArgumentException iae) {
            	throw new IllegalArgumentException("Couldn't parse a timezone for the input string: " + evaluatedTimezone.toString());
            }
    	} else if (evaluatedTimezone instanceof DateTimeZone) {
    		timezone = (DateTimeZone) evaluatedTimezone;
    	} else {
    		throw new IllegalArgumentException("JodaTimezone only supports String and DateTimeZone timezones. Actual argument was: " + (evaluatedTimezone == null ? "null" : evaluatedTimezone.getClass().getName()));
    	}
    	// create a scope with the new timezone and process the body
    	ScopeChain values = context.getScopeChain();
    	values.pushScope();
    	values.put(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE, timezone);
    	body.render(self, writer, context);
    	// check if scope is the same and clean it, else warn (there's nothing more we can do about it)
    	if (values.currentScopeContainsVariable(JodaExtension.TIMEZONE_REQUEST_ATTRIBUTE)) values.popScope();
    	else logger.warn("Could not clean scoped timezone {} because a child node opened a scope without closing it. The timezone will live for the rest of the current render.", timezone);

    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}