////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks for empty line separators after header, package, all import declarations,
 * fields, constructors, methods, nested classes,
 * static initializers and instance initializers.
 *
 * <p> By default the check will check the following statements:
 *  {@link TokenTypes#PACKAGE_DEF PACKAGE_DEF},
 *  {@link TokenTypes#IMPORT IMPORT},
 *  {@link TokenTypes#CLASS_DEF CLASS_DEF},
 *  {@link TokenTypes#INTERFACE_DEF INTERFACE_DEF},
 *  {@link TokenTypes#STATIC_INIT STATIC_INIT},
 *  {@link TokenTypes#INSTANCE_INIT INSTANCE_INIT},
 *  {@link TokenTypes#METHOD_DEF METHOD_DEF},
 *  {@link TokenTypes#CTOR_DEF CTOR_DEF},
 *  {@link TokenTypes#VARIABLE_DEF VARIABLE_DEF}.
 * </p>
 *
 * <p>
 * Example of declarations without empty line separator:
 * </p>
 *
 * <pre>
 * ///////////////////////////////////////////////////
 * //HEADER
 * ///////////////////////////////////////////////////
 * package com.puppycrawl.tools.checkstyle.whitespace;
 * import java.io.Serializable;
 * class Foo
 * {
 *     public static final int FOO_CONST = 1;
 *     public void foo() {} //should be separated from previous statement.
 * }
 * </pre>
 *
 * <p> An example of how to configure the check with default parameters is:
 * </p>
 *
 * <pre>
 * &lt;module name="EmptyLineSeparator"/&gt;
 * </pre>
 *
 * <p>
 * Example of declarations with empty line separator
 * that is expected by the Check by default:
 * </p>
 *
 * <pre>
 * ///////////////////////////////////////////////////
 * //HEADER
 * ///////////////////////////////////////////////////
 *
 * package com.puppycrawl.tools.checkstyle.whitespace;
 *
 * import java.io.Serializable;
 *
 * class Foo
 * {
 *     public static final int FOO_CONST = 1;
 *
 *     public void foo() {}
 * }
 * </pre>
 * <p> An example how to check empty line after
 * {@link TokenTypes#VARIABLE_DEF VARIABLE_DEF} and
 * {@link TokenTypes#METHOD_DEF METHOD_DEF}:
 * </p>
 *
 * <pre>
 * &lt;module name="EmptyLineSeparator"&gt;
 *    &lt;property name="tokens" value="VARIABLE_DEF, METHOD_DEF"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>
 * An example how to allow no empty line between fields:
 * </p>
 * <pre>
 * &lt;module name="EmptyLineSeparator"&gt;
 *    &lt;property name="allowNoEmptyLineBetweenFields" value="true"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>
 * Example of declarations with multiple empty lines between class members (allowed by default):
 * </p>
 *
 * <pre>
 * ///////////////////////////////////////////////////
 * //HEADER
 * ///////////////////////////////////////////////////
 *
 *
 * package com.puppycrawl.tools.checkstyle.whitespace;
 *
 *
 *
 * import java.io.Serializable;
 *
 *
 * class Foo
 * {
 *     public static final int FOO_CONST = 1;
 *
 *
 *
 *     public void foo() {}
 * }
 * </pre>
 * <p>
 * An example how to disallow multiple empty lines between class members:
 * </p>
 * <pre>
 * &lt;module name="EmptyLineSeparator"&gt;
 *    &lt;property name="allowMultipleEmptyLines" value="false"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @author maxvetrenko
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 */
public class EmptyLineSeparatorCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message empty.line.separator in "messages.properties"
     * file.
     */
    public static final String MSG_SHOULD_BE_SEPARATED = "empty.line.separator";

    /**
     * A key is pointing to the warning message empty.line.separator.multiple.lines
     *  in "messages.properties"
     * file.
     */
    public static final String MSG_MULTIPLE_LINES = "empty.line.separator.multiple.lines";

    /**
     * A key is pointing to the warning message empty.line.separator.lines.after
     * in "messages.properties" file.
     */
    public static final String MSG_MULTIPLE_LINES_AFTER =
            "empty.line.separator.multiple.lines.after";

    /** Allows no empty line between fields. */
    private boolean allowNoEmptyLineBetweenFields;

    /** Allows multiple empty lines between class members. */
    private boolean allowMultipleEmptyLines = true;

    /**
     * Allow no empty line between fields.
     * @param allow
     *        User's value.
     */
    public final void setAllowNoEmptyLineBetweenFields(boolean allow) {
        allowNoEmptyLineBetweenFields = allow;
    }

    /**
     * Allow multiple empty lines between class members.
     * @param allow User's value.
     */
    public void setAllowMultipleEmptyLines(boolean allow) {
        allowMultipleEmptyLines = allow;
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.IMPORT,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.STATIC_INIT,
            TokenTypes.INSTANCE_INIT,
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.VARIABLE_DEF,
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return ArrayUtils.EMPTY_INT_ARRAY;
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (hasMultipleLinesBefore(ast)) {
            log(ast.getLineNo(), MSG_MULTIPLE_LINES, ast.getText());
        }

        final DetailAST nextToken = ast.getNextSibling();
        if (nextToken != null) {
            final int astType = ast.getType();
            switch (astType) {
                case TokenTypes.VARIABLE_DEF:
                    processVariableDef(ast, nextToken);
                    break;
                case TokenTypes.IMPORT:
                    processImport(ast, nextToken, astType);
                    break;
                case TokenTypes.PACKAGE_DEF:
                    processPackage(ast, nextToken);
                    break;
                default:
                    if (nextToken.getType() == TokenTypes.RCURLY) {
                        if (hasNotAllowedTwoEmptyLinesBefore(nextToken)) {
                            log(ast.getLineNo(), MSG_MULTIPLE_LINES_AFTER, ast.getText());
                        }
                    }
                    else if (!hasEmptyLineAfter(ast)) {
                        log(nextToken.getLineNo(), MSG_SHOULD_BE_SEPARATED,
                            nextToken.getText());
                    }
            }
        }
    }

    /**
     * Whether the token has not allowed multiple empty lines before.
     * @param ast the ast to check.
     * @return true if the token has not allowed multiple empty lines before.
     */
    private boolean hasMultipleLinesBefore(DetailAST ast) {
        boolean result = false;
        if ((ast.getType() != TokenTypes.VARIABLE_DEF
            || isTypeField(ast))
                && hasNotAllowedTwoEmptyLinesBefore(ast)) {
            result = true;
        }
        return result;
    }

    /**
     * Process Package.
     * @param ast token
     * @param nextToken next token
     */
    private void processPackage(DetailAST ast, DetailAST nextToken) {
        if (ast.getLineNo() > 1 && !hasEmptyLineBefore(ast)) {
            log(ast.getLineNo(), MSG_SHOULD_BE_SEPARATED, ast.getText());
        }
        if (!hasEmptyLineAfter(ast)) {
            log(nextToken.getLineNo(), MSG_SHOULD_BE_SEPARATED, nextToken.getText());
        }
    }

    /**
     * Process Import.
     * @param ast token
     * @param nextToken next token
     * @param astType token Type
     */
    private void processImport(DetailAST ast, DetailAST nextToken, int astType) {
        if (astType != nextToken.getType() && !hasEmptyLineAfter(ast)) {
            log(nextToken.getLineNo(), MSG_SHOULD_BE_SEPARATED, nextToken.getText());
        }
    }

    /**
     * Process Variable.
     * @param ast token
     * @param nextToken next Token
     */
    private void processVariableDef(DetailAST ast, DetailAST nextToken) {
        if (isTypeField(ast) && !hasEmptyLineAfter(ast)
                && isViolatingEmptyLineBetweenFieldsPolicy(nextToken)) {
            log(nextToken.getLineNo(), MSG_SHOULD_BE_SEPARATED,
                    nextToken.getText());
        }
    }

    /**
     * Checks whether token placement violates policy of empty line between fields.
     * @param detailAST token to be analyzed
     * @return true if policy is violated and warning should be raised; false otherwise
     */
    private boolean isViolatingEmptyLineBetweenFieldsPolicy(DetailAST detailAST) {
        return allowNoEmptyLineBetweenFields
                    && detailAST.getType() != TokenTypes.VARIABLE_DEF
                    && detailAST.getType() != TokenTypes.RCURLY
                || !allowNoEmptyLineBetweenFields
                    && detailAST.getType() != TokenTypes.RCURLY;
    }

    /**
     * Checks if a token has empty two previous lines and multiple empty lines is not allowed.
     * @param token DetailAST token
     * @return true, if token has empty two lines before and allowMultipleEmptyLines is false
     */
    private boolean hasNotAllowedTwoEmptyLinesBefore(DetailAST token) {
        return !allowMultipleEmptyLines && hasEmptyLineBefore(token)
                && isPrePreviousLineEmpty(token);
    }

    /**
     * Checks if a token has empty pre-previous line.
     * @param token DetailAST token.
     * @return true, if token has empty lines before.
     */
    private boolean isPrePreviousLineEmpty(DetailAST token) {
        boolean result = false;
        final int previousLineNo = firstNotCommentLineBeforeLine(token.getLineNo());
        if (previousLineNo >= 1) {
            final int prePreviousLineNo = firstNotCommentLineBeforeLine(previousLineNo);
            if (prePreviousLineNo >= 1) {
                final String prePreviousLine = getLines()[prePreviousLineNo - 1];
                result = prePreviousLine.trim().isEmpty();
            }
        }
        return result;
    }

    /**
     * Checks if token have empty line after.
     * @param token token.
     * @return true if token have empty line after.
     */
    private boolean hasEmptyLineAfter(DetailAST token) {
        DetailAST lastToken = token.getLastChild().getLastChild();
        if (lastToken == null) {
            lastToken = token.getLastChild();
        }
        // Start of the next token
        final int nextBegin = token.getNextSibling().getLineNo();
        // End of current token.
        final int currentEnd = lastToken.getLineNo();
        return hasEmptyLine(currentEnd + 1, nextBegin - 1);
    }

    /**
     * Checks, whether there are empty lines within the specified line range. Line numbering is
     * started from 1 for parameter values
     * @param startLine number of the first line in the range
     * @param endLine number of the second line in the range
     * @return <code>true</code> if found any blank line within the range, <code>false</code>
     *         otherwise
     */
    private boolean hasEmptyLine(int startLine, int endLine) {
        // Initial value is false - blank line not found
        boolean result = false;
        if (startLine <= endLine) {
            final FileContents fileContents = getFileContents();
            for (int line = startLine; line <= endLine; line++) {
                // Check, if the line is blank. Lines are numbered from 0, so subtract 1
                if (fileContents.lineIsBlank(line - 1)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks if a token has a empty line before.
     * @param token token.
     * @return true, if token have empty line before.
     */
    private boolean hasEmptyLineBefore(DetailAST token) {
        boolean result = false;
        final int lineNo = token.getLineNo();
        if (lineNo == 1) {
            return false;
        }
        int firstNotCommentLineBeforeToken = firstNotCommentLineBeforeLine(token.getLineNo());
        if (firstNotCommentLineBeforeToken >= 1) {
            final String lineBefore = getLines()[firstNotCommentLineBeforeToken - 1];
            result = lineBefore.trim().isEmpty();
        }

        return result;
    }

    private int firstNotCommentLineBeforeLine(int line) {
        // lineNo - 2 is the number of the previous line because the numbering starts from zero,
        // so lineNo - 1 equals current position
        int lineIndex = line - 2;
        boolean insideComment = false;
        while (lineIndex >= 0) {
            if (singleCommentLine(lineIndex)) {
                lineIndex--;
            }
            else if (endsMultilineComment(lineIndex)) {
                insideComment = true;
                lineIndex--;
            }
            else if (beginsMultilineComment(lineIndex)) {
                insideComment = false;
                lineIndex--;
            }
            else if (insideComment) {
                lineIndex--;
            }
            else {
                break;
            }
        }
        // getLines() count numbers from 0, tokens from 1, so
        // to return line number like in tokens we have to add 1
        return lineIndex + 1;
    }

    private boolean singleCommentLine(int line) {
        FileContents fileContents = getFileContents();
        return fileContents.lineIsComment(line);
    }

    private boolean endsMultilineComment(int lineIndex) {
        final String MATCH_END_OF_MULTILINE_COMMENT = "^.*\\*/\\s*$";
        Pattern compile = Pattern.compile(MATCH_END_OF_MULTILINE_COMMENT);
        return compile.matcher(getLines()[lineIndex]).matches();
    }

    private boolean beginsMultilineComment(int lineIndex) {
        String MATCH_BEGINS_COMMENT_PAT = "^.*/\\*.*$";
        Pattern compile = Pattern.compile(MATCH_BEGINS_COMMENT_PAT);
        return compile.matcher(getLines()[lineIndex]).matches();
    }

    /**
     * If variable definition is a type field.
     * @param variableDef variable definition.
     * @return true variable definition is a type field.
     */
    private static boolean isTypeField(DetailAST variableDef) {
        final int parentType = variableDef.getParent().getParent().getType();
        return parentType == TokenTypes.CLASS_DEF;
    }
}
