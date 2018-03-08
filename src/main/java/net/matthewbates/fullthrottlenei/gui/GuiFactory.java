package net.matthewbates.fullthrottlenei.gui;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

class GuiFactory implements IModGuiFactory
{
    public void initialize(Minecraft minecraft)
    {
    }

    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return ModGuiConfig.class;
    }

    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element)
    {
        return null;
    }
}