package mint.clickgui.impl.alts.zprestige.ias.gui;

import mint.clickgui.impl.alts.tools.alt.AltDatabase;
import mint.clickgui.impl.alts.zprestige.ias.account.ExtendedAccountData;

public class GuiAddAccount extends AbstractAccountGui {

	public GuiAddAccount()
	{
		super("Add account");
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().add(new ExtendedAccountData(getUsername(), getPassword(), getUsername()));
	}
}
