package net.matthewbates.fullthrottlenei.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

/**
 * Created by Matthew Bates on 27/04/2016.
 */
public class FixGrindingBonusTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String tname, byte[] bytes)
    {
        if (name.contains("pa.tile.TileAlchemyFurnace"))
        {
            ClassReader classReader = new ClassReader(bytes);
            final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor cl = new ClassVisitor(Opcodes.ASM4, classWriter)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    if (name.equals("getResult"))
                    {
                        return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions))
                        {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
                            {
                                if (owner.equals("pa/api/recipe/IGrindRecipe") && name.equals("getChance"))
                                {
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                    mv.visitInsn(Opcodes.SWAP);
                                } else
                                {
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                }
                            }
                        };
                    } else
                    {
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                }
            };
            classReader.accept(cl, 0);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
