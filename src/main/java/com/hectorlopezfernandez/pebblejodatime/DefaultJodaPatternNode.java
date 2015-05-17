package com.hectorlopezfernandez.pebblejodatime;

import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class DefaultJodaPatternNode extends AbstractRenderableNode {

    private final Expression<?> value;

    public DefaultJodaPatternNode(int lineNumber, Expression<?> value) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    	// evaluate and parse pattern
    	Object evaluatedPattern = value.evaluate(self, context);
    	if (!(evaluatedPattern instanceof String)) {
    		throw new IllegalArgumentException("DefaultJodaPattern only supports String patterns. Actual argument was: " + (evaluatedPattern == null ? "null" : evaluatedPattern.getClass().getName()));
    	}
    	// if this context has another pattern, push a new scope
    	if (context.get(JodaExtension.PATTERN_REQUEST_ATTRIBUTE) != null) context.pushScope();
    	// put the new pattern in the context
        context.put(JodaExtension.PATTERN_REQUEST_ATTRIBUTE, evaluatedPattern);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}