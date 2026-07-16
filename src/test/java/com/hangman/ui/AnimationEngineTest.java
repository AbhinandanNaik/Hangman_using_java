package com.hangman.ui;

import org.junit.jupiter.api.Test;
import javax.swing.JPanel;
import static org.junit.jupiter.api.Assertions.*;

public class AnimationEngineTest {

    @Test
    public void testParticleInitialization() {
        AnimationEngine.Particle particle = new AnimationEngine.Particle(640, 840);
        assertNotNull(particle);
        assertTrue(particle.x >= 0 && particle.x <= 640);
        assertTrue(particle.y < 0); // particle starts above visible threshold
        assertTrue(particle.vy >= 2.5 && particle.vy <= 6.5); // standard gravity limits
        assertNotNull(particle.color);
    }

    @Test
    public void testParticleUpdate() {
        AnimationEngine.Particle particle = new AnimationEngine.Particle(640, 840);
        double initialY = particle.y;
        
        // Simulate frame update tick
        particle.update(640, 840);
        assertTrue(particle.y > initialY, "Particle must drop vertical height on update ticks");
    }

    @Test
    public void testComponentShakeNoCrash() {
        JPanel panel = new JPanel();
        panel.setSize(200, 200);
        panel.setLocation(50, 50);
        
        assertDoesNotThrow(() -> {
            AnimationEngine.shake(panel);
        }, "Screen-shake actions should not trigger exceptions in standard components");
    }
}
