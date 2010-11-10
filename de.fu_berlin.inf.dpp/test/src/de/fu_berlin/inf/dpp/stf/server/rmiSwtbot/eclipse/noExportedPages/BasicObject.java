package de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.noExportedPages;

import org.eclipse.swtbot.swt.finder.waits.Conditions;

import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.EclipseObject;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.RmiSWTWorkbenchBot;

public class BasicObject extends EclipseObject {

    public BasicObject(RmiSWTWorkbenchBot rmiBot) {
        super(rmiBot);
    }

    public void waitUntilButtonEnabled(String mnemonicText) {
        waitUntil(Conditions.widgetIsEnabled(bot.button(mnemonicText)));
        // try {
        // while (!delegate.button(mnemonicText).isEnabled()) {
        // delegate.sleep(100);
        // }
        // } catch (Exception e) {
        // // next window opened
        // }
    }

    public void waitUnitButtonWithTooltipTextEnabled(String tooltipText) {
        waitUntil(Conditions
            .widgetIsEnabled(bot.buttonWithTooltip(tooltipText)));
    }

}