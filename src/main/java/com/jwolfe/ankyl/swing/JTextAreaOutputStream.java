package com.jwolfe.ankyl.swing;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class JTextAreaOutputStream extends OutputStream {
    private JTextArea textArea;

    private PrintStream textAreaPrintStream;
    private PrintStream standardOutStream;
    private PrintStream standardErrorStream;

    private boolean consoleReplaced;

    public JTextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void replaceConsole() {
        if(consoleReplaced) {
            return;
        }

        textAreaPrintStream = new PrintStream(this);
        standardOutStream = System.out;
        standardErrorStream = System.err;

        System.setOut(textAreaPrintStream);
        System.setErr(textAreaPrintStream);

        consoleReplaced = true;
    }

    public void restoreConsole() {
        if(!consoleReplaced) {
            return;
        }

        System.setOut(standardOutStream);
        System.setErr(standardErrorStream);

        consoleReplaced = false;
    }
}
