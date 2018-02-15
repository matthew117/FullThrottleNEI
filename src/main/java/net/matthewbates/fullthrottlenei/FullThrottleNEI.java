package net.matthewbates.fullthrottlenei;

import com.google.gson.Gson;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.matthewbates.fullthrottlenei.config.ConfigurationHandler;
import net.matthewbates.fullthrottlenei.gui.GuiHandler;
import net.matthewbates.fullthrottlenei.integration.AlchemyUtil;
import net.matthewbates.fullthrottlenei.json.*;
import net.matthewbates.fullthrottlenei.network.DragDropPacket;
import net.matthewbates.fullthrottlenei.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import pa.api.FTAAPI;
import pa.api.recipe.BasicRecipe;
import pa.api.recipe.FreezeStack;
import pa.api.recipe.GrindStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

@Mod(modid = FullThrottleNEI.MODID, name = FullThrottleNEI.NAME, version = FullThrottleNEI.VERSION, dependencies = "required-after:Project_Alchemy;required-after:NotEnoughItems", guiFactory = "net.matthewbates.fullthrottlenei.gui.GuiFactory")
public class FullThrottleNEI
{
    public static final String MODID = "fullthrottlenei";
    public static final String VERSION = "1.7.10-0.0.8";
    public static final String NAME = "FullThrottle NEI";
    public static JsonData jsonData;

    @Mod.Instance(FullThrottleNEI.MODID)
    public static FullThrottleNEI instance;

    @SidedProxy(clientSide = "net.matthewbates.fullthrottlenei.proxy.ClientProxy", serverSide = "net.matthewbates.fullthrottlenei.proxy.CommonProxy")
    public static IProxy proxy;

    public static SimpleNetworkWrapper packetHandler;

