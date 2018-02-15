package net.matthewbates.fullthrottlenei.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.matthewbates.fullthrottlenei.gui.ExtContainer;
import net.matthewbates.fullthrottlenei.gui.GhostSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Matthew on 04/05/2016.
 */
public class DragDropPacket implements IMessage
{
    private int windowID;
    private int slot;
    private int button;
    private int modifiers;
    private ItemStack item;

    public DragDropPacket()
    {

    }

    public DragDropPacket(int windowID, int slot, int button, int modifiers, ItemStack item)
    {
        this.windowID = windowID;
        this.slot = slot;
        this.button = button;
        this.modifiers = modifiers;
        this.item = item;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowID = buf.readInt();
        slot = buf.readInt();
        button = buf.readInt();
        modifiers = buf.readInt();
        item = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowID);
        buf.writeInt(slot);
        buf.writeInt(button);
        buf.writeInt(modifiers);
        ByteBufUtils.writeItemStack(buf, item);
    }

    public static class Handler implements IMessageHandler<DragDropPacket, IMessage>
    {
        @Override
        public IMessage onMessage(DragDropPacket message, MessageContext ctx)
        {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if(player.openContainer instanceof ExtContainer && player.openContainer.windowId == message.windowID && message.slot >= 0 && message.slot < player.openContainer.inventorySlots.size())
            {
                Slot slot = (Slot)player.openContainer.inventorySlots.get(message.slot);
                if(slot instanceof GhostSlot)
                    ((ExtContainer)player.openContainer).dropItem(message.slot, message.button, message.modifiers, message.item);
            }
            return null;
        }
    }
}
