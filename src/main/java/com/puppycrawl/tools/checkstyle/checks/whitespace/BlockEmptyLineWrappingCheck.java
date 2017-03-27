////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.whitespace;

import com.puppycrawl.tools.checkstyle.api.*;

import java.util.Locale;

/**
 * Checks for empty line wrapping at the beginning of the block and at the end.
 *
 * @author <a href="mailto:piotr.listkiewicz@gmail.com">liscju</a>
 */
public class BlockEmptyLineWrappingCheck extends AbstractCheck {

    /**
     * Message no empty line wrapping.
     */
    public static final String MSG_NO_BLOCK_EMPTY_LINE_WRAPPING =
            "ws.noBlockEmptyLineWrapping";

    /**
     * Message empty line wrapping.
     */
    public static final String MSG_BLOCK_EMPTY_LINE_WRAPPING =
            "ws.blockEmptyLineWrapping";

    /**
     * Top separator policy.
     */
    private BlockEmptyLineSeparatorOption topSeparator =
            BlockEmptyLineSeparatorOption.EMPTY_LINE_ALLOWED;

    /**
     * Bottom separator policy.
     */
    private BlockEmptyLineSeparatorOption bottomSeparator =
            BlockEmptyLineSeparatorOption.EMPTY_LINE_ALLOWED;

    /**
     * Sets option for top separator.
     *
     * @param separatorStr string to decode separator from
     * @throws IllegalArgumentException if unable to decode
     */
    public void setTopSeparator(String separatorStr) {
        try {
            String separator = separatorStr.trim().toUpperCase(Locale.ENGLISH);
            topSeparator = BlockEmptyLineSeparatorOption.valueOf(separator);
        }
        catch (IllegalArgumentException ex) {
            String errMsg = "unable to parse " + separatorStr;
            throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * Sets option for bottom separator.
     * @param separatorStr string to decode separator from
     * @throws IllegalArgumentException if unable to decode
     */
    public void setBottomSeparator(String separatorStr) {
        try {
            String separator = separatorStr.trim().toUpperCase(Locale.ENGLISH);
            bottomSeparator = BlockEmptyLineSeparatorOption.valueOf(separator);
        }
        catch (IllegalArgumentException ex) {
            String errMsg = "unable to parse " + separatorStr;
            throw new IllegalArgumentException(errMsg);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.CLASS_DEF};
    }

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_SWITCH,
        };
    }

    @Override
    public void visitToken(DetailAST ast) {
        assert ast.getType() == TokenTypes.CLASS_DEF;
        final DetailAST objBlockAst  = ast.findFirstToken(TokenTypes.OBJBLOCK);
        final DetailAST lcurlyAst = objBlockAst.getFirstChild();
        final DetailAST rcurlyAst = objBlockAst.getLastChild();
        final int firstInstrLineNo = getBeginLineNo(lcurlyAst.getNextSibling());
        final int lastInstrLineNo = getEndLineNo(rcurlyAst.getPreviousSibling());

        if (firstInstrLineNo > lastInstrLineNo)
            return;

        final boolean isEmptyLineAfterBlockBegin =
                firstInstrLineNo - lcurlyAst.getLineNo() > 1;
        final boolean isEmptyLineBeforeBlockEnds =
                rcurlyAst.getLineNo() - lastInstrLineNo  > 1;

        if (topSeparator == BlockEmptyLineSeparatorOption.EMPTY_LINE
                && !isEmptyLineAfterBlockBegin) {
            log(lcurlyAst.getLineNo(), MSG_NO_BLOCK_EMPTY_LINE_WRAPPING);
        }
        if (topSeparator == BlockEmptyLineSeparatorOption.NO_EMPTY_LINE
                && isEmptyLineAfterBlockBegin) {
            log(lcurlyAst.getLineNo(), MSG_BLOCK_EMPTY_LINE_WRAPPING);
        }
        if (bottomSeparator == BlockEmptyLineSeparatorOption.EMPTY_LINE
                && !isEmptyLineBeforeBlockEnds) {
            log(rcurlyAst.getLineNo(), MSG_NO_BLOCK_EMPTY_LINE_WRAPPING);
        }
        if (bottomSeparator == BlockEmptyLineSeparatorOption.NO_EMPTY_LINE
                && isEmptyLineBeforeBlockEnds) {
            log(rcurlyAst.getLineNo(), MSG_BLOCK_EMPTY_LINE_WRAPPING);
        }
    }

    /**
     * Gets line number of the beginning of the ast.
     *
     * @param ast ast
     * @return line number of the beginning of ast including javadoc
     */
    private int getBeginLineNo(DetailAST ast) {
        final FileContents contents = getFileContents();
        final TextBlock textBlock = contents.getJavadocBefore(ast.getLineNo());
        if (textBlock != null)
            return textBlock.getStartLineNo();
        return ast.getLineNo();
    }

    /**
     * Gets line number of the end of the ast.
     *
     * @param ast ast
     * @return line number of the end of ast
     */
    private int getEndLineNo(DetailAST ast) {
        if (ast.getChildCount() > 0) {
            return getEndLineNo(ast.getLastChild());
        }
        return ast.getLineNo();
    }
}
