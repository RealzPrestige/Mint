package mint.clickgui.impl.alts.zprestige.ias.gui;


import mint.clickgui.impl.alts.tools.alt.AccountData;
import mint.clickgui.impl.alts.tools.alt.AltDatabase;
import mint.clickgui.impl.alts.zprestige.ias.account.ExtendedAccountData;
import mint.clickgui.impl.alts.zprestige.ias.enums.EnumBool;
import mint.clickgui.impl.alts.zprestige.ias.tools.JavaTools;
import mint.clickgui.impl.alts.zprestige.iasencrypt.EncryptionTools;

class GuiEditAccount extends AbstractAccountGui {
	private final ExtendedAccountData data;
	private final int selectedIndex;

	public GuiEditAccount(int index){
		super("Edit account");
		this.selectedIndex=index;
		AccountData data = AltDatabase.getInstance().getAlts().get(index);

		if(data instanceof ExtendedAccountData){
			this.data = (ExtendedAccountData) data;
		}else{
			this.data = new ExtendedAccountData(data.user, data.pass, data.alias, 0, JavaTools.getJavaCompat().getDate(), EnumBool.UNKNOWN);
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		setUsername(EncryptionTools.decode(data.user));
		setPassword(EncryptionTools.decode(data.pass));
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().set(selectedIndex, new ExtendedAccountData(getUsername(), getPassword(), hasUserChanged ? getUsername() : data.alias, data.useCount, data.lastused, data.premium));
	}

}
