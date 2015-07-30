package com.puppycrawl.tools.checkstyle.indentation; //indent:0 exp:0

/**                                                                            //indent:0 exp:0
 * This test-input is intended to be checked using following configuration:    //indent:1 exp:1
 *                                                                             //indent:1 exp:1
 * arrayInitIndent = 4                                                         //indent:1 exp:1
 * basicOffset = 4                                                             //indent:1 exp:1
 * braceAdjustment = 0                                                         //indent:1 exp:1
 * caseIndent = 4                                                              //indent:1 exp:1
 * forceStrictCondition = false                                                //indent:1 exp:1
 * lineWrappingIndentation = 4                                                 //indent:1 exp:1
 * tabWidth = 4                                                                //indent:1 exp:1
 * throwsIndent = 4                                                            //indent:1 exp:1
 *                                                                             //indent:1 exp:1
 */                                                                            //indent:1 exp:1
public class InputArrayInitCommentsShouldNotChangeViolationMessage { //indent:0 exp:0
    void m() { //indent:4 exp:4
        int[] array = { //indent:8 exp:8
           1,2,3 //indent:11 exp:12
        }; //indent:8 exp:8
    } //indent:4 exp:4
    void k() { //indent:4 exp:4
        int[] array = { //comment ! //indent:8 exp:8
           1,2,3 //indent:11 exp:12
        }; //indent:8 exp:8
    } //indent:4 exp:4
} //indent:0 exp:0
