package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import downloads.Download;

/**
 *
 * @author Joseph Arhar
 */
public class DownloaderGUI extends JFrame implements Observer {
    // ArrayList containing downloads
    private ArrayList<Download> downloads = new ArrayList<Download>();
    
    // Table Model to store download info
    private DownloadTable downloadTable;
    
    // Download Control buttons
    private JButton btnPause, btnRemove, btnResume, btnCancel, btnStart;
    
    // Selected download in the table
    private Download selectedDownload;
    
    // Textbox to get URLs from user
    private JTextField txtAdd;
    
    // JTable containing DownloadTable
    private JTable table;
    
    // True when a download is in the process of being removed
    private boolean isRemoving = false;
    
    public DownloaderGUI() {
        // Set JFrame components
        setTitle("Downloader");
        setSize(800, 600);
        
        // Close Event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        // Menu Bar setup
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Add Panel setup
        JPanel addPanel = new JPanel();
        txtAdd = new JTextField(30);
        addPanel.add(txtAdd);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionAdd();
            }
        });
        addPanel.add(addButton);
        
        // Download Table Panel setup
        downloadTable = new DownloadTable();
        table = new JTable(downloadTable);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        //Table mode makes only one row selectable
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Progress Bar setup
        DownloadPrgBar progressBar = new DownloadPrgBar(0, 100);
        progressBar.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, progressBar);
        
        // Change table to accomodate progress bar
        table.setRowHeight((int) progressBar.getPreferredSize().getHeight());
        
        // Download Panel setup
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Buttons Panel setup
        JPanel buttonsPanel = new JPanel();
        //Pause Button
        btnPause = new JButton("Pause");
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPause();
            }
        });
        btnPause.setEnabled(false);
        //Resume Button
        btnResume = new JButton("Resume");
        btnResume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionResume();
            }
        });
        btnResume.setEnabled(false);
        //Cancel Button
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionCancel();
            }
        });
        btnCancel.setEnabled(false);
        //Remove Button
        btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionRemove();
            }
        });
        btnRemove.setEnabled(false);
        //Start Button
        btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionStart();
            }
        });
        btnStart.setEnabled(false);
        //Add Buttons to Panel
        buttonsPanel.add(btnPause);
        buttonsPanel.add(btnResume);
        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnRemove);
        buttonsPanel.add(btnStart);
        
        // Add Panels to Frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private void actionPause() {
        selectedDownload.pause();
        updateButtons();
    }
    
    private void actionResume() {
        selectedDownload.resume();
        updateButtons();
    }
    
    private void actionCancel() {
        selectedDownload.cancel();
        updateButtons();
    }
    
    private void actionRemove() {
        isRemoving = true;
        downloadTable.removeDownload(table.getSelectedRow());
        selectedDownload = null;
        updateButtons();
        isRemoving = false;
    }
    
    private void actionStart() {
        selectedDownload.start();
        updateButtons();
    }
    
    private void actionAdd() {
        String url = txtAdd.getText();
        if (tryURL(url)) {
            downloadTable.addDownload(new Download(url));
            txtAdd.setText("");
        } else {
            // Show error message to user
            JOptionPane.showMessageDialog(this, "Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // This is called when the user clicks a different download and references must be updated
    private void tableSelectionChanged() {
        if (selectedDownload != null) {
            selectedDownload.deleteObserver(this);
        }
        
        if (!isRemoving && table.getSelectedRow() != -1) {
            selectedDownload = downloadTable.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(this);
            updateButtons();
        }
        
        System.out.println("selected download: " + selectedDownload.getFileName());
    }
    
    private void updateButtons() {
        System.out.println("updateButtons");
        if (selectedDownload != null) {
            switch(selectedDownload.state) {
                case Download.READY:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(true);
                    btnStart.setEnabled(true);
                    break;
                case Download.DOWNLOADING:
                    btnPause.setEnabled(true);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(false);
                    btnStart.setEnabled(false);
                    break;
                case Download.PAUSED:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(true);
                    btnCancel.setEnabled(true);
                    btnRemove.setEnabled(false);
                    btnStart.setEnabled(false);
                    break;
                case Download.ERROR:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(true);
                    btnStart.setEnabled(false);
                    break;
                case Download.COMPLETE:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(true);
                    btnStart.setEnabled(false);
                    break;
                case Download.CANCELLED:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(true);
                    btnStart.setEnabled(false);
                    break;
                default:
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRemove.setEnabled(false);
                    btnStart.setEnabled(false);
                    break;
            }
        } else {
            // No download has been selected
            btnPause.setEnabled(false);
            btnResume.setEnabled(false);
            btnCancel.setEnabled(false);
            btnRemove.setEnabled(false);
            btnStart.setEnabled(false);
        }
    }
    
    private boolean tryURL(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    // This gets called whenever a download changes its status
    @Override
    public void update(Observable o, Object arg) {
        if (selectedDownload != null && selectedDownload.equals(o))
            updateButtons();
    }
    
}