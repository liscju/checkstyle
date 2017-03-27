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

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.puppycrawl.tools.checkstyle.checks.whitespace.BlockEmptyLineWrappingCheck.MSG_BLOCK_EMPTY_LINE_WRAPPING;
import static com.puppycrawl.tools.checkstyle.checks.whitespace.BlockEmptyLineWrappingCheck.MSG_NO_BLOCK_EMPTY_LINE_WRAPPING;
import static org.junit.Assert.*;

public class BlockEmptyLineWrappingCheckTest
        extends BaseCheckTestSupport {
    @Override
    protected String getPath(String filename) throws IOException {
        return super.getPath("checks" + File.separator
                + "whitespace" + File.separator
                + "blockemptylinewrapping" + File.separator + filename);
    }

    @Test
    public void testGetRequiredTokens() throws Exception {
        final BlockEmptyLineWrappingCheck checkObj = new BlockEmptyLineWrappingCheck();
        assertArrayEquals(CommonUtils.EMPTY_INT_ARRAY, checkObj.getRequiredTokens());
    }

    @Test
    public void testGetDefaultTokens() throws Exception {
        final BlockEmptyLineWrappingCheck checkObj = new BlockEmptyLineWrappingCheck();
        final int[] expected = {TokenTypes.CLASS_DEF};
        assertArrayEquals(expected, checkObj.getDefaultTokens());
    }

    @Test
    public void testGetAcceptableTokens() throws Exception {
        final BlockEmptyLineWrappingCheck checkObj = new BlockEmptyLineWrappingCheck();
        final int[] expected = {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_SWITCH,
        };
        assertArrayEquals(expected, checkObj.getAcceptableTokens());
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);

        final String[] expected = CommonUtils.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }

    @Test
    public void testExplicitDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("topSeparator", "empty_line_allowed");
        checkConfig.addAttribute("bottomSeparator", "empty_line_allowed");

        final String[] expected = CommonUtils.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }

    @Test
    public void testTopSeparatorEmptyLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("topSeparator", "empty_line");
        final String[] expected = {
                "5: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "9: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "32: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "37: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
        };
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }

    @Test
    public void testBottomSeparatorEmptyLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("bottomSeparator", "empty_line");
        final String[] expected = {
                "7: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "17: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "22: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "30: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
        };
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }

    @Test
    public void testTopSeparatorNoEmptyLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("topSeparator", "no_empty_line");
        final String[] expected = {
                "3: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "19: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "24: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "45: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
        };
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }

    @Test
    public void testBottomSeparatorNoEmptyLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("bottomSeparator", "no_empty_line");
        final String[] expected = {
                "35: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "43: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "49: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
                "54: " + getCheckMessage(MSG_BLOCK_EMPTY_LINE_WRAPPING),
        };
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }
    
    @Test
    public void testTopBottomEmptyLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(BlockEmptyLineWrappingCheck.class);
        checkConfig.addAttribute("topSeparator", "empty_line");
        checkConfig.addAttribute("bottomSeparator", "empty_line");
        final String[] expected = {
                "5: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "7: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "9: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "17: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "22: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "30: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "32: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
                "37: " + getCheckMessage(MSG_NO_BLOCK_EMPTY_LINE_WRAPPING),
        };
        verify(checkConfig, getPath("InputBaseClassCases.java"), expected);
    }
}