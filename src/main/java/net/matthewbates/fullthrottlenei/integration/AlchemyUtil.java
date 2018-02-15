package net.matthewbates.fullthrottlenei.integration;

import codechicken.nei.ItemList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.matthewbates.fullthrottlenei.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.oredict.OreDictionary;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import pa.api.alchemy.Element;
import pa.api.recipe.BasicRecipe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Matthew on 15/04/2016.
 */
public class AlchemyUtil
{
    public static HashMap<String, Float> getElements(ItemStack stack)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.alchemy.AlchemyUtil");
            Method getElementsMethod = clazz.getMethod("getElements", ItemStack.class);
            return (HashMap<String, Float>) getElementsMethod.invoke(null, stack);
        } catch (Exception e)
        {
            e.printStackTrace();
            return new HashMap<String, Float>();
        }
    }

    public static SortedMap<String, Float> getElementsSortedByAtomicNumber(ItemStack stack)
    {
        HashMap<String, Float> elements = getElements(stack);
        SortedMap<String, Float> sortedElements = new TreeMap<String, Float>(new Comparator<String>()
        {
            @Override
            public int compare(String a, String b)
            {
                return getElementByName(a).getNumber() < getElementByName(b).getNumber() ? -1 : 1;
            }
        });
        sortedElements.putAll(elements);
        return sortedElements;
    }

    public static SortedMap<String, Float> getElementsSortedBySymbol(ItemStack stack)
    {
        HashMap<String, Float> elements = getElements(stack);
        SortedMap<String, Float> sortedElements = new TreeMap<String, Float>(new Comparator<String>()
        {
            @Override
            public int compare(String a, String b)
            {
                return getElementByName(a).getSymbol().compareTo(getElementByName(b).getSymbol());
            }
        });
        sortedElements.putAll(elements);
        return sortedElements;
    }

    public static LinkedHashMap<String, Float> getElementsSortedByAmount(ItemStack stack)
    {
        final HashMap<String, Float> data = getElements(stack);
        LinkedHashMap<String, Float> sortedElements = new LinkedHashMap<String, Float>();
        Iterator iter;
        String largestName = "";
        float largestValue = 0.0F;
        if(data != null) {
            while(sortedElements.size() < data.size() && sortedElements.size() < 8) {
                iter = data.keySet().iterator();

                while(iter.hasNext()) {
                    String name = (String)iter.next();
                    float value = data.get(name);
                    if(!sortedElements.keySet().contains(name) && value > largestValue) {
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
            return (ArrayList<BasicRecipe>) clazz.getDeclaredField("list").get(null);
        } catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<BasicRecipe>();
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

    private static final HashMap<String, ArrayList<ItemStack>> elementToItemMap = new HashMap<String, ArrayList<ItemStack>>();
    private static final Set<ItemStack> itemSet = new HashSet<ItemStack>();

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
                                ArrayList<ItemStack> list = new ArrayList<ItemStack>();
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
//            for (Map.Entry<String, ArrayList<ItemStack>> k : elementToItemMap.entrySet())
//            {
//                Collections.sort(k.getValue(), new ElementAmountComparator(k.getKey()));
//            }
        }
        return elementToItemMap;
    }

    static class ElementAmountComparator implements Comparator<ItemStack>
    {
        String s;

        public ElementAmountComparator(String s)
        {
            this.s = s;
        }

        @Override
        public int compare(ItemStack a, ItemStack b)
        {
            return getElements(a).get(s).compareTo(getElements(b).get(s));
        }
    }

    public static Set<ItemStack> getItemsThatHaveElements()
    {
        if (itemSet.isEmpty())
            getElementToItemMap();
        return itemSet;
    }

    private static final Map<BasicRecipe, ArrayList<ItemStack>> recommendedRecipes = new HashMap<BasicRecipe, ArrayList<ItemStack>>();
    private static final Multimap<String, ItemStack> recommendedItems = LinkedListMultimap.create();

    public static Map<BasicRecipe, ArrayList<ItemStack>> getRecommendedRecipes()
    {
        if (recommendedRecipes.isEmpty())
        {
            if (recommendedItems.isEmpty())
            {
                recommendedItems.put("Oxygen", new ItemStack(Blocks.log));
                recommendedItems.put("Carbon", new ItemStack(Blocks.log));
                recommendedItems.put("Carbon", new ItemStack(Items.coal));
                recommendedItems.put("Hydrogen", new ItemStack(Blocks.log));
                recommendedItems.put("Nitrogen", new ItemStack(Blocks.log));
                recommendedItems.put("Silicon", new ItemStack(Blocks.sand));
                recommendedItems.put("Silicon", new ItemStack(Blocks.cobblestone));
//                recommendedItems.put("Silicon", new ItemStack(Blocks.obsidian));
                recommendedItems.put("Lithium", new ItemStack(Items.redstone));
                recommendedItems.put("Iron", new ItemStack(Items.iron_ingot));
                recommendedItems.put("Gold", new ItemStack(Items.gold_ingot));

                List<ItemStack> ingotCopper = OreDictionary.getOres("ingotCopper");
                if (!ingotCopper.isEmpty())
                {
                    recommendedItems.put("Copper", ingotCopper.get(0));
                }
                recommendedItems.put("Copper", new ItemStack(Blocks.sand));

                List<ItemStack> ingotAluminium = OreDictionary.getOres("ingotAluminium");
                if (!ingotAluminium.isEmpty())
                {
                    recommendedItems.put("Aluminium", ingotAluminium.get(0));
                }
                recommendedItems.put("Aluminium", new ItemStack(Items.clay_ball));

                List<ItemStack> ingotTin = OreDictionary.getOres("ingotTin");
                if (!ingotTin.isEmpty())
                {
                    recommendedItems.put("Tin", ingotTin.get(0));
                }
                recommendedItems.put("Tin", new ItemStack(Items.redstone));

                List<ItemStack> ingotSilver = OreDictionary.getOres("ingotSilver");
                if (!ingotSilver.isEmpty())
                {
                    recommendedItems.put("Silver", ingotSilver.get(0));
                }

                List<ItemStack> ingotNickel = OreDictionary.getOres("ingotNickel");
                if (!ingotNickel.isEmpty())
                {
                    recommendedItems.put("Nickel", ingotNickel.get(0));
                }

                List<ItemStack> ingotLead = OreDictionary.getOres("ingotLead");
                if (!ingotLead.isEmpty())
                {
                    recommendedItems.put("Lead", ingotLead.get(0));
                }
                recommendedItems.put("Lead", new ItemStack(Items.ender_pearl));

                List<ItemStack> ingotPlatinum = OreDictionary.getOres("ingotPlatinum");
                if (!ingotPlatinum.isEmpty())
                {
                    recommendedItems.put("Platinum", ingotPlatinum.get(0));
                }

                List<ItemStack> dustSulfur = OreDictionary.getOres("dustSulfur");
                if (!dustSulfur.isEmpty())
                {
                    recommendedItems.put("Sulfur", dustSulfur.get(0));
                }

                recommendedItems.put("Sulfur", new ItemStack(Items.coal));
                recommendedItems.put("Sulfur", new ItemStack(Blocks.netherrack));
                recommendedItems.put("Calcium", new ItemStack(Items.bone));
                recommendedItems.put("Calcium", new ItemStack(Items.egg));
                recommendedItems.put("Phosphorus", new ItemStack(Blocks.log));
                recommendedItems.put("Sulfur", new ItemStack(Blocks.netherrack));
                try
                {
                    Class<?> clazz = Class.forName("pa.data.PAItems");
                    Field resourcef = clazz.getDeclaredField("resource");
                    Item resource = (Item) resourcef.get(null);
                    if (resource != null)
                    {
                        recommendedItems.put("Phosphorus", new ItemStack(resource, 1, 2));
                        recommendedItems.put("Potassium", new ItemStack(resource, 1, 2));
                        recommendedItems.put("Sulfur", new ItemStack(resource, 1, 2));
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                recommendedItems.put("Potassium", new ItemStack(Items.gunpowder));
                recommendedItems.put("Potassium", new ItemStack(Items.carrot));
                recommendedItems.put("Sodium", new ItemStack(Blocks.gravel));
                recommendedItems.put("Arsenic", new ItemStack(Items.rotten_flesh));
                recommendedItems.put("Beryllium", new ItemStack(Items.quartz));
                recommendedItems.put("Manganese", new ItemStack(Blocks.gravel));
                recommendedItems.put("Magnesium", new ItemStack(Blocks.sapling));
                recommendedItems.put("Mercury", new ItemStack(Items.fish));
                recommendedItems.put("Neon", new ItemStack(Items.glowstone_dust));
            }
            int k = 0;
            for (BasicRecipe recipe : getResearch())
            {
                System.out.println(k++);
                final Map<String, Float> elements = new HashMap<String, Float>(recipe.getIngredients());
                if (!elements.isEmpty())
                {
                    HashMap<String, ItemStack> itemSet = new HashMap<String, ItemStack>();
                    for (String element : elements.keySet())
                    {
                        List<ItemStack> items = new ArrayList<ItemStack>(recommendedItems.get(element));
                        for (ItemStack item : items)
                        {
                            itemSet.put(item.getUnlocalizedName(), item);
                        }
                    }

                    //============================================================================

                    ICombinatoricsVector<ItemStack> cvector = Factory.createVector(itemSet.values());
                    Generator<ItemStack> cgenerator = Factory.createSubSetGenerator(cvector);
                    ArrayList<ItemStack> currentBest = new ArrayList<ItemStack>();
                    float currentEffeciency = Float.MAX_VALUE;
                    for (ICombinatoricsVector<ItemStack> itemSubset : cgenerator)
                    {
                        if (itemSubset.getSize() > 0 && itemSubset.getSize() <= 5)
                        {
                            Generator<ItemStack> g = Factory.createPermutationGenerator(itemSubset);
                            for (ICombinatoricsVector<ItemStack> perm : g)
                            {
                                //TODO permutations of items choices/tally up on going stack sizes
                                Tuple recipeScore = getRecipeEfficiency(recipe, perm.getVector()/*itemSubset.getVector()*/);
                                if (recipeScore == null) continue;
                                Float efficiency = (Float) recipeScore.getFirst();

                                if (efficiency < currentEffeciency)
                                {
                                    currentEffeciency = efficiency;
                                    currentBest = new ArrayList<ItemStack>((Collection<? extends ItemStack>) recipeScore.getSecond());
                                }
                                if (currentEffeciency < 0.00000001) break;
                            }
                            if (currentEffeciency < 0.00000001) break;
                        }
                    }

                    //============================================================================

                    recommendedRecipes.put(recipe, currentBest);
                }
            }
        }
        return recommendedRecipes;
    }

    private static Tuple getRecipeEfficiency(BasicRecipe recipe, List<ItemStack> potentialRecipe)
    {
        if (potentialRecipe.isEmpty() || recipe.getIngredients().isEmpty()) return null;

        // Add up weight of elements on potential recipe
        HashMap<String, Float> ingredients = getElementsOnItemList(potentialRecipe);
        if (ingredients.equals(recipe.getIngredients()))
            return new Tuple(0.0f, potentialRecipe); // Perfect matching, so end early

        // Proposed recipe must include all of the required elements
        if (!ingredients.keySet().containsAll(recipe.getIngredients().keySet())) return null;
        float sum = 0;
        List<ItemStack> potentialRecipeCopy = new ArrayList<ItemStack>();
        for (ItemStack s : potentialRecipe)
            potentialRecipeCopy.add(s.copy());

//        HashMap<String, Float> currentElements = new HashMap<String, Float>();
        while (!canCraftRecipe(recipe, getElementsOnItemList(potentialRecipeCopy)))
        {
            for (ItemStack s : potentialRecipeCopy)
            {
//                currentElements.putAll(getElements(s));
                while (!isStackLargeEnough(recipe, getElementsOnItemList(Arrays.asList(s))))
                {
                    s.stackSize++;
                }
//                if (canCraftRecipe(recipe, currentElements)) break;
            }
        }
        return new Tuple(Utils.sum(getElementsOnItemList(potentialRecipeCopy).values()) - Utils.sum(recipe.getIngredients().values()), potentialRecipeCopy);
    }



    public static HashMap<String, Float> getElementsOnItemList(List<ItemStack> items)
    {
        HashMap<String, Float> ingredients = new HashMap<String, Float>();
        for (ItemStack item : items)
        {
            HashMap<String, Float> elements = getElements(item);
            if (elements != null)
            {
                for (Map.Entry<String, Float> element : elements.entrySet())
                {
                    if (ingredients.containsKey(element.getKey()))
                    {
                        ingredients.put(element.getKey(), ingredients.get(element.getKey()) + element.getValue() * item.stackSize);
                    } else
                    {
                        ingredients.put(element.getKey(), element.getValue() * item.stackSize);
                    }
                }
            }
        }
        return ingredients;
    }

    public static boolean canCraftRecipe(BasicRecipe recipe, HashMap<String, Float> ingredients)
    {
        for (Map.Entry<String, Float> entry : recipe.getIngredients().entrySet())
        {
            if (ingredients.get(entry.getKey()) < entry.getValue()) return false;
        }
        return true;
    }

    private static boolean isStackLargeEnough(BasicRecipe recipe, Map<String, Float> ingredients)
    {
        for (Map.Entry<String, Float> entry : ingredients.entrySet())
        {
            if (recipe.getIngredients().containsKey(entry.getKey()) && entry.getValue() < recipe.getIngredients().get(entry.getKey())) return false;
        }
        return true;
    }
}
