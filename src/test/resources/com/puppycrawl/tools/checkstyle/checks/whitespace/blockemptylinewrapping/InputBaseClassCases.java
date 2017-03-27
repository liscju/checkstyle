package com.puppycrawl.tools.checkstyle.checks.whitespace.blockemptylinewrapping;

public class InputBaseClassCases {

    private static class NoEmptyLineClass {
        int i;
    }

    private static class NoEmptyLineClassComment {
        /**
         * Comment
         */
        int i;
        /**
         * Comment
         */
    }

    private static class TopEmptyLineClass {

        int i;
    }

    private static class TopEmptyLineClassComment {

        int i;
        /**
         * Comment.
         */
    }

    private static class BottomEmptyLineClass {
        int i;

    }

    private static class BottomEmptyLineClassComment {
        /**
         * Comment.
         */
        int i;

    }

    private static class TopAndBottomEmptyLineClass {

        int i;

    }

    private static class EmptyClass {
    }

}
