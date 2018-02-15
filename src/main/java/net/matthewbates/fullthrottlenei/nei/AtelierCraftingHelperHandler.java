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
import pa.api.recipe.BasicRecipe;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/**
 * Created by Matthew on 17/04/2016.
 */
public class AtelierCraftingHelperHandler extends TemplateRecipeHandler
{
    private static final int X_OFFSET = 3;

    private class CachedAtelierHelper extends CachedRecipe
    {
        List<PositionedStack> ingredientsPos;
        PositionedStack resultPos;
        BasicRecipe recipe;

        CachedAtelierHelper(BasicRecipe recipe, ArrayList<ItemStack> ingredients, ItemStack result)
        {
            result.stackSize = 1;
            resultPos = new PositionedStack(result, X_OFFSET + 126, 57);
            ingredientsPos = new ArrayList<PositionedStack>();
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
                    ingredientsPos.add(new PositionedStack(vstack, X_OFFSET + 79, 49));
                }
                    int i = 0;
                    for (ItemStack ingredient : ingredients)
                    {
                        int x = 8 + (i % 2) * 27;
                        int y = 17 + (i / 2) * 27;
                        ingredientsPos.add(new PositionedStack(ingredient, X_OFFSET + x, y));
                        i++;
                    }


            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        public List<PositionedStack> getIngredients()
        {
            return ingredientsPos;
        }

        public PositionedStack getResult()
        {
            return resultPos;
        }

    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        Map<BasicRecipe, ArrayList<ItemStack>> recipes = AlchemyUtil.getRecommendedRecipes();
        for (Map.Entry<BasicRecipe, ArrayList<ItemStack>> recipe : recipes.entrySet())
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(result, recipe.getKey().getDefaultItem()))
            {
                arecipes.add(new CachedAtelierHelper(recipe.getKey(), recipe.getValue(), result));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals(getRecipeID()) && getClass() == AtelierCraftingHelperHandler.class)
        {
            Map<BasicRecipe, ArrayList<ItemStack>> recipes = new TreeMap<BasicRecipe, ArrayList<ItemStack>>(new Comparator<BasicRecipe>()
            {
                @Override
                public int compare(BasicRecipe a, BasicRecipe b)
                {
                    Utils.ItemIDComparator c = new Utils.ItemIDComparator();
                    return c.compare(a.getDefaultItem(), b.getDefaultItem());
                }
            });
            recipes.putAll(AlchemyUtil.getRecommendedRecipes());
            for (Map.Entry<BasicRecipe, ArrayList<ItemStack>> recipe : recipes.entrySet())
            {
                if (!recipe.getValue().isEmpty())
                    arecipes.add(new CachedAtelierHelper(recipe.getKey(), recipe.getValue(), recipe.getKey().getDefaultItem()));
            }
        } else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    private String getRecipeID()
    {
        return FullThrottleNEI.MODID + ":" + "atelierCraftingHelper";
    }

    @Override
    public String getRecipeName()
    {
        return "Atelier for Dummies";
    }

    @Override
    public String getGuiTexture()
    {
        return (new ResourceLocation(FullThrottleNEI.MODID, "textures/gui/neiAtelierCrafting.png")).toString();
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(X_OFFSET, 0, 0, 0, 160, 130);
    }

    @Override
    public void drawExtras(int recipeId)
    {
        drawProgressBar(X_OFFSET + 67, 75, 160, 0, 40, 4, 48, 0);
    }


    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(X_OFFSET + 58, 73, 44, 8), getRecipeID()));
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }
}
