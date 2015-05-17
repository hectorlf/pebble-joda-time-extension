package com.hectorlopezfernandez.pebblejodatime;

import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class DefaultJodaLocaleNode extends AbstractRenderableNode {

    private final Expression<?> value;

    public DefaultJodaLocaleNode(int lineNumber, Expression<?> value) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
        context.put(JodaExtension.LOCALE_REQUEST_ATTRIBUTE, value.evaluate(self, context));
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}