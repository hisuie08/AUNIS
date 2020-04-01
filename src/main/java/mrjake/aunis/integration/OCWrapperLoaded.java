package mrjake.aunis.integration;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.Visibility;
import mrjake.aunis.item.dialer.UniverseDialerWirelessEndpoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class OCWrapperLoaded implements OCWrapperInterface {
	
	public void sendSignalToReachable(Node node, Context invoker, String name, Object... params) {
		for (Node targetNode : node.reachableNodes()) {
			
			if (targetNode.host() instanceof Machine) {
				Machine machine = (Machine) targetNode.host();
				
				// If the receiving machine was a sender 
				boolean caller = machine == invoker;
				
				Object[] array = new Object[params.length + 2];
				array[0] = node.address();
				array[1] = caller;
				
				for (int i=0; i<params.length; i++)
					array[i+2] = params[i];
				
				machine.signal(name, array);
			}
		}
	}
	
	@Override
	public Node createNode(TileEntity environment, String componentName) {
		return Network.newNode((Environment) environment, Visibility.Network).withComponent(componentName, Visibility.Network).create();
	}
	
	@Override
	public void joinOrCreateNetwork(TileEntity tileEntity) {
		Network.joinOrCreateNetwork(tileEntity);
	}
	
	@Override
	public boolean isModLoaded() {
		return true;
	}
	
	@Override
	public void sendWirelessPacketPlayer(EntityPlayer player, String address, short port, Object[] data) {
		UniverseDialerWirelessEndpoint endpoint = new UniverseDialerWirelessEndpoint(player);
		Packet packet = Network.newPacket("unv-dialer-"+player.getName(), address, port, data);
		
//		Network.joinWirelessNetwork(endpoint);
		Network.sendWirelessPacket(endpoint, 10, packet);
//		Network.leaveWirelessNetwork(endpoint);
	}
}
