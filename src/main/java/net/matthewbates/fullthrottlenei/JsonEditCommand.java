package net.matthewbates.fullthrottlenei;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 30/04/2016.
 */
public class JsonEditCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "fullthrottlenei";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/fullthrottlenei [elements|atelier|grinding|freezing]";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        List<String> options = new ArrayList<String>();
        options.add("elements");
        options.add("atelier");
        options.add("grinding");
        options.add("freezing");
        return options;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayer)
        {
            ((EntityPlayer)sender).openGui(FullThrottleNEI.instance, 0, sender.getEntityWorld(), 0, 0, 0);
        }
    }
}
