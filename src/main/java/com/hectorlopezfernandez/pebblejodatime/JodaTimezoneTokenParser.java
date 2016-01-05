package com.hectorlopezfernandez.pebblejodatime;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;

public class JodaTimezoneTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {
    	TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip the 'defaultJodaTimezone' token
        stream.next();

        Expression<?> value = parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        return new DefaultJodaTimezoneNode(lineNumber, value);
    }

    @Override
    public String getTag() {
        return "defaultJodaTimezone";
    }

}