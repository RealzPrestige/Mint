package mint.clickgui.impl.alts.zprestige.ias.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiPasswordField extends GuiTextField
{
	public GuiPasswordField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height)
	{
		super(componentId, fontrendererObj, x, y, par5Width, par6Height);
	}

	@Override
	public void drawTextBox()
	{
		String password = getText();
		super.drawTextBox();
		replaceText(password);
	}

	@Override
	public boolean textboxKeyTyped(char typedChar, int keyCode)
	{
		return  !GuiScreen.isKeyComboCtrlC(keyCode) && !GuiScreen.isKeyComboCtrlX(keyCode) && super.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		String password = getText();
		super.mouseClicked(mouseX, mouseY, mouseButton);
		replaceText(password);
    return true;
	}

	private void replaceText(String newText)
	{
		int cursorPosition = getCursorPosition();
		int selectionEnd = getSelectionEnd();
		setText(newText);
		setCursorPosition(cursorPosition);
		setSelectionPos(selectionEnd);
	}
}
