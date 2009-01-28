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
import java.util.Map;
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

    @Override
    protected String getRightText() {
        return method.getReturnType().getSimpleName();
    }

    @Override
    protected Image getIcon() {
        int type = method.getModifiers() & 0xf; // only get public/private/protected/static
        if(icons == null) {
            icons = readIcons(METHOD_ICON_LOC);
        }
        return icons.get(type);
    }

    private static Map<Integer, Image> icons = null;
    public static final String METHOD_ICON_LOC = "/META-INF/images/completions/method";
}
