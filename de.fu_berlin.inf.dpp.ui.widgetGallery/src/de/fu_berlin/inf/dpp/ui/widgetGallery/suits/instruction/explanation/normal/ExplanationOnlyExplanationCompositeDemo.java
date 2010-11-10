package de.fu_berlin.inf.dpp.ui.widgetGallery.suits.instruction.explanation.normal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.fu_berlin.inf.dpp.ui.widgetGallery.demos.Demo;
import de.fu_berlin.inf.dpp.ui.widgetGallery.demos.DemoContainer;
import de.fu_berlin.inf.dpp.ui.widgets.explanation.ExplanationComposite;

public class ExplanationOnlyExplanationCompositeDemo extends Demo {
	public ExplanationOnlyExplanationCompositeDemo(DemoContainer demoContainer, String title) {
		super(demoContainer, title);
	}
	
	@Override
	public void createPartControls(Composite parent) {
		final ExplanationComposite expl = new ExplanationComposite(parent, SWT.NONE, null);
		expl.setLayout(new FillLayout());
		Button explContent_hide = new Button(expl, SWT.PUSH);
		explContent_hide.setText("I'm a button explanation.");
	}
}