package com.hangman.ui;

import javax.swing.*;
import java.awt.*;

public class ScreenController extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    
    private final GameplayPanel gameplayPanel;
    private final AnalyticsPanel analyticsPanel;
    private final WordEditorPanel wordEditorPanel;

    public ScreenController() {
        super("Hangman: Enterprise Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 840));
        setSize(640, 840);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Initialize view panels
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        gameplayPanel = new GameplayPanel(this);
        wordEditorPanel = new WordEditorPanel(this);
        analyticsPanel = new AnalyticsPanel(this);

        // Register panels in CardLayout
        cardsPanel.add(dashboardPanel, "DASHBOARD");
        cardsPanel.add(gameplayPanel, "GAMEPLAY");
        cardsPanel.add(wordEditorPanel, "EDITOR");
        cardsPanel.add(analyticsPanel, "ANALYTICS");

        add(cardsPanel);
        showScreen("DASHBOARD");
    }

    public void showScreen(String screenName) {
        // Refresh state before showing panel
        if ("GAMEPLAY".equals(screenName)) {
            gameplayPanel.refreshStats();
            gameplayPanel.resetMatch();
        } else if ("ANALYTICS".equals(screenName)) {
            analyticsPanel.refreshData();
        } else if ("EDITOR".equals(screenName)) {
            wordEditorPanel.refreshData();
        }
        
        cardLayout.show(cardsPanel, screenName);
    }
}
