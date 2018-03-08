package net.matthewbates.fullthrottlenei.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.Utils;
import net.matthewbates.fullthrottlenei.integration.AlchemyUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import pa.api.alchemy.Element;
import pa.api.recipe.BasicRecipe;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 17/04/2016.
 */
public class AtelierCraftingRecipeHandler extends TemplateRecipeHandler
{
    private static final int X_OFFSET = 3;

    private String getRecipeID()
    {
        return FullThrottleNEI.MODID + ":" + "atelierCrafting";
    }

    @Override
    public String getRecipeName()
    {
        return "Atelier Crafting";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(X_OFFSET + 58, 73, 44, 8), getRecipeID()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals(getRecipeID()) && getClass() == AtelierCraftingRecipeHandler.class)
        {
            List<BasicRecipe> recipes = AlchemyUtil.getResearch();
            for (BasicRecipe recipe : recipes)
            {
                if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty())
                    arecipes.add(new CachedAtelierCrafting(recipe, recipe.getDefaultItem()));
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
            if (NEIServerUtils.areStacksSameTypeCrafting(result, recipe.getDefaultItem()))
            {
                arecipes.add(new CachedAtelierCrafting(recipe, result));
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
                if (ingredient.getItem().getClass() == Class.forName("pa.item.ItemVellum") && ingredient.hasTagCompound())
                {
                    String recipeID = ingredient.getTagCompound().getString("recipeID");
                    if (recipeID != null)
                    {
                        if (recipe.getName().equals(recipeID))
                        {
                            arecipes.add(new CachedAtelierCrafting(recipe, recipe.getDefaultItem()));
                        }
                    }
                }
                if (ingredient.getItem().getClass() == Class.forName("pa.item.ItemFlask") && ingredient.getItemDamage() >= 32 && ingredient.hasTagCompound())
                {
                    Element element = AlchemyUtil.getElementByNumber(ingredient.getItemDamage() - 32);
                    if (element != null)
                    {
                        if (recipe.getIngredients().containsKey(element.getName()))
                        {
                            arecipes.add(new CachedAtelierCrafting(recipe, recipe.getDefaultItem()));
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
    public String getGuiTexture()
    {
        return (new ResourceLocation(FullThrottleNEI.MODID, "textures/gui/neiAtelierCrafting.png")).toString();
    }

    @Override
    public void drawExtras(int recipeId)
    {
        drawProgressBar(X_OFFSET + 67, 75, 160, 0, 40, 4, 48, 0);
        CachedAtelierCrafting cachedRecipe = (CachedAtelierCrafting) this.arecipes.get(recipeId);

        float scale = 0.5f;
        float scaleinv = 1f / scale;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        int i = 0;
        for (Map.Entry<String, Float> element : cachedRecipe.recipe.getIngredients().entrySet())
        {
            Element e = AlchemyUtil.getElementByName(element.getKey());
            int x = X_OFFSET + 7 + (i % 2) * 27;
            int y = 34 + (i / 2) * 27;
            GuiDraw.drawStringC(Utils.getElementAmountFormatted(element.getValue()), (int) (x * scaleinv), (int) (y * scaleinv), (int) (18 * scaleinv), (int) (9 * scaleinv), 0x404040, false);
            if (e != null)
                GuiDraw.drawStringC(e.getSymbol(), (int) ((x - 9) * scaleinv), (int) ((y - 18) * scaleinv), (int) (9 * scaleinv), (int) (18 * scaleinv), 0x404040, false);
            i++;
        }
        GL11.glPopMatrix();
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(X_OFFSET, 0, 0, 0, 160, 130);
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    class CachedAtelierCrafting extends TemplateRecipeHandler.CachedRecipe
    {
        final List<PositionedStack> ingredients;
        final PositionedStack result;
        final BasicRecipe recipe;

        CachedAtelierCrafting(BasicRecipe recipe, ItemStack result)
        {
            result.stackSize = 1;
            this.result = new PositionedStack(result, X_OFFSET + 126, 57);
            this.ingredients = new ArrayList<>();
            this.recipe = recipe;
            try
            {
                Class<?> clazz = Class.forName("pa.data.PAItems");

                Field vellumf = clazz.getDeclaredField("vellum");
                Item vellum = (Item) vellumf.get(null);
                if (vellum != null)
                {
                    ItemStack vstack = new ItemStack(vellum);
                    vstack.setTagCompound(new NBTTagCompound());
                    vstack.getTagCompound().setString("recipeID", recipe.getName());
                    ingredients.add(new PositionedStack(vstack, X_OFFSET + 79, 49));
                }

                Field flaskf = clazz.getDeclaredField("forceFlask");
                Item flask = (Item) flaskf.get(null);
                if (flask != null)
                {
                    int i = 0;
                    for (Map.Entry<String, Float> element : recipe.getIngredients().entrySet())
                    {
                        int x = 8 + (i % 2) * 27;
                        int y = 17 + (i / 2) * 27;
                        ItemStack fstack = new ItemStack(flask);
                        fstack.setTagCompound(new NBTTagCompound());
                        fstack.setItemDamage(32 + AlchemyUtil.getElementByName(element.getKey()).getNumber());
                        fstack.getTagCompound().setFloat("amount", element.getValue());
                        ingredients.add(new PositionedStack(fstack, X_OFFSET + x, y));
                        i++;
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        public PositionedStack getResult()
        {
            return result;
        }

        public List<PositionedStack> getIngredients()
        {
            return ingredients;
        }

    }
}
