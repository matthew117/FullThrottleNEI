package net.matthewbates.fullthrottlenei.gui;

import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.Utils;
import net.matthewbates.fullthrottlenei.integration.AlchemyUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by Matthew Bates on 29/04/2016.
 */

@SideOnly(Side.CLIENT)
class EditElementsGui extends ExtContainerGui
{
    private final EditElementsContainer container;
    private final GuiTextField[] txtFields = new GuiTextField[8];
    private int txtFieldInFocus = -1;

    EditElementsGui(EditElementsContainer container)
    {
        super(container);
        this.container = container;
        xSize = 256;
        ySize = 219;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                GuiTextField txtField = new GuiTextField(fontRendererObj, guiLeft + 8 + j * 55, guiTop + 28 + i * 40, 45, 16);
                txtField.setMaxStringLength(6);
                txtField.setFocused(false);
                txtFields[j + i * 4] = txtField;
            }
        }
        buttonList.clear();
        //noinspection unchecked
        buttonList.add(new GuiButton(0, guiLeft + 185, guiTop + 100, 48, 20, "Save"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture((new ResourceLocation(FullThrottleNEI.MODID, "textures/gui/editElements.png")).toString());
        GuiDraw.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        ItemStack stack = container.editedItem.getStackInSlot(0);
        if (stack != null)
        {
            Map<String, Float> elements = AlchemyUtil.getElementsSortedByAmount(stack);
            GL11.glPushMatrix();
            GL11.glScalef(.5f, .5f, .5f);

            int i = 0;
            for (Map.Entry<String, Float> entry : elements.entrySet())
            {
                int yoffset = (8 - (elements.size() % 9)) / 2;
                GuiDraw.drawString(entry.getKey() + " : " + Utils.getElementAmountFormatted(entry.getValue()), (guiLeft + 124) * 2, (guiTop + (5 * yoffset) + 90 + i * 5) * 2, 0x404040, false);
                i++;
            }
            GL11.glPopMatrix();
        }
        for (GuiTextField txtField : txtFields)
        {
            if (txtField != null)
                txtField.drawTextBox();
        }
    }

    protected void mouseClicked(int x, int y, int mouseButton)
    {
        super.mouseClicked(x, y, mouseButton);
        boolean clickedAny = false;
        for (int i = 0; i < txtFields.length; i++)
        {
            GuiTextField txtField = txtFields[i];
            if (x >= txtField.xPosition && x <= txtField.xPosition + txtField.width && y >= txtField.yPosition && y <= txtField.yPosition + txtField.height)
            {
                txtField.setFocused(true);
                txtFieldInFocus = i;
                clickedAny = true;
            } else
            {
                txtField.setFocused(false);
            }
        }
        if (!clickedAny)
            txtFieldInFocus = -1;
    }

    protected void keyTyped(char c, int keycode)
    {
        if (txtFieldInFocus >= 0 && txtFields[txtFieldInFocus].isFocused())
        {
            if ((((keycode >= 2 && keycode <= 11) || (keycode == 52 && !txtFields[txtFieldInFocus].getText().contains("."))) ||
                    (keycode == 83 && !txtFields[txtFieldInFocus].getText().contains(".")) ||
                    ((keycode >= 71 && keycode < 83) && keycode != 74 && keycode != 78) || keycode == 14) ||
                    keycode == 203 || keycode == 205)
            {
                txtFields[txtFieldInFocus].textboxKeyTyped(c, keycode);
            } else if (keycode == 28)
            {
                txtFields[txtFieldInFocus].setFocused(false);
                txtFieldInFocus = -1;
            } else if (keycode == 15)
            {
                txtFields[txtFieldInFocus].setFocused(false);
                txtFieldInFocus = (txtFieldInFocus + 1) % 8;
                txtFields[txtFieldInFocus].setFocused(true);
            }
        } else
            super.keyTyped(c, keycode);
    }


}
