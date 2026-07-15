package com.hangman.ui;

import com.hangman.config.CommonConstants;
import com.hangman.model.Word;
import com.hangman.model.WordDB;
import com.hangman.util.CustomTools;
import com.hangman.util.SoundManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WordEditorPanel extends JPanel {
    private final ScreenController controller;
    private final WordDB wordDB;

    private JTable wordTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private JTextField wordField;
    private JTextField categoryField;
    private JTextField hintField;
    private JComboBox<Word.Difficulty> difficultyCombo;
    private JTextField searchField;
    
    private JPanel formPanel;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    
    private Font fontMedium;
    private Font fontSmall;

    public WordEditorPanel(ScreenController controller) {
        this.controller = controller;
        this.wordDB = new WordDB();
        
        this.fontMedium = CustomTools.createFont(CommonConstants.FONT_PATH, 18f);
        this.fontSmall = CustomTools.createFont(CommonConstants.FONT_PATH, 13f);

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        applyTheme();
    }

    private void initComponents() {
        // Title banner
        titleLabel = new JLabel("WORD DATABASE EDITOR", SwingConstants.CENTER);
        titleLabel.setFont(CustomTools.createFont(CommonConstants.FONT_PATH, 32f));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center Split Panel (Search + Table & Form)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JLabel searchLbl = new JLabel("Search Words:");
        searchLbl.setFont(fontSmall);
        searchPanel.add(searchLbl, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(fontSmall);
        searchField.addCaretListener(e -> {
            String filter = searchField.getText().trim();
            if (filter.length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + filter, 0, 1));
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Table definition
        String[] columns = {"Category", "Word", "Difficulty", "Hint"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };
        
        wordTable = new JTable(tableModel);
        wordTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        wordTable.getTableHeader().setFont(fontSmall);
        wordTable.setRowHeight(24);
        wordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        rowSorter = new TableRowSorter<>(tableModel);
        wordTable.setRowSorter(rowSorter);

        scrollPane = new JScrollPane(wordTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Form side-panel (East) or bottom (South)
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Form Labels & Inputs
        JLabel categoryLbl = new JLabel("Category:");
        categoryLbl.setFont(fontSmall);
        gbc.gridy = 0; formPanel.add(categoryLbl, gbc);
        
        categoryField = new JTextField();
        categoryField.setFont(fontSmall);
        gbc.gridy = 1; formPanel.add(categoryField, gbc);

        JLabel wordLbl = new JLabel("Word:");
        wordLbl.setFont(fontSmall);
        gbc.gridy = 2; formPanel.add(wordLbl, gbc);

        wordField = new JTextField();
        wordField.setFont(fontSmall);
        gbc.gridy = 3; formPanel.add(wordField, gbc);

        JLabel diffLbl = new JLabel("Difficulty:");
        diffLbl.setFont(fontSmall);
        gbc.gridy = 4; formPanel.add(diffLbl, gbc);

        difficultyCombo = new JComboBox<>(Word.Difficulty.values());
        difficultyCombo.setFont(fontSmall);
        gbc.gridy = 5; formPanel.add(difficultyCombo, gbc);

        JLabel hintLbl = new JLabel("Hint:");
        hintLbl.setFont(fontSmall);
        gbc.gridy = 6; formPanel.add(hintLbl, gbc);

        hintField = new JTextField();
        hintField.setFont(fontSmall);
        gbc.gridy = 7; formPanel.add(hintField, gbc);

        // Buttons Panel (add/delete)
        JPanel actSubPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        actSubPanel.setOpaque(false);
        actSubPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton addBtn = createStyledButton("ADD CHALLENGE", fontSmall, true);
        addBtn.addActionListener(e -> handleAddWord());
        actSubPanel.add(addBtn);

        JButton delBtn = createStyledButton("DELETE SELECTED", fontSmall, false);
        delBtn.addActionListener(e -> handleDeleteWord());
        actSubPanel.add(delBtn);

        gbc.gridy = 8;
        formPanel.add(actSubPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.EAST);
        formPanel.setPreferredSize(new Dimension(220, 0));

        add(contentPanel, BorderLayout.CENTER);

        // Footer options (Back to menu)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        JButton backBtn = createStyledButton("BACK TO MAIN MENU", fontMedium, true);
        backBtn.setPreferredSize(new Dimension(200, 40));
        backBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            controller.showScreen("DASHBOARD");
        });
        footerPanel.add(backBtn, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void handleAddWord() {
        String category = categoryField.getText().trim().toUpperCase();
        String word = wordField.getText().trim().toUpperCase();
        String hint = hintField.getText().trim();
        Word.Difficulty difficulty = (Word.Difficulty) difficultyCombo.getSelectedItem();

        // 1. Validations
        if (category.isEmpty() || word.isEmpty() || hint.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "All database fields (Category, Word, Hint) must be filled!", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!word.matches("^[A-Z ]+$")) {
            JOptionPane.showMessageDialog(this, 
                    "Word contains invalid characters! Only letters and spaces are allowed.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Word newWord = new Word(category, word, hint, difficulty);
        boolean success = wordDB.addWord(newWord);

        if (success) {
            SoundManager.playSound("resources/sounds/correct.wav");
            JOptionPane.showMessageDialog(this, 
                    "Word \"" + word + "\" added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clean inputs
            categoryField.setText("");
            wordField.setText("");
            hintField.setText("");
            
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "The word \"" + word + "\" already exists in the database!", 
                    "Duplicate Entry", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDeleteWord() {
        int selectedRow = wordTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a word row from the table to delete.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert index in case sorter was used
        int modelRow = wordTable.convertRowIndexToModel(selectedRow);
        String wordValue = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to permanently delete the word: \"" + wordValue + "\"?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            wordDB.removeWord(wordValue);
            SoundManager.playSound("resources/sounds/incorrect.wav");
            JOptionPane.showMessageDialog(this, 
                    "Deleted successfully!", 
                    "Deleted", 
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Word> all = wordDB.getAllWords();
        for (Word w : all) {
            tableModel.addRow(new Object[]{
                    w.getCategory(),
                    w.getWord(),
                    w.getDifficulty().toString(),
                    w.getHint()
            });
        }
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackgroundColor();
        Color fg = ThemeManager.getTextColor();
        Color primary = ThemeManager.getPrimaryColor();
        Color accent = ThemeManager.getSecondaryColor();

        setBackground(bg);
        titleLabel.setForeground(accent);
        
        scrollPane.getViewport().setBackground(primary);
        wordTable.setBackground(primary);
        wordTable.setForeground(fg);
        wordTable.setGridColor(Color.DARK_GRAY);
        wordTable.getTableHeader().setBackground(primary);
        wordTable.getTableHeader().setForeground(accent);

        formPanel.setBackground(primary);
        formPanel.setBorder(new LineBorder(accent, 1, true));

        // Color labels & fields
        for (Component c : formPanel.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(fg);
            }
        }
        
        searchField.setBackground(primary);
        searchField.setForeground(fg);
        searchField.setCaretColor(fg);
        searchField.setBorder(new LineBorder(Color.DARK_GRAY));

        wordField.setBackground(bg);
        wordField.setForeground(fg);
        wordField.setCaretColor(fg);
        wordField.setBorder(new LineBorder(Color.DARK_GRAY));

        categoryField.setBackground(bg);
        categoryField.setForeground(fg);
        categoryField.setCaretColor(fg);
        categoryField.setBorder(new LineBorder(Color.DARK_GRAY));

        hintField.setBackground(bg);
        hintField.setForeground(fg);
        hintField.setCaretColor(fg);
        hintField.setBorder(new LineBorder(Color.DARK_GRAY));

        difficultyCombo.setBackground(bg);
        difficultyCombo.setForeground(fg);
    }

    private JButton createStyledButton(String text, Font font, boolean accent) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normalBg = accent ? ThemeManager.getSecondaryColor() : ThemeManager.getPrimaryColor();
        Color hoverBg = normalBg.brighter();
        
        btn.setBackground(normalBg);
        btn.setForeground(accent ? Color.BLACK : ThemeManager.getButtonTextColor());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(accent ? ThemeManager.getSecondaryColor() : ThemeManager.getPrimaryColor());
                }
            }
        });

        return btn;
    }
}
