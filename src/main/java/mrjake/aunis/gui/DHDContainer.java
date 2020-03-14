package mrjake.aunis.gui;

import mrjake.aunis.item.AunisItems;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.DHDTile;
import mrjake.aunis.tileentity.util.ReactorStateEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DHDContainer extends Container {

	public Slot slotCrystal;
	public FluidTank tankNaquadah;
	public DHDTile dhdTile;

	private BlockPos pos;
	private int tankLastAmount;
	private ReactorStateEnum lastReactorState;
	
	public DHDContainer(IInventory playerInventory, World world, int x, int y, int z) {	
		pos = new BlockPos(x, y, z);
		dhdTile = (DHDTile) world.getTileEntity(pos);
		IItemHandler itemHandler = dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		
		slotCrystal = new SlotItemHandler(itemHandler, 0, 80, 35);
		addSlotToContainer(slotCrystal);
		
		tankNaquadah = (FluidTank) dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		
		for (int row=0; row<2; row++) {
			for (int col=0; col<2; col++) {				
				addSlotToContainer(new SlotItemHandler(itemHandler, row*2+col+1, 9+18*col, 18+18*row));
			}
		}
				
		addPlayerSlots(playerInventory);
	}
	
	private void addPlayerSlots(IInventory playerInventory) {		
		addSlotRow(playerInventory, 0, 8, 18, 144);
				
		for (int row=0; row<3; row++) {
			addSlotRow(playerInventory, 9*(row+1), 8, 18, 86 + 18*row);
		}
	}
	
	private void addSlotRow(IInventory playerInventory, int firstIndex, int xStart, int xOffset, int y) {		
		for (int col=0; col<9; col++) {
			addSlotToContainer(new Slot(playerInventory, firstIndex+col, 18*col + 8, y));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack stack = getSlot(index).getStack();
		
		// Transfering from DHD to player's inventory
        if (index < 5) {
        	if (!mergeItemStack(stack, 5, inventorySlots.size(), false)) {
        		return ItemStack.EMPTY;
        	}
        }
        
		// Transfering from player's inventory to DHD
        else {
        	if (stack.getItem() == AunisItems.crystalControlDhd) {
        		if (!slotCrystal.getHasStack()) {
        			slotCrystal.putStack(stack.copy());
        			stack.shrink(1);
        		}
        	}
        	
        	else if (DHDTile.SUPPORTED_UPGRADES.contains(stack.getItem())) {
        		for (int i=1; i<5; i++) {
        			if (!getSlot(i).getHasStack()) {
        				ItemStack stack1 = stack.copy();
        				stack1.setCount(1);
        				
        				putStackInSlot(i, stack1);
        				stack.shrink(1);
        				
        				return stack;
        			}
        		}
        	}
        	
        	return ItemStack.EMPTY;
        }
        
        return stack;
    }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
				
		if (tankLastAmount != tankNaquadah.getFluidAmount() || lastReactorState != dhdTile.getReactorState()) {			
			for (IContainerListener listener : listeners) {
				if (listener instanceof EntityPlayerMP) {
					AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_STATE, new DHDContainerGuiState(tankNaquadah.getFluidAmount(), tankNaquadah.getCapacity(), dhdTile.getReactorState())), (EntityPlayerMP) listener);
				}
			}
			
			tankLastAmount = tankNaquadah.getFluidAmount();
			lastReactorState = dhdTile.getReactorState();
		}
	}
}
