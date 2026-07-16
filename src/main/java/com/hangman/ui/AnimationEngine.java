package com.hangman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class AnimationEngine {
    
    public static void shake(JComponent component) {
        final Point originalLocation = component.getLocation();
        final int duration = 300; // milliseconds
        final int interval = 20;  // 50 FPS tick rate
        final int amplitude = 7;  // movement offset range
        
        Timer timer = new Timer(interval, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            private final Random rand = new Random();
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= duration) {
                    component.setLocation(originalLocation);
                    timer.stop();
                } else {
                    int dx = rand.nextInt(amplitude * 2 + 1) - amplitude;
                    int dy = rand.nextInt(amplitude * 2 + 1) - amplitude;
                    component.setLocation(originalLocation.x + dx, originalLocation.y + dy);
                }
            }
        });
        timer.start();
    }

    public static class Particle {
        public double x, y;
        public double vx, vy;
        public Color color;
        public int size;
        public double angle;
        public double rotationSpeed;
        public int shape; // 0 = rect, 1 = oval, 2 = triangle

        public Particle(int width, int height) {
            Random rand = new Random();
            this.x = rand.nextInt(Math.max(1, width));
            this.y = -rand.nextInt(120) - 10;
            this.vx = rand.nextDouble() * 4 - 2;
            this.vy = rand.nextDouble() * 3 + 2.5; // fall speed
            this.size = rand.nextInt(8) + 7;
            this.angle = rand.nextDouble() * 360;
            this.rotationSpeed = rand.nextDouble() * 12 - 6;
            this.shape = rand.nextInt(3);
            
            Color[] colors = {
                Color.decode("#FF007F"),
                Color.decode("#00F0FF"),
                Color.decode("#00FFCC"),
                Color.decode("#FFD60A"),
                Color.decode("#FF5500"),
                Color.decode("#FF00FF"),
                Color.decode("#39FF14")
            };
            this.color = colors[rand.nextInt(colors.length)];
        }

        public void update(int width, int height) {
            x += vx;
            y += vy;
            angle += rotationSpeed;
            
            // Drift oscillation
            vx += Math.sin(y / 40.0) * 0.08;
            
            // bounce off side borders
            if (x < 0 || x > width) {
                vx = -vx;
            }
        }

        public void draw(Graphics2D g2) {
            g2.setColor(color);
            // Save state
            AffineTransformBackup backup = new AffineTransformBackup(g2);
            g2.translate(x, y);
            g2.rotate(Math.toRadians(angle));
            
            if (shape == 0) {
                g2.fillRect(-size/2, -size/2, size, size);
            } else if (shape == 1) {
                g2.fillOval(-size/2, -size/2, size, size);
            } else {
                int[] xPoints = {0, -size/2, size/2};
                int[] yPoints = {-size/2, size/2, size/2};
                g2.fillPolygon(xPoints, yPoints, 3);
            }
            
            // Restore state
            backup.restore(g2);
        }
    }

    private static class AffineTransformBackup {
        private final java.awt.geom.AffineTransform transform;
        
        public AffineTransformBackup(Graphics2D g2) {
            this.transform = g2.getTransform();
        }
        
        public void restore(Graphics2D g2) {
            g2.setTransform(this.transform);
        }
    }
}
