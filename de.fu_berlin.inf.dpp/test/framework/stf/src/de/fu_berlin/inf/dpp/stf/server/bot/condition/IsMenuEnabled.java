package de.fu_berlin.inf.dpp.stf.server.bot.condition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class IsMenuEnabled extends DefaultCondition {

    private SWTWorkbenchBot bot;
    private String[] labels;

    IsMenuEnabled(SWTWorkbenchBot bot, String... labels) {

        this.bot = bot;
        this.labels = labels;

    }

    public String getFailureMessage() {

        return null;
    }

    public boolean test() throws Exception {
        try {
            for (String label : labels) {
                bot.menu(label);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
