////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2002  Oliver Burn
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

package com.puppycrawl.tools.checkstyle.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import antlr.ANTLRException;

import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Displays information about a parse tree.
 * The user can change the file that is parsed and displayed
 * through a JFileChooser.
 *
 * @author Lars Kühne
 * @author <a href="mailto:piotr.listkiewicz@gmail.com">liscju</a>
 */
public class ParseTreeInfoPanel extends JPanel {
    /** For Serialisation that will never happen. */
    private static final long serialVersionUID = -4243405131202059043L;

    private final transient ParseTreeModel parseTreeModel;
    private final JTextArea jTextArea;
    private File lastDirectory;
    private File currentFile;
    private final Action reloadAction;
    private final List<Integer>   lines2position  = new ArrayList<>();
    private final JTextPane status;

    private static class JavaFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f == null) {
                return false;
            }
            return f.isDirectory() || f.getName().endsWith(".java");
        }

        @Override
        public String getDescription() {
            return "Java Source Code";
        }
    }

    public void openAst(DetailAST parseTree, final Component parent) {
        parseTreeModel.setParseTree(parseTree);
        reloadAction.setEnabled(true);

        // clear for each new file
        getLines2position().clear();
        // starts line counting at 1
        getLines2position().add(0);
        // insert the contents of the file to the text area

        // clean the text area before inserting the lines of the new file
        if (jTextArea.getText().length() != 0) {
            jTextArea.replaceRange("", 0, jTextArea.getText().length());
        }

        // move back to the top of the file
        jTextArea.moveCaretPosition(0);
    }

    private class FileSelectionAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -1926935338069418119L;

        public FileSelectionAction() {
            super("Select Java File");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fc = new JFileChooser( lastDirectory );
            final FileFilter filter = new JavaFileFilter();
            fc.setFileFilter(filter);
            final Component parent =
                SwingUtilities.getRoot(ParseTreeInfoPanel.this);
            fc.showDialog(parent, "Open");
            final File file = fc.getSelectedFile();
            openFile(file, parent);

        }
    }

    private class ReloadAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -1021880396046355863L;

        public ReloadAction() {
            super("Reload Java File");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Component parent =
                SwingUtilities.getRoot(ParseTreeInfoPanel.this);
            openFile(currentFile, parent);
        }
    }


    private class FileDropListener implements FileDrop.Listener {
        private final JScrollPane mSp;

        @Override
        public void filesDropped(File... files) {
            if (files != null && files.length > 0) {
                final File file = files[0];
                openFile(file, mSp);
            }
        }

        public FileDropListener(JScrollPane aSp) {
            mSp = aSp;
        }
    }


    public void openFile(File file, final Component parent) {
        if (file != null) {
            try {
                Main.frame.setTitle("Checkstyle : " + file.getName());
                final FileText text = new FileText(file.getAbsoluteFile(),
                                                   getEncoding());
                final DetailAST parseTree = parseFile(text);
                parseTreeModel.setParseTree(parseTree);
                currentFile = file;
                lastDirectory = file.getParentFile();
                reloadAction.setEnabled(true);

                final String[] sourceLines = text.toLinesArray();

                // clear for each new file
                 getLines2position().clear();
                 // starts line counting at 1
                 getLines2position().add(0);
                 // insert the contents of the file to the text area
                 for (String element : sourceLines) {
                   getLines2position().add(jTextArea.getText().length());
                   jTextArea.append(element + "\n");
                 }

                //clean the text area before inserting the lines of the new file
                if (jTextArea.getText().length() != 0) {
                    jTextArea.replaceRange("", 0, jTextArea.getText()
                            .length());
                }

                // insert the contents of the file to the text area
                for (final String element : sourceLines) {
                    jTextArea.append(element + "\n");
                }

                // move back to the top of the file
                jTextArea.moveCaretPosition(0);
            }
            catch (final IOException | ANTLRException ex) {
                showErrorDialog(
                        parent,
                        "Could not parse" + file + ": " + ex.getMessage());
            }
        }
    }

    /**
     * Parses a file and returns the parse tree.
     * @param fileName the file to parse
     * @return the root node of the parse tree
     * @throws IOException if the file cannot be opened
     * @throws ANTLRException if the file is not a Java source
     * @deprecated Use {@link #parseFile(FileText)} instead
     */
    @Deprecated
    public static DetailAST parseFile(String fileName)
        throws IOException, ANTLRException {
        return parseFile(new FileText(new File(fileName), getEncoding()));
    }

    /**
     * Parses a file and returns the parse tree.
     * @param text the file to parse
     * @return the root node of the parse tree
     * @throws ANTLRException if the file is not a Java source
     */
    public static DetailAST parseFile(FileText text)
        throws ANTLRException {
        final FileContents contents = new FileContents(text);
        return TreeWalker.parse(contents);
    }

    /**
     * Returns the configured file encoding.
     * This can be set using the {@code file.encoding} system property.
     * It defaults to UTF-8.
     * @return the configured file encoding
     */
    private static String getEncoding() {
        return System.getProperty("file.encoding", "UTF-8");
    }

    /**
     * Create a new ParseTreeInfoPanel instance.
     */
    public ParseTreeInfoPanel() {
        setLayout(new BorderLayout());

        parseTreeModel = new ParseTreeModel(null);
        JTreeTable treeTable = new JTreeTable(parseTreeModel);
        final JScrollPane sp = new JScrollPane(treeTable);
        this.add(sp, BorderLayout.NORTH);

        final JButton fileSelectionButton =
            new JButton(new FileSelectionAction());

        reloadAction = new ReloadAction();
        reloadAction.setEnabled(false);
        final JButton reloadButton = new JButton(reloadAction);

        jTextArea = createTextArea();
        treeTable.setEditor(jTextArea);
        treeTable.setLinePositionMap(lines2position);

        final JPanel southBorderPanel = new JPanel(new GridLayout(2,1));
        this.add(southBorderPanel, BorderLayout.SOUTH);

        status = createStatusBar();
        southBorderPanel.add(status);

        final JPanel actionButtonPanel = new JPanel(new GridLayout(1,2));
        actionButtonPanel.add(fileSelectionButton);
        actionButtonPanel.add(reloadButton);

        southBorderPanel.add(actionButtonPanel);

        try {
            new FileDrop(sp, new FileDropListener(sp));
        }
        catch (final TooManyListenersException ex) {
           showErrorDialog(null, "Cannot initialize Drag and Drop support");
        }

    }

    private JTextPane createStatusBar() {
        JTextPane statusBar = new JTextPane();
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        statusBar.setParagraphAttributes(attribs, true);
        return statusBar;
    }

    private RSyntaxTextArea createTextArea() {
        final RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(20,15);
        rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rSyntaxTextArea.setEditable(false);
        rSyntaxTextArea.setPopupMenu(null);

        rSyntaxTextArea.addFocusListener(new TextAreaFocusListener(rSyntaxTextArea));
        rSyntaxTextArea.addCaretListener(new TextAreaCaretListener());

        RTextScrollPane scrollPane = new RTextScrollPane(rSyntaxTextArea);
        this.add(scrollPane);
        return rSyntaxTextArea;
    }

    private void updateStatus(int lineNumber, int columnNumber) {
        String lineNumberFormat = String.format("%3s", lineNumber);
        String columnNumberFormat = String.format("%2s", columnNumber);
        status.setText("Line: " + lineNumberFormat + " Column: " + columnNumberFormat);
    }

    private static void showErrorDialog(final Component parent, final String msg) {
        final Runnable showError = new FrameShower(parent, msg);
        SwingUtilities.invokeLater(showError);
    }

    public List<Integer> getLines2position() {
      return lines2position;
    }

    /**
     * http://findbugs.sourceforge.net/bugDescriptions.html#SW_SWING_METHODS_INVOKED_IN_SWING_THREAD
     */
    private static class FrameShower implements Runnable {
        /**
         * frame
         */
        final Component parent;

        /**
         * frame
         */
        final String msg;

        /**
         * contstructor
         */
        public FrameShower(Component parent, final String msg) {
            this.parent = parent;
            this.msg = msg;
        }

        /**
         * display a frame
         */
        public void run() {
            JOptionPane.showMessageDialog(parent, msg);
        }
    }

    private static class TextAreaFocusListener implements FocusListener {
        private final JTextArea textArea;

        public TextAreaFocusListener(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void focusGained(FocusEvent e) {
            textArea.getCaret().setVisible(true); // fixing hiding caret after losing focus
        }

        @Override
        public void focusLost(FocusEvent e) {
            // do nothing
        }
    }

    private class TextAreaCaretListener implements CaretListener {
        @Override
        public void caretUpdate(CaretEvent e) {
            JTextArea editArea = (JTextArea) e.getSource();
            int linenum = 1;
            int columnnum = 1;
            try {
                int caretpos = editArea.getCaretPosition();
                linenum = editArea.getLineOfOffset(caretpos);

                columnnum = caretpos - editArea.getLineStartOffset(linenum);

                linenum += 1;
            } catch (BadLocationException ex) {
                // can't happen because location is taken from caret position,
                // added statement to overcome PMD 'Avoid empty catch blocks'
                return;
            }

            updateStatus(linenum, columnnum);
        }
    }
}

