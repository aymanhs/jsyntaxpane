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
import java.awt.Component;
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

class IntelliListRenderer extends DefaultListCellRenderer {

    static final Color evensColor = new Color(15663086);

    @Override
    public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, boolean cellHasFocus) {
        Color back = (index % 2 == 1) ? list.getBackground() : evensColor;
        if (value instanceof Method) {
            final Method method = (Method) value;
            return new MethodCell(list, isSelected, back, method);
        } else if (value instanceof Field) {
            Field field = (Field) value;
            return new FieldCell(list, isSelected, back, field);
        }
        JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (index % 2 == 1 && (!isSelected)) {
            lbl.setBackground(evensColor);
        }
        if (value instanceof String) {
            String string = (String) value;
            if (string.contains("byte")) {
                lbl.setFont(getFont().deriveFont(Font.ITALIC | Font.BOLD));
            }
        }
        return lbl;
    }
}
