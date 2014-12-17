package mcp.mobius.wailacore.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {
	
	public CoreTransformer(){
		super();
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		
		/*
		boolean     changed     = false;
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);

    	for (MethodNode methodNode : classNode.methods){
    		
    		if (methodNode.name.equals("getNBTData")
    		&&  methodNode.desc.equals("(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;"))
    		{
            	methodNode.desc = "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;";
            	changed = true;
            	CoreDescription.log.info(String.format("Class %s implements outdated getNBTData signature. Updating signature to most recent API.", name));                	
    		}
    	}        
        
    	if (changed){
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();    		
    	}
    	*/
    	
        /*
        if (classNode.interfaces.contains("mcp/mobius/waila/api/IWailaDataProvider")){
        	
        	CoreDescription.log.info(String.format("Identified Waila plugin class : %s", name)); 
        	for (MethodNode methodNode : classNode.methods){
        		CoreDescription.log.info(String.format(" %s - %s | %s | %s | %s", name, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature));
        		
        		
        		if (methodNode.name.equals("getNBTData")
        		&&  methodNode.desc.equals("(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;"))
        		{
                	methodNode.desc = "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;";
                	CoreDescription.log.info(String.format("Class %s implements outdated getNBTData signature. Updating signature to most recent API.", name));                	
        		}
        	}
        	
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
           
            return writer.toByteArray();
        }
        */

        return bytes;
	}


}
