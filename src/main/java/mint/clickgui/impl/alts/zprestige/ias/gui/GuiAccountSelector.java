package mint.clickgui.impl.alts.zprestige.ias.gui;

import mint.clickgui.impl.alts.tools.Config;
import mint.clickgui.impl.alts.tools.Tools;
import mint.clickgui.impl.alts.tools.alt.AccountData;
import mint.clickgui.impl.alts.tools.alt.AltDatabase;
import mint.clickgui.impl.alts.tools.alt.AltManager;
import mint.clickgui.impl.alts.zprestige.ias.account.AlreadyLoggedInException;
import mint.clickgui.impl.alts.zprestige.ias.account.ExtendedAccountData;
import mint.clickgui.impl.alts.zprestige.ias.config.ConfigValues;
import mint.clickgui.impl.alts.zprestige.ias.enums.EnumBool;
import mint.clickgui.impl.alts.zprestige.ias.tools.HttpTools;
import mint.clickgui.impl.alts.zprestige.ias.tools.JavaTools;
import mint.clickgui.impl.alts.zprestige.ias.tools.SkinTools;
import mint.clickgui.impl.alts.zprestige.iasencrypt.EncryptionTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GuiAccountSelector extends GuiScreen {
  private int selectedAccountIndex = 0;
  private int prevIndex = 0;
  private Throwable loginfailed;
  private ArrayList<ExtendedAccountData> queriedaccounts = convertData();
  private List accountsgui;
  private GuiButton login;
  private GuiButton loginoffline;
  private GuiButton delete;
  private GuiButton edit;
  private GuiButton reloadskins;
  private String query;
  private GuiTextField search;

  @Override
  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    accountsgui = new List(this.mc);
    accountsgui.registerScrollButtons(5, 6);
    query = I18n.format("Search");
    this.buttonList.clear();
    //Above Top Row
    this.buttonList.add(reloadskins = new GuiButton(8, this.width / 2 - 154 - 10, this.height - 76 - 8, 120, 20, I18n.format("Reload skins")));
    //Top Row
    this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 40, this.height - 52, 120, 20, I18n.format("Add account")));
    this.buttonList.add(login = new GuiButton(1, this.width / 2 - 154 - 10, this.height - 52, 120, 20, I18n.format("Login")));
    this.buttonList.add(edit = new GuiButton(7, this.width / 2 - 40, this.height - 52, 80, 20, I18n.format("Edit")));
    //Bottom Row
    this.buttonList.add(loginoffline = new GuiButton(2, this.width / 2 - 154 - 10, this.height - 28, 110, 20, I18n.format("Login") + " " + I18n.format("Offline")));
    this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 28, 110, 20, I18n.format("Cancel")));
    this.buttonList.add(delete = new GuiButton(4, this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("Delete")));
    search = new GuiTextField(8, this.fontRenderer, this.width / 2 - 80, 14, 160, 16);
    search.setText(query);
    updateButtons();
    if (!queriedaccounts.isEmpty())
      SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias);
  }

  @Override
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.accountsgui.handleMouseInput();
  }

  @Override
  public void updateScreen() {
    this.search.updateCursorCounter();
    updateText();
    updateButtons();
    if (!(prevIndex == selectedAccountIndex)) {
      updateShownSkin();
      prevIndex = selectedAccountIndex;
    }
  }

  private void updateShownSkin() {
    if (!queriedaccounts.isEmpty())
      SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    boolean flag = search.isFocused();
    this.search.mouseClicked(mouseX, mouseY, mouseButton);
    if (!flag && search.isFocused()) {
      query = "";
      updateText();
      updateQueried();
    }
  }

  private void updateText() {
    search.setText(query);
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    Config.save();
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {
    accountsgui.drawScreen(par1, par2, par3);
    this.drawCenteredString(fontRenderer, I18n.format("Select account"), this.width / 2, 4, -1);
    if (loginfailed != null) {
      this.drawCenteredString(fontRenderer, loginfailed.getLocalizedMessage(), this.width / 2, this.height - 62, 16737380);
    }
    search.drawTextBox();
    super.drawScreen(par1, par2, par3);
    if (!queriedaccounts.isEmpty()) {
      SkinTools.javDrawSkin(8, height / 2 - 64 - 16, 64, 128);
      Tools.drawBorderedRect(width - 8 - 64, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);
      if (queriedaccounts.get(selectedAccountIndex).premium == EnumBool.TRUE)
        this.drawString(fontRenderer, I18n.format("Premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
      else if (queriedaccounts.get(selectedAccountIndex).premium == EnumBool.FALSE)
        this.drawString(fontRenderer, I18n.format("Cracked"), width - 8 - 61, height / 2 - 64 - 13, 16737380);
      this.drawString(fontRenderer, I18n.format("Times used"), width - 8 - 61, height / 2 - 64 - 15 + 12, -1);
      this.drawString(fontRenderer, String.valueOf(queriedaccounts.get(selectedAccountIndex).useCount), width - 8 - 61, height / 2 - 64 - 15 + 21, -1);
      if (queriedaccounts.get(selectedAccountIndex).useCount > 0) {
        this.drawString(fontRenderer, I18n.format("Last used"), width - 8 - 61, height / 2 - 64 - 15 + 30, -1);
        this.drawString(fontRenderer, JavaTools.getJavaCompat().getFormattedDate(), width - 8 - 61, height / 2 - 64 - 15 + 39, -1);

      }
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    if (button.enabled) {
      if (button.id == 3) {
        escape();
      } else if (button.id == 0) {
        add();
      } else if (button.id == 4) {
        delete();
      } else if (button.id == 1) {
        login(selectedAccountIndex);
      } else if (button.id == 2) {
        logino(selectedAccountIndex);
      } else if (button.id == 7) {
        edit();
      } else if (button.id == 8) {
        reloadSkins();
      } else {
        accountsgui.actionPerformed(button);
      }
    }
  }

  private void reloadSkins() {
    Config.save();
    SkinTools.cacheSkins();
    updateShownSkin();
  }

  private void escape() {
    mc.displayGuiScreen(null);
  }

  private void delete() {
    AltDatabase.getInstance().getAlts().remove(getCurrentAsEditable());
    if (selectedAccountIndex > 0)
      selectedAccountIndex--;
    updateQueried();
    updateButtons();
  }

  private void add() {
    mc.displayGuiScreen(new GuiAddAccount());
  }

  private void logino(int selected) {
    ExtendedAccountData data = queriedaccounts.get(selected);
    AltManager.getInstance().setUserOffline(data.alias);
    loginfailed = null;
    Minecraft.getMinecraft().displayGuiScreen(null);
    ExtendedAccountData current = getCurrentAsEditable();
    Objects.requireNonNull ( current ).useCount++;
    current.lastused = JavaTools.getJavaCompat().getDate();
  }

  private void login(int selected) {
    ExtendedAccountData data = queriedaccounts.get(selected);
    loginfailed = AltManager.getInstance().setUser(data.user, data.pass);
    if (loginfailed == null) {
      ExtendedAccountData current = getCurrentAsEditable();
      Objects.requireNonNull ( current ).premium = EnumBool.TRUE;
      current.useCount++;
      current.lastused = JavaTools.getJavaCompat().getDate();
    } else if (loginfailed instanceof AlreadyLoggedInException) {
      Objects.requireNonNull ( getCurrentAsEditable ( ) ).lastused = JavaTools.getJavaCompat().getDate();
    } else if (HttpTools.ping("http://minecraft.net")) {
      Objects.requireNonNull ( getCurrentAsEditable ( ) ).premium = EnumBool.FALSE;
    }
  }


  private void edit() {
    mc.displayGuiScreen(new GuiEditAccount(selectedAccountIndex));
  }

  private void updateQueried() {
    queriedaccounts = convertData();
    if (!query.equals(I18n.format("Search")) && !query.equals("")) {
      for (int i = 0; i < queriedaccounts.size(); i++) {
        if (!queriedaccounts.get(i).alias.contains(query) && ConfigValues.CASESENSITIVE) {
          queriedaccounts.remove(i);
          i--;
        } else if (!queriedaccounts.get(i).alias.toLowerCase().contains(query.toLowerCase()) && !ConfigValues.CASESENSITIVE) {
          queriedaccounts.remove(i);
          i--;
        }
      }
    }
    if (!queriedaccounts.isEmpty()) {
      while (selectedAccountIndex >= queriedaccounts.size()) {
        selectedAccountIndex--;
      }
    }
  }

  @Override
  protected void keyTyped(char character, int keyIndex) {
    if (keyIndex == Keyboard.KEY_UP && !queriedaccounts.isEmpty()) {
      if (selectedAccountIndex > 0) {
        selectedAccountIndex--;
      }
    } else if (keyIndex == Keyboard.KEY_DOWN && !queriedaccounts.isEmpty()) {
      if (selectedAccountIndex < queriedaccounts.size() - 1) {
        selectedAccountIndex++;
      }
    } else if (keyIndex == Keyboard.KEY_ESCAPE) {
      escape();
    } else if (keyIndex == Keyboard.KEY_DELETE && delete.enabled) {
      delete();
    } else if (character == '+') {
      add();
    } else if (character == '/' && edit.enabled) {
      edit();
    } else if (!search.isFocused() && keyIndex == Keyboard.KEY_R) {
      reloadSkins();
    } else if (keyIndex == Keyboard.KEY_RETURN && !search.isFocused() && (login.enabled || loginoffline.enabled)) {
      if ((Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) && loginoffline.enabled) {
        logino(selectedAccountIndex);
      } else {
        if (login.enabled)
          login(selectedAccountIndex);
      }
    } else if (keyIndex == Keyboard.KEY_BACK) {
      if (search.isFocused() && query.length() > 0) {
        query = query.substring(0, query.length() - 1);
        updateText();
        updateQueried();
      }
    } else if (keyIndex == Keyboard.KEY_F5) {
      reloadSkins();
    } else if (character != 0) {
      if (search.isFocused()) {
        if (keyIndex == Keyboard.KEY_RETURN) {
          search.setFocused(false);
          updateText();
          updateQueried();
          return;
        }
        query += character;
        updateText();
        updateQueried();
      }
    }
  }

  private ArrayList<ExtendedAccountData> convertData() {
    @SuppressWarnings("unchecked")
	ArrayList<AccountData> tmp = (ArrayList<AccountData>) AltDatabase.getInstance().getAlts().clone();
    ArrayList<ExtendedAccountData> converted = new ArrayList<>();
    int index = 0;
    for (AccountData data : tmp) {
      if (data instanceof ExtendedAccountData) {
        converted.add((ExtendedAccountData) data);
      } else {
        converted.add(new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.pass), data.alias));
        AltDatabase.getInstance().getAlts().set(index, new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.pass), data.alias));
      }
      index++;
    }
    return converted;
  }

  private ArrayList<AccountData> getAccountList() {
    return AltDatabase.getInstance().getAlts();
  }

  private ExtendedAccountData getCurrentAsEditable() {
    for (AccountData dat : getAccountList()) {
      if (dat instanceof ExtendedAccountData) {
        if (dat.equals(queriedaccounts.get(selectedAccountIndex))) {
          return (ExtendedAccountData) dat;
        }
      }
    }
    return null;
  }

  private void updateButtons() {
    login.enabled = !queriedaccounts.isEmpty() && !EncryptionTools.decode(queriedaccounts.get(selectedAccountIndex).pass).equals("");
    loginoffline.enabled = !queriedaccounts.isEmpty();
    delete.enabled = !queriedaccounts.isEmpty();
    edit.enabled = !queriedaccounts.isEmpty();
    reloadskins.enabled = !AltDatabase.getInstance().getAlts().isEmpty();
  }

  class List extends GuiSlot {
    public List(Minecraft mcIn) {
      super(mcIn, GuiAccountSelector.this.width, GuiAccountSelector.this.height, 32, GuiAccountSelector.this.height - 64, 14);
    }

    @Override
    protected int getSize() {
      return GuiAccountSelector.this.queriedaccounts.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      GuiAccountSelector.this.selectedAccountIndex = slotIndex;
      GuiAccountSelector.this.updateButtons();

      if (isDoubleClick && GuiAccountSelector.this.login.enabled) {
        GuiAccountSelector.this.login(slotIndex);
      }
    }

    @Override
    protected boolean isSelected(int slotIndex) {
      return slotIndex == GuiAccountSelector.this.selectedAccountIndex;
    }

    @Override
    protected int getContentHeight() {
      return GuiAccountSelector.this.queriedaccounts.size() * 14;
    }

    @Override
    protected void drawBackground() {
      GuiAccountSelector.this.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
      {
        ExtendedAccountData data = queriedaccounts.get(p_192637_1_);
        String s = data.alias;
        if (StringUtils.isEmpty(s)) {
          s = I18n.format("Alt") + " " + (p_192637_1_ + 1);
        }
        int color = 16777215;
        if (Minecraft.getMinecraft().getSession().getUsername().equals(data.alias)) {
          color = 0x00FF00;
        }
        GuiAccountSelector.this.drawString(GuiAccountSelector.this.fontRenderer, s, p_192637_2_ + 2, p_192637_3_ + 1, color);
      }
    }
  }
}
