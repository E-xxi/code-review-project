// Source: https://github.com/antlr/grammars-v4/tree/master/python/python3 (MIT License, Bart Kiers)
package com.example.codereview.grammar;

import org.antlr.v4.runtime.*;

public abstract class Python3ParserBase extends Parser {
    protected Python3ParserBase(TokenStream input) {
        super(input);
    }

    public boolean CannotBePlusMinus() {
        return true;
    }

    public boolean CannotBeDotLpEq() {
        return true;
    }
}
