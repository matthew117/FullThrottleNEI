package net.matthewbates.fullthrottlenei.nei;

import codechicken.lib.gui.GuiDraw;
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

import java.awt.*;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Matthew Bates on 17/04/2016.
 */
public class AtelierDecompositionRecipeHandler extends TemplateRecipeHandler
{
    private static final int X_OFFSET = 3;

    private String getRecipeID()
    {
        return FullThrottleNEI.MODID + ":" + "atelierDecomposition";
    }

    @Override
    public String getRecipeName()
    {
        return "Atelier Decomposition";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(X_OFFSET + 45, 62, 44, 8), getRecipeID()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals(getRecipeID()) && getClass() == AtelierDecompositionRecipeHandler.class)
        {
            Set<ItemStack> items = AlchemyUtil.getItemsThatHaveElements();
            List<ItemStack> list = new ArrayList<>(items);
            list.sort(new Utils.ItemIDComparator());
            for (ItemStack stack : list)
            {
                arecipes.add(new CachedAtelierDecomposition(AlchemyUtil.getElements(stack), "", stack));
            }
        } else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        Map<String, ArrayList<ItemStack>> recipes = AlchemyUtil.getElementToItemMap();
        try
        {
            if (result.getItem().getClass() == Class.forName("pa.item.ItemFlask") && result.getItemDamage() >= 32 && result.hasTagCompound())
            {
                Element element = AlchemyUtil.getElementByNumber(result.getItemDamage() - 32);
                if (element != null)
                {
                    if (recipes.containsKey(element.getName()))
                    {
                        for (ItemStack stack : recipes.get(element.getName()))
                        {
                            arecipes.add(new CachedAtelierDecomposition(AlchemyUtil.getElements(stack), element.getName(), stack));
                        }
                    }
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        try
        {
            if (ingredient.getItem().getClass() == Class.forName("pa.item.ItemVellum"))
            {
                return;
            }
            if (ingredient.getItem().getClass() == Class.forName("pa.item.ItemFlask"))
            {
                return;
            }

            HashMap<String, Float> elements = AlchemyUtil.getElements(ingredient);
            if (elements != null && !elements.isEmpty())
            {
                arecipes.add(new CachedAtelierDecomposition(elements, "", ingredient));
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getGuiTexture()
    {
        return (new ResourceLocation(FullThrottleNEI.MODID, "textures/gui/neiAtelierDecomposition.png")).toString();
    }

    @Override
    public void drawExtras(int recipeId)
    {
        drawProgressBar(X_OFFSET + 47, 64, 160, 0, 40, 4, 48, 0);
        CachedAtelierDecomposition cachedRecipe = (CachedAtelierDecomposition) this.arecipes.get(recipeId);
        Element mainelement = AlchemyUtil.getElementByName(cachedRecipe.text);
        GuiDraw.drawStringC(cachedRecipe.text, X_OFFSET + 2, 2, 156, 8, mainelement != null ? mainelement.getColor() : 0x404040, mainelement != null);

        NumberFormat format = NumberFormat.getInstance();
        float scale = 0.5f;
        float scaleinv = 1f / scale;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        int i = 0;
        for (Map.Entry<String, Float> element : cachedRecipe.elements.entrySet())
        {
            Element e = AlchemyUtil.getElementByName(element.getKey());
            int x = X_OFFSET + 102 + ((i % 2) * 27);
            int y = 34 + (i / 2) * 27;
            float amount = element.getValue();
            if (amount >= 100.0F)
            {
                format.setMaximumFractionDigits(1);
            } else
            {
                format.setMaximumFractionDigits(2);
            }
            String amountText = format.format(amount) + "g";
            GuiDraw.drawStringC(amountText, (int) (x * scaleinv), (int) (y * scaleinv), (int) (18 * scaleinv), (int) (9 * scaleinv), 0x404040, false);
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

    class CachedAtelierDecomposition extends TemplateRecipeHandler.CachedRecipe
    {
        final List<PositionedStack> ingredients;
        final PositionedStack result;
        final HashMap<String, Float> elements;
        final String text;

        CachedAtelierDecomposition(HashMap<String, Float> elements, String text, ItemStack result)
        {
            result.stackSize = 1;
            this.result = new PositionedStack(result, X_OFFSET + 12, 57);
            this.ingredients = new ArrayList<>();
            this.elements = elements;
            this.text = text;
            try
            {
                Class<?> clazz = Class.forName("pa.data.PAItems");

                Field flaskf = clazz.getDeclaredField("forceFlask");
                Item flask = (Item) flaskf.get(null);
                if (flask != null)
                {
                    int i = 0;
                    for (Map.Entry<String, Float> e : elements.entrySet())
                    {
                        int x = 103 + (i % 2) * 27;
                        int y = 17 + (i / 2) * 27;
                        ItemStack fstack = new ItemStack(flask);
                        fstack.setTagCompound(new NBTTagCompound());
                        fstack.setItemDamage(32 + AlchemyUtil.getElementByName(e.getKey()).getNumber());
                        fstack.getTagCompound().setFloat("amount", e.getValue());
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
