package jsyntaxpane.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;

/**
 * This class performs a Find Next operation by using the current pattern
 */
public class FindNextAction extends DefaultSyntaxAction {

    public FindNextAction() {
        super("FIND_NEXT");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        DocumentSearchData dsd = DocumentSearchData.getFromEditor(target);
        if (dsd != null) {
            dsd.doFindNext(target);
        }
    }
}
