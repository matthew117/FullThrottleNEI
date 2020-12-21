package net.matthewbates.fullthrottlenei.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.integration.AlchemyUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import pa.api.recipe.BasicRecipe;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew Bates on 30/03/2016.
 */
@SuppressWarnings("unchecked")
public class AtelierDiscoveryRecipeHandler extends TemplateRecipeHandler
{
    private static final int X_OFFSET = 3;

    private String getRecipeID()
    {
        return FullThrottleNEI.MODID + ":" + "atelierDiscovery";
    }

    @Override
    public String getRecipeName()
    {
        return "Atelier Research";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(X_OFFSET + 58, 28, 44, 8), getRecipeID()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals(getRecipeID()) && (getClass() == AtelierDiscoveryRecipeHandler.class))
        {
            List<BasicRecipe> recipes = AlchemyUtil.getResearch();
            for (BasicRecipe recipe : recipes)
            {
                try
                {
                    Field chances = recipe.getClass().getDeclaredField("chances");
                    chances.setAccessible(true);
                    ArrayList<ItemStack> research = (ArrayList<ItemStack>) chances.get(recipe);
                    for (ItemStack stack : research)
                    {
                        arecipes.add(new CachedAtelierDiscovery(recipe, stack.copy(), recipe.getDefaultItem()));
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        } else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        List<BasicRecipe> recipes = AlchemyUtil.getResearch();
        for (BasicRecipe recipe : recipes)
        {
            try
            {
                if (result.getItem().getClass() == Class.forName("pa.item.ItemVellum") && result.hasTagCompound())
                {
                    String recipeID = result.getTagCompound().getString("recipeID");
                    if (recipeID != null)
                    {
                        Field chances = recipe.getClass().getDeclaredField("chances");
                        chances.setAccessible(true);
                        ArrayList<ItemStack> research = (ArrayList<ItemStack>) chances.get(recipe);
                        for (ItemStack stack : research)
                        {
                            if (recipe.getName().equals(recipeID))
                            {
                                arecipes.add(new CachedAtelierDiscovery(recipe, stack.copy(), result));
                            }
                        }
                    }
                } else
                {
                    Field chances = recipe.getClass().getDeclaredField("chances");
                    chances.setAccessible(true);
                    ArrayList<ItemStack> research = (ArrayList<ItemStack>) chances.get(recipe);
                    for (ItemStack stack : research)
                    {
                        // not needed due to getting caches list (faster than calling getDiscoveryModifier on every item stack)
                        if (/*recipe.getDiscoveryModifier(stack) > 0.0f &&*/ NEIServerUtils.areStacksSameTypeCrafting(result, recipe.getDefaultItem()))
                        {
                            arecipes.add(new CachedAtelierDiscovery(recipe, stack.copy(), result));
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        List<BasicRecipe> recipes = AlchemyUtil.getResearch();
        for (BasicRecipe recipe : recipes)
        {
            try
            {
                Field chances = recipe.getClass().getDeclaredField("chances");
                chances.setAccessible(true);
                ArrayList<ItemStack> research = (ArrayList<ItemStack>) chances.get(recipe);
                for (ItemStack stack : research)
                {
                    if (recipe.getDiscoveryModifier(stack) > 0.0f && NEIServerUtils.areStacksSameTypeCrafting(ingredient, stack))
                    {
                        arecipes.add(new CachedAtelierDiscovery(recipe, stack.copy(), ingredient));
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getGuiTexture()
    {
        return (new ResourceLocation(FullThrottleNEI.MODID, "textures/gui/neiAtelierDiscovery.png")).toString();
    }

    @Override
    public void drawExtras(int recipe)
    {
        drawProgressBar(X_OFFSET + 60, 30, 160, 0, 40, 4, 48, 0);
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(X_OFFSET, 0, 0, 0, 160, 65);
    }

    class CachedAtelierDiscovery extends CachedRecipe
    {
        final PositionedStack research;
        PositionedStack result;

        CachedAtelierDiscovery(BasicRecipe recipe, ItemStack research, ItemStack result)
        {
            research.stackSize = 1;
            this.research = new PositionedStack(research, X_OFFSET + 36, 24);
            result.stackSize = 1;
            try
            {
                Field vellumf = Class.forName("pa.data.PAItems").getDeclaredField("vellum");
                vellumf.setAccessible(true);
                Item vellum = (Item) vellumf.get(null);
                if (vellum != null)
                {
                    ItemStack vstack = new ItemStack(vellum);
                    vstack.setTagCompound(new NBTTagCompound());
                    vstack.getTagCompound().setString("recipeID", recipe.getName());
                    this.result = new PositionedStack(vstack, X_OFFSET + 108, 24);
                }

            } catch (Exception e)
            {
                this.result = new PositionedStack(result, X_OFFSET + 108, 24);
                e.printStackTrace();
            }

        }

        public PositionedStack getResult()
        {
            return result;
        }

        public PositionedStack getIngredient()
        {
            return research;
        }
    }

}
