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
package jsyntaxpane.actions;

import jsyntaxpane.actions.gui.ReflectCompletionDialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.TokenType;
import jsyntaxpane.util.Configuration;
import jsyntaxpane.util.ReflectUtils;

/**
 * ComboBox like Completion Action:
 * This will display a list of items to choose from, its can be used similar to
 * IntelliSense
 * 
 * @author Ayman Al-Sairafi
 */
public class ReflectCompletionAction extends DefaultSyntaxAction {

    Map<String, String> completions;
    ReflectCompletionDialog dlg;

    public ReflectCompletionAction() {
        super("COMBO_COMPLETION");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null && target.getDocument() instanceof SyntaxDocument) {
            SyntaxDocument sDoc = (SyntaxDocument) target.getDocument();
            int dot = target.getCaretPosition();
            try {
                Window window = SwingUtilities.getWindowAncestor(target);
                if (dlg == null) {
                    dlg = new ReflectCompletionDialog(window);
                }
                String initSelection = setupDialog(sDoc, dot);
                dlg.displayFor(target, initSelection);
                String res = dlg.getResult();
                ActionUtils.insertMagicString(target, dot, res);
            } catch (BadLocationException ex) {
                Logger.getLogger(ReflectCompletionAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * The completions will for now reside on another properties style file
     * referenced by prefix.Completions.File
     * 
     * @param config
     * @param prefix
     * @param name
     */
    @Override
    public void config(Configuration config, String prefix, String name) {
        // for now we will use just one list for anything.  This can be modified
        // by having a map from TokenType to String[] or something....
        // items = config.getPrefixPropertyList(prefix, name + ".Items");
    }

    public String setupDialog(SyntaxDocument doc, int dot) {
        Token dotToken = doc.getTokenAt(dot);
        String init = "";
        List<Member> members = new ArrayList<Member>();
        Class aClass = null;
        if (dotToken != null) {
            String dotStr = dotToken.getText(doc);
            // if the current token is ".", then the ident is the prev token
            if (dotStr.equals(".")) {
                Token prevToken = doc.getPrevToken(dotToken);
                if (prevToken.type == TokenType.STRING) {
                    aClass = String.class;
                    ReflectUtils.addMethods(aClass, members);
                    dlg.setCompletionFor("methods of " + aClass);
                } else {
                    String prevStr = prevToken.getText(doc);
                    // try to find the Type of the token
                    if (findClass(prevStr) != null) {
                        // prevToken is a class, so find the static methods:
                        aClass = findClass(prevStr);
                        ReflectUtils.addStaticFields(aClass, members);
                        ReflectUtils.addStaticMethods(aClass, members);
                        dlg.setCompletionFor("static methods of " + aClass);
                    } else {
                        System.out.println("prev: " + prevStr);
                        aClass = JTextPane.class;
                        ReflectUtils.addMethods(aClass, members);
                        dlg.setCompletionFor("methods of " + aClass);
                    }
                }
            }
        }
        dlg.setItems(members);
        return init;
    }

    private Class getClass(String className) {
        Class aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
        } finally {
            return aClass;
        }
    }

    private Class findClass(String shortName) {
        Class c = null;
        for (String prefix : paths) {
            c = getClass(prefix + shortName);
            if (c != null) {
                break;
            }
        }
        return c;
    }
    
    static final String[] paths = new String[]{
        "java.lang.",
        "java.util."
    };
}
