package net.matthewbates.fullthrottlenei.integration;

import codechicken.nei.ItemList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import pa.api.alchemy.Element;
import pa.api.recipe.BasicRecipe;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Matthew Bates on 15/04/2016.
 */
public class AlchemyUtil
{
    private static final HashMap<String, ArrayList<ItemStack>> elementToItemMap = new HashMap<>();
    private static final Set<ItemStack> itemSet = new HashSet<>();
    private static final Map<BasicRecipe, ArrayList<ItemStack>> recommendedRecipes = new HashMap<>();
    private static final Multimap<String, ItemStack> recommendedItems = LinkedListMultimap.create();

    public static HashMap<String, Float> getElements(ItemStack stack)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.AlchemyUtil");
            Method getElementsMethod = clazz.getMethod("getElements", ItemStack.class);
            //noinspection unchecked
            return (HashMap<String, Float>) getElementsMethod.invoke(null, stack);
        } catch (Exception e)
        {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static SortedMap<String, Float> getElementsSortedByAtomicNumber(ItemStack stack)
    {
        HashMap<String, Float> elements = getElements(stack);
        SortedMap<String, Float> sortedElements = new TreeMap<>(
                (a, b) -> getElementByName(a).getNumber() < getElementByName(b).getNumber() ? -1 : 1);
        sortedElements.putAll(elements);
        return sortedElements;
    }

    public static SortedMap<String, Float> getElementsSortedBySymbol(ItemStack stack)
    {
        HashMap<String, Float> elements = getElements(stack);
        SortedMap<String, Float> sortedElements = new TreeMap<>(Comparator.comparing(a -> getElementByName(a).getSymbol()));
        sortedElements.putAll(elements);
        return sortedElements;
    }

    public static LinkedHashMap<String, Float> getElementsSortedByAmount(ItemStack stack)
    {
        final HashMap<String, Float> data = getElements(stack);
        LinkedHashMap<String, Float> sortedElements = new LinkedHashMap<>();
        Iterator iter;
        String largestName = "";
        float largestValue = 0.0F;
        if (data != null)
        {
            while (sortedElements.size() < data.size() && sortedElements.size() < 8)
            {
                iter = data.keySet().iterator();

                while (iter.hasNext())
                {
                    String name = (String) iter.next();
                    float value = data.get(name);
                    if (!sortedElements.keySet().contains(name) && value > largestValue)
                    {
                        largestValue = value;
                        largestName = name;
                    }
                }

                sortedElements.put(largestName, largestValue);
                largestValue = 0.0F;
                largestName = "";
            }
        }

        return sortedElements;
    }

    public static Element getElement(String name)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.PeriodicTable");
            Method getElementMethod = clazz.getMethod("getElement", String.class);
            return (Element) getElementMethod.invoke(null, name);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static List<BasicRecipe> getResearch()
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.RecipeBook");
            //noinspection unchecked
            return (ArrayList<BasicRecipe>) clazz.getDeclaredField("list").get(null);
        } catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Element getElementByNumber(int meta)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.PeriodicTable");
            Method getElementByNumberMethod = clazz.getMethod("getElementByNumber", int.class);
            return (Element) getElementByNumberMethod.invoke(null, meta);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Element getElementByName(String name)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.PeriodicTable");
            Method getElementByNumberMethod = clazz.getMethod("getElement", String.class);
            return (Element) getElementByNumberMethod.invoke(null, name);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, ArrayList<ItemStack>> getElementToItemMap()
    {
        if (elementToItemMap.isEmpty())
        {
            for (ItemStack stack : ItemList.items)
            {
                try
                {
                    if (stack.getItem().getClass() == Class.forName("pa.item.ItemFlask"))
                    {
                        continue;
                    }
                    HashMap<String, Float> elements = getElements(stack);
                    if (!elements.isEmpty())
                    {
                        itemSet.add(stack);
                        for (String s : elements.keySet())
                        {
                            if (elementToItemMap.containsKey(s))
                            {
                                elementToItemMap.get(s).add(stack);
                            } else
                            {
                                ArrayList<ItemStack> list = new ArrayList<>();
                                list.add(stack);
                                elementToItemMap.put(s, list);
                            }

                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return elementToItemMap;
    }

    public static Set<ItemStack> getItemsThatHaveElements()
    {
        if (itemSet.isEmpty())
            getElementToItemMap();
        return itemSet;
    }

}
