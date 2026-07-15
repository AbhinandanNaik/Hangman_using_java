package com.hangman.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class CustomTools {
    
    public static JLabel loadImage(String resource) {
        try {
            String cleanResource = resource.startsWith("/") ? resource : "/" + resource;
            InputStream inputStream = CustomTools.class.getResourceAsStream(cleanResource);
            if (inputStream == null) {
                inputStream = CustomTools.class.getClassLoader().getResourceAsStream(resource);
            }
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resource);
            }
            BufferedImage image = ImageIO.read(inputStream);
            return new JLabel(new ImageIcon(image));
        } catch (Exception e) {
            System.err.println("Error loading image '" + resource + "': " + e);
        }
        return new JLabel();
    }

    public static void updateImage(JLabel imageContainer, String resource) {
        try {
            String cleanResource = resource.startsWith("/") ? resource : "/" + resource;
            InputStream inputStream = CustomTools.class.getResourceAsStream(cleanResource);
            if (inputStream == null) {
                inputStream = CustomTools.class.getClassLoader().getResourceAsStream(resource);
            }
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resource);
            }
            BufferedImage image = ImageIO.read(inputStream);
            imageContainer.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            System.err.println("Error updating image to '" + resource + "': " + e);
        }
    }

    public static Font createFont(String resource, float size) {
        try {
            String cleanResource = resource.startsWith("/") ? resource : "/" + resource;
            InputStream inputStream = CustomTools.class.getResourceAsStream(cleanResource);
            if (inputStream == null) {
                inputStream = CustomTools.class.getClassLoader().getResourceAsStream(resource);
            }
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resource);
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return customFont.deriveFont(size);
        } catch (Exception e) {
            System.err.println("Error creating font '" + resource + "': " + e);
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }
}
