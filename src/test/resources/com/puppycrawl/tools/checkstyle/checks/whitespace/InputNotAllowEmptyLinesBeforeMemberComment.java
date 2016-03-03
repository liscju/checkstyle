package com.puppycrawl.tools.checkstyle.checks.whitespace;

public class InputNotAllowEmptyLinesBeforeMemberComment {

    public static final void method1() { }



    // Method 2 must fail
    public static final void method2() { }



    // Method 3 must fail
    public static final void method3() { }



    /**
     * Method 4 must fail
     */
    public static final void method4() { }

    /**
     * Space before method5 and before comment, should fail
     */

    public static final void method5() { }

    /**
     * Should not fail because space before
     */
    public static final void method6() { }
    /**
     * Should not fail because there is only one space between
     * method6 and method7
     */

    public static final void method7() { }

    public static final void method8() { } /**
     * Should fail because there is no space
     * between method8 and method9
     */
    public static final void method9() { }
}
