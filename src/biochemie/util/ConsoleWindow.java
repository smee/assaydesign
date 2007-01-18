package biochemie.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class ConsoleWindow extends JFrame {

    private static ConsoleWindow INSTALLED_CONSOLE_WINDOW = null;
    JEditorPane textArea;
    ConsoleOutputStream outputStream = null;
    PrintStream printStream = null;
    OutputStream copy = null;

    public ConsoleWindow() { this(true); }

    public ConsoleWindow(boolean install) {
        super("Console Output");
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenuItem item = new JMenuItem("Copy");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    textArea.selectAll(); textArea.copy(); } });
        menubar.add(item);

        item = new JMenuItem("Clear");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    textArea.setText(null); } });
        menubar.add(item);

        item = new JMenuItem("Close");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false); } });
        menubar.add(item);

        textArea = new JEditorPane();
        textArea.setEditable(false);
        textArea.setCaretColor(textArea.getBackground());
        getContentPane().add(new JScrollPane(textArea));
        setSize(new Dimension(200, 200));
        if (install) install();
    }

    public void setCopyOutputStream(OutputStream c) {
        copy = c;
    }
    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new ConsoleOutputStream(System.out);
        return outputStream;
    }
    public PrintStream getPrintStream() {
        if (printStream == null)
            printStream = new PrintStream(getOutputStream(), true);
        return printStream;
    }
    public void install() {
        System.setOut(getPrintStream());
        System.setErr(getPrintStream());
        INSTALLED_CONSOLE_WINDOW = this;
    }
    public static ConsoleWindow getInstalledConsole() {
        return INSTALLED_CONSOLE_WINDOW;
    }
    public static void showInstalledConsole() {
        if (INSTALLED_CONSOLE_WINDOW != null)
            INSTALLED_CONSOLE_WINDOW.setVisible(true);
    }

    // WARNING - doesn't correctly translate bytes to chars.

    private class ConsoleOutputStream extends OutputStream {
        OutputStream orig;
        StringBuffer sb;
        
        public ConsoleOutputStream(OutputStream o) { 
            orig = o;
            sb=new StringBuffer();
        }
        public void write(int b) throws IOException {
            orig.write(b);
            if (copy != null) copy.write(b);
            sb.append((char)b);
            textArea.setText(sb.toString());
        }
        public void write(byte[] b, int off, int len) throws IOException {
            orig.write(b, off, len);
            if (copy != null) copy.write(b, off, len);
            sb.append(new String(b, off, len));
            textArea.setText(sb.toString());
        }
    }
}