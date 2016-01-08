package com.hectorlopezfernandez.pebblejodatime;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

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

public class JodaLocaleNode extends AbstractRenderableNode {

	private static final Logger logger = LoggerFactory.getLogger(JodaLocaleNode.class);

    private final Expression<?> value;
    private final BodyNode body;

    public JodaLocaleNode(int lineNumber, Expression<?> value, BodyNode body) {
        super(lineNumber);
        this.value = value;
        this.body = body;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    	// evaluate and parse locale
    	Object evaluatedLocale = value.evaluate(self, context);
    	Locale locale = null;
    	if (evaluatedLocale instanceof String) {
    		locale = new Locale.Builder().setLanguageTag((String) evaluatedLocale).build();
    	} else if (evaluatedLocale instanceof Locale) {
    		locale = (Locale) evaluatedLocale;
    	} else {
    		throw new IllegalArgumentException("JodaLocale only supports String and Locale locales. Actual argument was: " + (evaluatedLocale == null ? "null" : evaluatedLocale.getClass().getName()));
    	}
    	// create a scope with the new locale and process the body
    	ScopeChain values = context.getScopeChain();
    	values.pushScope();
    	values.put(JodaExtension.LOCALE_REQUEST_ATTRIBUTE, locale);
    	body.render(self, writer, context);
    	// check if scope is the same and clean it, else warn (there's nothing more we can do about it)
    	if (values.currentScopeContainsVariable(JodaExtension.LOCALE_REQUEST_ATTRIBUTE)) values.popScope();
    	else logger.warn("Could not clean scoped locale {} because a child node opened a scope without closing it. The locale will live for the rest of the current render.", locale);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}