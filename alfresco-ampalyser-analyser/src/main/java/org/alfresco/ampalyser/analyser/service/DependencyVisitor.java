/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class DependencyVisitor extends ClassVisitor
{
    //region Fields
    final Set<String> classes = new HashSet<>();
    final Map<String, Map<String, Integer>> groups = new HashMap<>();
    Map<String, Integer> current;
    //endregion

    public Map<String, Map<String, Integer>> getGlobals()
    {
        return groups;
    }

    public Set<String> getClasses()
    {
        return classes;
    }

    public DependencyVisitor()
    {
        super(Opcodes.ASM5);
    }

    //region ClassVisitor

    @Override
    public void visit(final int version, final int access, final String name,
        final String signature, final String superName, final String[] interfaces)
    {
        final String p = getGroupKey(name);
        current = groups.computeIfAbsent(p, k -> new HashMap<>());

        if (signature == null)
        {
            if (superName != null)
            {
                addInternalName(superName);
            }
            addInternalNames(interfaces);
        }
        else
        {
            addSignature(signature);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
        final boolean visible)
    {
        addDesc(desc);
        return new AnnotationDependencyVisitor();
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef,
        final TypePath typePath, final String desc, final boolean visible)
    {
        addDesc(desc);
        return new AnnotationDependencyVisitor();
    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
        final String desc, final String signature, final Object value)
    {
        if (signature == null)
        {
            addDesc(desc);
        }
        else
        {
            addTypeSignature(signature);
        }
        if (value instanceof Type)
        {
            addType((Type) value);
        }
        return new FieldDependencyVisitor();
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
        final String desc, final String signature, final String[] exceptions)
    {
        if (signature == null)
        {
            addMethodDesc(desc);
        }
        else
        {
            addSignature(signature);
        }
        addInternalNames(exceptions);
        return new MethodDependencyVisitor();
    }
    //endregion

    //region Visitors

    class AnnotationDependencyVisitor extends AnnotationVisitor
    {
        public AnnotationDependencyVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(final String name, final Object value)
        {
            if (value instanceof Type)
            {
                addType((Type) value);
            }
        }

        @Override
        public void visitEnum(final String name, final String desc,
            final String value)
        {
            addDesc(desc);
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String name,
            final String desc)
        {
            addDesc(desc);
            return this;
        }

        @Override
        public AnnotationVisitor visitArray(final String name)
        {
            return this;
        }
    }

    class FieldDependencyVisitor extends FieldVisitor
    {
        public FieldDependencyVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(final int typeRef,
            final TypePath typePath, final String desc,
            final boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }
    }

    class MethodDependencyVisitor extends MethodVisitor
    {
        public MethodDependencyVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault()
        {
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(final int typeRef,
            final TypePath typePath, final String desc,
            final boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(final int parameter,
            final String desc, final boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public void visitTypeInsn(final int opcode, final String type)
        {
            addType(Type.getObjectType(type));
        }

        @Override
        public void visitFieldInsn(final int opcode, final String owner,
            final String name, final String desc)
        {
            addInternalName(owner);
            addDesc(desc);
        }

        @Override
        public void visitMethodInsn(final int opcode, final String owner,
            final String name, final String desc, final boolean itf)
        {
            addInternalName(owner);
            addMethodDesc(desc);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
        {
            addMethodDesc(desc);
            addConstant(bsm);
            for (Object bsmArg : bsmArgs)
            {
                addConstant(bsmArg);
            }
        }

        @Override
        public void visitLdcInsn(final Object cst)
        {
            addConstant(cst);
        }

        @Override
        public void visitMultiANewArrayInsn(final String desc, final int dims)
        {
            addDesc(desc);
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public void visitLocalVariable(final String name, final String desc,
            final String signature, final Label start, final Label end,
            final int index)
        {
            addTypeSignature(signature);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public void visitTryCatchBlock(final Label start, final Label end,
            final Label handler, final String type)
        {
            if (type != null)
            {
                addInternalName(type);
            }
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible)
        {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }
    }

    class SignatureDependencyVisitor extends SignatureVisitor
    {
        String signatureClassName;

        public SignatureDependencyVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitClassType(final String name)
        {
            signatureClassName = name;
            addInternalName(name);
        }

        @Override
        public void visitInnerClassType(final String name)
        {
            signatureClassName = signatureClassName + "$" + name;
            addInternalName(signatureClassName);
        }
    }
    //endregion

    private String getGroupKey(String name)
    {
//        int n = name.lastIndexOf('/');
//        if (n > -1) {
//            name = name.substring(0, n);
//        }
        classes.add(name);
        return name;
    }

    private void addName(final String name)
    {
        if (name == null)
        {
            return;
        }
        final String p = getGroupKey(name);
        if (current.containsKey(p))
        {
            current.put(p, current.get(p) + 1);
        }
        else
        {
            current.put(p, 1);
        }
    }

    void addInternalName(final String name)
    {
        addType(Type.getObjectType(name));
    }

    private void addInternalNames(final String[] names)
    {
        if (names == null)
        {
            return;
        }
        for (String name : names)
        {
            addInternalName(name);
        }
    }

    void addDesc(final String desc)
    {
        addType(Type.getType(desc));
    }

    void addMethodDesc(final String desc)
    {
        addType(Type.getReturnType(desc));

        for (Type type : Type.getArgumentTypes(desc))
        {
            addType(type);
        }
    }

    void addType(final Type t)
    {
        switch (t.getSort())
        {
        case Type.ARRAY:
            addType(t.getElementType());
            break;
        case Type.OBJECT:
            addName(t.getInternalName());
            break;
        case Type.METHOD:
            addMethodDesc(t.getDescriptor());
            break;
        }
    }

    private void addSignature(final String signature)
    {
        if (signature != null)
        {
            new SignatureReader(signature).accept(new SignatureDependencyVisitor());
        }
    }

    void addTypeSignature(final String signature)
    {
        if (signature != null)
        {
            new SignatureReader(signature).acceptType(new SignatureDependencyVisitor());
        }
    }

    void addConstant(final Object cst)
    {
        if (cst instanceof Type)
        {
            addType((Type) cst);
        }
        else if (cst instanceof Handle)
        {
            final Handle h = (Handle) cst;
            addInternalName(h.getOwner());
            addMethodDesc(h.getDesc());
        }
    }
}


