package com.hectorlopezfernandez.pebblejodatime;

import java.io.IOException;
import java.io.Writer;

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

public class JodaPatternNode extends AbstractRenderableNode {

	private static final Logger logger = LoggerFactory.getLogger(JodaPatternNode.class);

    private final Expression<?> value;
    private final BodyNode body;

    public JodaPatternNode(int lineNumber, Expression<?> value, BodyNode body) {
        super(lineNumber);
        this.value = value;
        this.body = body;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    	// evaluate and parse pattern
    	Object evaluatedPattern = value.evaluate(self, context);
    	if (!(evaluatedPattern instanceof String)) {
    		throw new IllegalArgumentException("JodaPattern only supports String patterns. Actual argument was: " + (evaluatedPattern == null ? "null" : evaluatedPattern.getClass().getName()));
    	}
    	// create a scope with the new pattern and process the body
    	ScopeChain values = context.getScopeChain();
    	values.pushScope();
    	values.put(JodaExtension.PATTERN_REQUEST_ATTRIBUTE, evaluatedPattern);
    	body.render(self, writer, context);
    	// check if scope is the same and clean it, else warn (there's nothing more we can do about it)
    	if (values.currentScopeContainsVariable(JodaExtension.PATTERN_REQUEST_ATTRIBUTE)) values.popScope();
    	else logger.warn("Could not clean scoped pattern {} because a child node opened a scope without closing it. The pattern will live for the rest of the current render.", evaluatedPattern);

    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}