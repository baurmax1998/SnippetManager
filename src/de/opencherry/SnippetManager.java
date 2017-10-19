package de.opencherry;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.HashMap;

/**
 * Created by mbaaxur on 19.10.17.
 */
public class SnippetManager {
    private JTextArea codeArea;
    private JList list1;
    private JButton saveButton;
    private JTextField fileNameField;
    private JTextField searchField;
    private JPanel main;



    private DefaultListModel<String> model = new DefaultListModel<String>();
    private File sourceFolder =
            new File(
                    new File(
                            getClass().getProtectionDomain().getCodeSource().getLocation().getFile())
                            .getParent()+ File.separator+ "snippets"+File.separator);
    private HashMap<String, File> fileHashMap = new HashMap<String, File>();


    public SnippetManager() {
        System.out.println(sourceFolder.getAbsolutePath());
        if (!sourceFolder.exists()) {
            boolean mkdir = sourceFolder.mkdir();
        }


        list1.setModel(model);


        File[] files = sourceFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".txt");
            }
        });

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                model.addElement(name);
                fileHashMap.put(name, file);
            }
        }

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list1.getSelectedValue() != null) {


                    File file = fileHashMap.get(list1.getSelectedValue());
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();
                        String str = new String(data, "UTF-8");
                        codeArea.setText(str);
                        fileNameField.setText(file.getName());
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = fileHashMap.get(fileNameField.getText());
                if (file == null) {
                    file = new File(sourceFolder.getAbsolutePath() + File.separator + fileNameField.getText() + ".txt");
                    fileHashMap.put(file.getName(), file);
                    model.addElement(file.getName());
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    String content = codeArea.getText();
                    bw.write(content);
                    System.out.println("Done");
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        });


        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                model.clear();
                for (String element : fileHashMap.keySet()) {
                    if (element.contains(searchField.getText()))
                        model.addElement(element);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SnippetManager");
        frame.setContentPane(new SnippetManager().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
