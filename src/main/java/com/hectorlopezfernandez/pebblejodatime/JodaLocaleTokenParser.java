package com.hectorlopezfernandez.pebblejodatime;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;

public class JodaLocaleTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token) throws ParserException {
    	TokenStream stream = this.parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip the 'defaultJodaLocale' token
        stream.next();

        Expression<?> value = this.parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        return new DefaultJodaLocaleNode(lineNumber, value);
    }

    @Override
    public String getTag() {
        return "defaultJodaLocale";
    }

}