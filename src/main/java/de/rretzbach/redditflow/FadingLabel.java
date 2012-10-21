package de.rretzbach.redditflow;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

/**
 *
 * @author rretzbach
 */
public class FadingLabel extends JLabel {

    private float alpha = 1f;
    private BufferedImage a;
    private BufferedImage b;

    public void setImageA(BufferedImage image) {
        this.a = image;
    }

    public void setImageB(BufferedImage image) {
        this.b = image;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        drawAlphaImage(g2, a, alpha);
        drawAlphaImage(g2, b, 1.0f - alpha);
        g2.dispose();
    }

    public boolean hasImage() {
        return a != null;
    }

    void setImage(final BufferedImage image) {
        if (hasImage()) {
            // add new image as overlay
            setImageB(image);

            // fadein new image in
            Timeline timeline = new Timeline(this);
            timeline.addPropertyToInterpolate("alpha", 1.0f, 0.0f);
            timeline.addCallback(new TimelineCallback() {
                public void onTimelineStateChanged(TimelineState ts, TimelineState ts1, float f, float f1) {
                    if (ts.equals(TimelineState.DONE)) {
                        // set new image to base image
                        setImageA(image);
                        setAlpha(1);
                    }
                }

                public void onTimelinePulse(float f, float f1) {
                }
            });
            timeline.play();
        } else {
            setImageA(image);
            setAlpha(0);

            // fadein new image
            Timeline timeline = new Timeline(this);
            timeline.addPropertyToInterpolate("alpha", 0.0f, 1.0f);
            timeline.play();
        }
    }

    private float computeResizeFactor(int srcW, int srcH, int availW, int availH) {
        float factor = 1;
        // is resizing needed?
        if (srcW > availW || srcH > availH) {
            float srcRatio = (float) srcW / srcH;
            float availRatio = (float) availW / availH;
            
            // is image wider than window?
            if (srcRatio > availRatio) {
                factor = (float)availW / (float)srcW;
            } else {
                factor = (float)availH / (float)srcH;
            }
        }
        return factor;
    }

    private void drawAlphaImage(Graphics2D g2, BufferedImage image, float alpha) {
        if (image != null) {
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            float factor = computeResizeFactor(image.getWidth(), image.getHeight(), getWidth(), getHeight());
            g2.drawImage(image, getX(), getY(), (int) (image.getWidth() * factor), (int) (image.getHeight() * factor), this);
        }
    }
}