    //TODO: Graphite Furnace (ComputerCraft Support? storage?)
    //TODO: Cryogenic Liquefaction of Air (automated atelier)
    //TODO: Combine and Split Elements Atomically
    //TODO: Calculate elements on items by analyzing recipes

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigurationHandler.init(new File(event.getModConfigurationDirectory().getAbsolutePath(), "/FullThrottleNEI/fullthrottlenei.cfg"));
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        MinecraftForge.EVENT_BUS.register(proxy);
        packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        packetHandler.registerMessage(DragDropPacket.Handler.class, DragDropPacket.class, 0, Side.SERVER);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        if (ConfigurationHandler.allowJson)
        {
            File jsonFile = new File(event.getModConfigurationDirectory().getAbsolutePath(), "/FullThrottleNEI/fullthrottlenei.json");
            Gson gson = new Gson();
            Reader reader;
            try
            {
                if (jsonFile.exists())
                {
                    reader = new InputStreamReader(new FileInputStream(jsonFile), "UTF-8");
                    if (reader.ready())
                        jsonData = gson.fromJson(reader, JsonData.class);
                } else
                {
                    if (jsonFile.createNewFile())
                        generateDefaultJsonFile(jsonFile);
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // TODO: support for ore dictionary
        // TODO: support for numeric ranges on meta values
        // TODO: live editing of json file via commands/GUI (ProjectE?)
        if (jsonData != null)
        {
            loadJsonElements();
            loadJsonAtelierRecipes();
            loadJsonGrindingRecipes();
            loadJsonFreezingRecipes();
        }
//        AlchemyUtil.getRecommendedRecipes();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new JsonEditCommand());
    }

    private static void loadJsonElements()
    {
        if (jsonData == null || jsonData.elements == null) return;

        JsonElementProvider elementProvider = new JsonElementProvider();
        FTAAPI.addElementProvider(elementProvider);

        for (JsonElementData elementSource : jsonData.elements)
        {
            String[] outputComponents = elementSource.output.split(":");
            if (outputComponents.length < 2) continue;
            String outputModID = outputComponents[0];
            String outputItemName = outputComponents[1];
            int outputItemMeta = 0;
            if (outputComponents.length > 2)
            {
                try
                {
                    outputItemMeta = Integer.parseInt(outputComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack outputItemStack = null;

            Item item = GameRegistry.findItem(outputModID, outputItemName);
            Block block = GameRegistry.findBlock(outputModID, outputItemName);

            if (item != null)
            {
                outputItemStack = new ItemStack(item, 1, outputItemMeta);
            } else if (block != null)
            {
                outputItemStack = new ItemStack(block, 1, outputItemMeta);
            }

            if (outputItemStack == null) continue;

            HashMap<String, Float> elementInputs = new HashMap<String, Float>();
            for (JsonMultiplierItem elementItem : elementSource.inputs.items)
            {
                String[] elementItemComponents = elementItem.itemID.split(":");
                if (elementItemComponents.length < 2) continue;
                String elementModID = elementItemComponents[0];
                String elementItemName = elementItemComponents[1];
                int elementItemMeta = 0;
                if (elementItemComponents.length > 2)
                {
                    try
                    {
                        elementItemMeta = Integer.parseInt(elementItemComponents[2]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        continue;
                    }
                }
                ItemStack elementItemStack = null;

                item = GameRegistry.findItem(elementModID, elementItemName);
                block = GameRegistry.findBlock(elementModID, elementItemName);

                if (item != null)
                {
                    elementItemStack = new ItemStack(item, 1, elementItemMeta);
                } else if (block != null)
                {
                    elementItemStack = new ItemStack(block, 1, elementItemMeta);
                }

                if (elementItemStack == null) continue;

                for (HashMap.Entry<String, Float> e : AlchemyUtil.getElements(elementItemStack).entrySet())
                {
                    if (elementInputs.containsKey(e.getKey()))
                    {
                        elementInputs.put(e.getKey(), elementInputs.get(e.getKey()) + (e.getValue() * elementItem.multiplier));
                    } else
                    {
                        elementInputs.put(e.getKey(), e.getValue() * elementItem.multiplier);
                    }
                }
            }
            for (JsonElement element : elementSource.inputs.elements)
            {
                if (AlchemyUtil.getElement(element.element) != null)
                {

                    elementInputs.put(element.element, elementInputs.containsKey(element.element) ?
                            elementInputs.get(element.element) + element.amount :
                            element.amount);
                }
            }
            elementProvider.addElements(outputItemStack, elementInputs);
        }
    }

    private static void loadJsonAtelierRecipes()
    {
        if (jsonData == null || jsonData.atelier == null) return;

        for (JsonAtelierRecipeData atelierRecipe : jsonData.atelier)
        {
            String[] outputItemComponents = atelierRecipe.output.split(":");
            if (outputItemComponents.length < 2) continue;
            String outputModID = outputItemComponents[0];
            String outputItemName = outputItemComponents[1];
            int outputItemMeta = 0;
            if (outputItemComponents.length > 2)
            {
                try
                {
                    outputItemMeta = Integer.parseInt(outputItemComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack outputItemStack = null;

            Item item = GameRegistry.findItem(outputModID, outputItemName);
            Block block = GameRegistry.findBlock(outputModID, outputItemName);

            if (item != null)
            {
                outputItemStack = new ItemStack(item, 1, outputItemMeta);
            } else if (block != null)
            {
                outputItemStack = new ItemStack(block, 1, outputItemMeta);
            }

            if (outputItemStack == null) continue;

            ArrayList<ItemStack> research = new ArrayList<ItemStack>();
            for (String researchItem : atelierRecipe.research)
            {
                String[] researchItemComponents = researchItem.split(":");
                if (researchItemComponents.length < 2) continue;
                String researchModID = researchItemComponents[0];
                String researchItemName = researchItemComponents[1];
                int researchItemMeta = 0;
                if (researchItemComponents.length > 2)
                {
                    try
                    {
                        researchItemMeta = Integer.parseInt(researchItemComponents[2]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        continue;
                    }
                }
                ItemStack researchItemStack = null;

                item = GameRegistry.findItem(researchModID, researchItemName);
                block = GameRegistry.findBlock(researchModID, researchItemName);

                if (item != null)
                {
                    researchItemStack = new ItemStack(item, 1, researchItemMeta);
                } else if (block != null)
                {
                    researchItemStack = new ItemStack(block, 1, researchItemMeta);
                }

                if (researchItemStack == null) continue;

                research.add(researchItemStack);
            }

            HashMap<String, Float> elementInputs = new HashMap<String, Float>();
            for (JsonMultiplierItem elementItem : atelierRecipe.inputs.items)
            {
                String[] elementItemComponents = elementItem.itemID.split(":");
                if (elementItemComponents.length < 2) continue;
                String elementModID = elementItemComponents[0];
                String elementItemName = elementItemComponents[1];
                int elementItemMeta = 0;
                if (elementItemComponents.length > 2)
                {
                    try
                    {
                        elementItemMeta = Integer.parseInt(elementItemComponents[2]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        continue;
                    }
                }
                ItemStack elementItemStack = null;

                item = GameRegistry.findItem(elementModID, elementItemName);
                block = GameRegistry.findBlock(elementModID, elementItemName);

                if (item != null)
                {
                    elementItemStack = new ItemStack(item, 1, elementItemMeta);
                } else if (block != null)
                {
                    elementItemStack = new ItemStack(block, 1, elementItemMeta);
                }

                for (HashMap.Entry<String, Float> e : AlchemyUtil.getElements(elementItemStack).entrySet())
                {
                    if (elementInputs.containsKey(e.getKey()))
                    {
                        elementInputs.put(e.getKey(), elementInputs.get(e.getKey()) + (e.getValue() * elementItem.multiplier));
                    } else
                    {
                        elementInputs.put(e.getKey(), e.getValue() * elementItem.multiplier);
                    }
                }
            }
            for (JsonElement element : atelierRecipe.inputs.elements)
            {
                if (AlchemyUtil.getElement(element.element) != null)
                {
                    elementInputs.put(element.element, elementInputs.containsKey(element.element) ?
                            elementInputs.get(element.element) + element.amount :
                            element.amount);
                }
            }

            FTAAPI.addRecipe(new BasicRecipe(elementInputs, outputItemStack, String.format("ftanei_%s", atelierRecipe.output.replaceAll(":", "_")), research));
        }
    }

    private static void loadJsonGrindingRecipes()
    {
        if (jsonData == null || jsonData.grinding == null) return;

        for (JsonGrindingRecipeData grindingRecipe : jsonData.grinding)
        {
            if (grindingRecipe.input == null || grindingRecipe.output == null) continue;

            String[] inputItemComponents = grindingRecipe.input.split(":");
            if (inputItemComponents.length < 2) continue;
            String inputModID = inputItemComponents[0];
            String inputItemName = inputItemComponents[1];
            int inputItemMeta = 0;
            if (inputItemComponents.length > 2)
            {
                try
                {
                    inputItemMeta = Integer.parseInt(inputItemComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack inputItemStack = null;

            Item item = GameRegistry.findItem(inputModID, inputItemName);
            Block block = GameRegistry.findBlock(inputModID, inputItemName);

            if (item != null)
            {
                inputItemStack = new ItemStack(item, 1, inputItemMeta);
            } else if (block != null)
            {
                inputItemStack = new ItemStack(block, 1, inputItemMeta);
            }

            String[] outputItemComponents = grindingRecipe.output.itemID.split(":");
            if (outputItemComponents.length < 2) continue;
            String outputModID = outputItemComponents[0];
            String outputItemName = outputItemComponents[1];
            int outputItemMeta = 0;
            if (outputItemComponents.length > 2)
            {
                try
                {
                    outputItemMeta = Integer.parseInt(outputItemComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack outputItemStack = null;

            item = GameRegistry.findItem(outputModID, outputItemName);
            block = GameRegistry.findBlock(outputModID, outputItemName);

            if (item != null)
            {
                outputItemStack = new ItemStack(item, grindingRecipe.output.amount, outputItemMeta);
            } else if (block != null)
            {
                outputItemStack = new ItemStack(block, grindingRecipe.output.amount, outputItemMeta);
            }

            if (grindingRecipe.bonus == null || grindingRecipe.bonus.itemID == null)
            {
                if (inputItemStack != null && outputItemStack != null)
                    FTAAPI.addGrind(new GrindStack(inputItemStack, outputItemStack));
            } else
            {
                String[] bonusItemComponents = grindingRecipe.bonus.itemID.split(":");
                if (bonusItemComponents.length < 2) continue;
                String bonusModID = bonusItemComponents[0];
                String bonusItemName = bonusItemComponents[1];
                int bonusItemMeta = 0;
                if (bonusItemComponents.length > 2)
                {
                    try
                    {
                        bonusItemMeta = Integer.parseInt(bonusItemComponents[2]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        continue;
                    }
                }
                ItemStack bonusItemStack = null;

                item = GameRegistry.findItem(bonusModID, bonusItemName);
                block = GameRegistry.findBlock(bonusModID, bonusItemName);

                if (item != null)
                {
                    bonusItemStack = new ItemStack(item, grindingRecipe.bonus.amount, bonusItemMeta);
                } else if (block != null)
                {
                    bonusItemStack = new ItemStack(block, grindingRecipe.bonus.amount, bonusItemMeta);
                }
                if (inputItemStack != null && outputItemStack != null && bonusItemStack != null)
                    FTAAPI.addGrind(new GrindStack(inputItemStack, outputItemStack, grindingRecipe.bonus.chance, bonusItemStack));
            }
        }
    }

    private static void loadJsonFreezingRecipes()
    {
        if (jsonData == null || jsonData.freezing == null) return;

        for (JsonFreezingRecipeData freezingRecipe : jsonData.freezing)
        {
            if (freezingRecipe.input == null || freezingRecipe.output == null) continue;

            String[] inputItemComponents = freezingRecipe.input.split(":");
            if (inputItemComponents.length < 2) continue;
            String inputModID = inputItemComponents[0];
            String inputItemName = inputItemComponents[1];
            int inputItemMeta = 0;
            if (inputItemComponents.length > 2)
            {
                try
                {
                    inputItemMeta = Integer.parseInt(inputItemComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack inputItemStack = null;

            Item item = GameRegistry.findItem(inputModID, inputItemName);
            Block block = GameRegistry.findBlock(inputModID, inputItemName);

            if (item != null)
            {
                inputItemStack = new ItemStack(item, 1, inputItemMeta);
            } else if (block != null)
            {
                inputItemStack = new ItemStack(block, 1, inputItemMeta);
            }

            String[] outputItemComponents = freezingRecipe.output.itemID.split(":");
            if (outputItemComponents.length < 2) continue;
            String outputModID = outputItemComponents[0];
            String outputItemName = outputItemComponents[1];
            int outputItemMeta = 0;
            if (outputItemComponents.length > 2)
            {
                try
                {
                    outputItemMeta = Integer.parseInt(outputItemComponents[2]);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            ItemStack outputItemStack = null;

            item = GameRegistry.findItem(outputModID, outputItemName);
            block = GameRegistry.findBlock(outputModID, outputItemName);

            if (item != null)
            {
                outputItemStack = new ItemStack(item, freezingRecipe.output.amount, outputItemMeta);
            } else if (block != null)
            {
                outputItemStack = new ItemStack(block, freezingRecipe.output.amount, outputItemMeta);
            }

            if (freezingRecipe.bonus == null || freezingRecipe.bonus.itemID == null)
            {
                if (inputItemStack != null && outputItemStack != null)
                    FTAAPI.addFreeze(new FreezeStack(inputItemStack, outputItemStack, freezingRecipe.hidden));
            } else
            {
                String[] bonusItemComponents = freezingRecipe.bonus.itemID.split(":");
                if (bonusItemComponents.length < 2) continue;
                String bonusModID = bonusItemComponents[0];
                String bonusItemName = bonusItemComponents[1];
                int bonusItemMeta = 0;
                if (bonusItemComponents.length > 2)
                {
                    try
                    {
                        bonusItemMeta = Integer.parseInt(bonusItemComponents[2]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        continue;
                    }
                }
                ItemStack bonusItemStack = null;

                item = GameRegistry.findItem(bonusModID, bonusItemName);
                block = GameRegistry.findBlock(bonusModID, bonusItemName);

                if (item != null)
                {
                    bonusItemStack = new ItemStack(item, freezingRecipe.bonus.amount, bonusItemMeta);
                } else if (block != null)
                {
                    bonusItemStack = new ItemStack(block, freezingRecipe.bonus.amount, bonusItemMeta);
                }
                if (inputItemStack != null && outputItemStack != null && bonusItemStack != null)
                    FTAAPI.addFreeze(new FreezeStack(inputItemStack, outputItemStack, freezingRecipe.bonus.chance, bonusItemStack, freezingRecipe.hidden));
            }
        }
    }

    private static void generateDefaultJsonFile(File jsonFile)
    {
        try
        {
            FileWriter writer = new FileWriter(jsonFile);
            BufferedWriter buffer = new BufferedWriter(writer);
            buffer.write("{\"elements\": [],\"atelier\": [],\"grinding\": [],\"freezing\": []}");
            buffer.close();
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
