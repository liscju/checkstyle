package com.puppycrawl.tools.checkstyle;

public class InputBracesSingleLineElseStatements {
    public int calculate(int pos) {
        if (pos > 0) {
            return 1;
        } else
            return 2;
    }
}

