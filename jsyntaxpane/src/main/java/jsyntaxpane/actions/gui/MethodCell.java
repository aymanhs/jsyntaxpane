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
import java.awt.Image;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JList;
import jsyntaxpane.util.ReflectUtils;

class MethodCell extends MemberCell {

    private final Method method;

    public MethodCell(JList list, boolean isSelected, Color backColor, Method method) {
        super(list, isSelected, backColor);
        this.method = method;
    }

    @Override
    protected String getBoldText() {
        return method.getName();
    }

    @Override
    protected String getNormalText() {
        return ReflectUtils.getParamsString(method.getParameterTypes());
    }

    protected String getRightText() {
        return method.getReturnType().getSimpleName();
    }

    @Override
    protected Image getIcon() {
        URL loc = this.getClass().getResource(METHOD_ICON_LOC);
        if (loc == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "Unable to get icon at: " + METHOD_ICON_LOC);
            return null;
        } else {
            Image i = new ImageIcon(loc).getImage();
            return i;
        }
    }

    private static Image[] icons = null;
    public static final String METHOD_ICON_LOC = "/META-INF/images/completions/method.png";
}
