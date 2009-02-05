/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsyntaxpane.actions.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import jsyntaxpane.SyntaxView;

/**
 * This class will render a Member.  There are Method, Field and Constructor subclasses
 * @author alsairafia
 */
abstract class MemberCell extends JPanel {

    private final JList list;
    private final boolean isSelected;
    private final Color backColor;

    public MemberCell(JList list, boolean isSelected, Color backColor) {
        super();
        this.list = list;
        this.isSelected = isSelected;
        this.backColor = backColor;
    }

    @Override
    public void paintComponent(Graphics g) {
        SyntaxView.setRenderingHits((Graphics2D) g);
        g.setFont(list.getFont());
        super.paintComponent(g);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(isSelected ? list.getSelectionBackground() : backColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(isSelected ? list.getSelectionForeground() : list.getForeground());
        g.drawImage(getIcon(), 2, 0, null);
        int x = 6 + getIcon().getWidth(this);
        int y = fm.getAscent();
        Font bold = list.getFont().deriveFont(Font.BOLD);
        g.setFont(bold);
        x = drawString(getBoldText(), x, y, g);
        g.setFont(list.getFont());
        x = drawString(getNormalText(), x, y, g);
        String right = getRightText();
        int rw = fm.stringWidth(right);
        g.drawString(right, getWidth() - rw - 4, fm.getAscent());
    }

    @Override
    public Dimension getPreferredSize() {
        Font font = list.getFont();
        Graphics g = getGraphics();
        FontMetrics fm = g.getFontMetrics(font);
        // total text for this component:
        String total = getBoldText() + getNormalText() + getRightText() + "  ";
        return new Dimension(fm.stringWidth(total) + 20, fm.getHeight());
    }

    private int drawString(String string, int x, int y, Graphics g) {
        int w = g.getFontMetrics().stringWidth(string);
        g.drawString(string, x, y);
        return x + w;
    }

    /**
     * Read all relevant icons and returns the Map.  The loc should contain the
     * fully qualified URL for the icons.  The icon names read will have the words
     * _private, protected, _static, _static_private and _static_protected and the
     * extension ".png" appended.
     * @param loc root for icon locations
     * @return Map (can be used directly with getModifiers & 0xf)
     */
    Map<Integer, Image> readIcons(String loc) {
        Map<Integer, Image> icons = new Hashtable<Integer, Image>();
        icons.put(Modifier.PUBLIC, readImage(loc, ""));
        icons.put(Modifier.PRIVATE, readImage(loc, "_private"));
        icons.put(Modifier.PROTECTED, readImage(loc, "_protected"));
        icons.put(Modifier.STATIC | Modifier.PUBLIC, readImage(loc, "_static"));
        icons.put(Modifier.STATIC | Modifier.PRIVATE, readImage(loc, "_static_private"));
        icons.put(Modifier.STATIC | Modifier.PROTECTED, readImage(loc, "_static_protected"));
        return icons;
    }

    private Image readImage(String iconLoc, String kind) {
        String fullPath = iconLoc + kind + ".png";
        URL loc = this.getClass().getResource(fullPath);
        if (loc == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "Unable to get icon at: " + fullPath);
            return null;
        } else {
            Image i = new ImageIcon(loc).getImage();
            return i;
        }
    }

    abstract protected String getBoldText();

    abstract protected String getNormalText();

    abstract protected String getRightText();

    abstract protected Image getIcon();
}
