package com.sys1yagi.fragmentcreator.writer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;
import com.sys1yagi.fragmentcreator.model.FragmentCreatorModel;
import com.sys1yagi.fragmentcreator.util.MirrorUtils;
import com.sys1yagi.fragmentcreator.util.SerializerHolder;
import com.sys1yagi.fragmentcreator.util.StringUtils;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

public class FragmentCreatorReadGenerator {

    ProcessingEnvironment environment;

    public FragmentCreatorReadGenerator(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    public MethodSpec createReadMethod(FragmentCreatorModel model) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(model.getElement()), "fragment");

        builder.addStatement("$T args = fragment.getArguments()", ClassName.get("android.os", "Bundle"));

        List<VariableElement> argsList = model.getArgsList();
        argsList.forEach(args -> {
            if (hasDefaultValue(args)) {
                createParameterWithDefaultValueInitializeStatement(builder, args);
            } else {
                createParameterInitializeStatement(builder, args);
            }
        });

        return builder.build();
    }

    // TODO
    //    public  void getParcelableArray(java.lang.String key, android.os.Parcelable[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getParcelableArrayList(java.lang.String key, java.util.ArrayList<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void getSparseParcelableArray(java.lang.String key, android.util.SparseArray<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void getIntegerArrayList(java.lang.String key, java.util.ArrayList<java.lang.Integer> value) { throw new RuntimeException("Stub!"); }
    //    public  void getStringArrayList(java.lang.String key, java.util.ArrayList<java.lang.String> value) { throw new RuntimeException("Stub!"); }
    //    public  void getCharSequenceArrayList(java.lang.String key, java.util.ArrayList<java.lang.CharSequence> value) { throw new RuntimeException("Stub!"); }
    //    public  void getSerializable(java.lang.String key, java.io.Serializable value) { throw new RuntimeException("Stub!"); }
    //    public  void getBooleanArray(java.lang.String key, boolean[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getByteArray(java.lang.String key, byte[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getShortArray(java.lang.String key, short[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getCharArray(java.lang.String key, char[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getIntArray(java.lang.String key, int[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getLongArray(java.lang.String key, long[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getFloatArray(java.lang.String key, float[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getDoubleArray(java.lang.String key, double[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getStringArray(java.lang.String key, java.lang.String[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getCharSequenceArray(java.lang.String key, java.lang.CharSequence[] value) { throw new RuntimeException("Stub!"); }
    //    public  void getBundle(java.lang.String key, android.os.Bundle value) { throw new RuntimeException("Stub!"); }

    void createGetParameterWithDefaultValueInitializeStatement(MethodSpec.Builder builder, VariableElement param,
            TypeMirror typeMirror) {
        Args args = param.getAnnotation(Args.class);
        String key = param.getSimpleName().toString();
        switch (typeMirror.toString()) {
            case "java.lang.Object":
                throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
            case "java.lang.String":
                builder.addStatement("$T $N = args.getString($S, $S)", ClassName.get(param.asType()), key, key,
                        args.defaultString());
                break;
            case "boolean":
            case "java.lang.Boolean":
                builder.addStatement("$T $N = args.getBoolean($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultBoolean());
                break;
            case "byte":
            case "java.lang.Byte":
                builder.addStatement("$T $N = args.getByte($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultByte());
                break;
            case "char":
            case "java.lang.Character":
                builder.addStatement("$T $N = args.getChar($S, '$L')", ClassName.get(param.asType()), key, key,
                        args.defaultChar());
                break;
            case "short":
            case "java.lang.Short":
                builder.addStatement("$T $N = args.getShort($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultShort());
                break;
            case "int":
            case "java.lang.Integer":
                builder.addStatement("$T $N = args.getInt($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultInt());
                break;
            case "long":
            case "java.lang.Long":
                builder.addStatement("$T $N = args.getLong($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultLong());
                break;
            case "float":
            case "java.lang.Float":
                builder.addStatement("$T $N = args.getFloat($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultFloat());
                break;
            case "double":
            case "java.lang.Double":
                builder.addStatement("$T $N = args.getDouble($S, $L)", ClassName.get(param.asType()), key, key,
                        args.defaultDouble());
                break;
            default:
                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
                if (typeElement != null) {
                    createGetParameterWithDefaultValueInitializeStatement(builder, param, typeElement.getSuperclass());
                }
        }
    }

    String extractParameterGetMethodFormat(TypeMirror typeMirror) {
        switch (typeMirror.toString()) {
            case "java.lang.Object":
                return "";
            case "java.lang.String":
                return "args.getString($S)";
            case "boolean":
            case "java.lang.Boolean":
                return "args.getBoolean($S)";
            case "byte":
            case "java.lang.Byte":
                return "args.getByte($S)";
            case "char":
            case "java.lang.Character":
                return "args.getChar($S)";
            case "short":
            case "java.lang.Short":
                return "args.getShort($S)";
            case "int":
            case "java.lang.Integer":
                return "args.getInt($S)";
            case "long":
            case "java.lang.Long":
                return "args.getLong($S)";
            case "float":
            case "java.lang.Float":
                return "args.getFloat($S)";
            case "double":
            case "java.lang.Double":
                return "args.getDouble($S)";
            case "java.lang.CharSequence":
                return "args.getCharSequence($S)";
            case "android.os.Parcelable":
                return "args.getParcelable($S)";
            case "java.io.Serializable":
                return "($T)args.getSerializable($S)";
            default:
                String format;
                format = extractGetIfList(typeMirror);
                if (format != null) {
                    return format;
                }
                format = extractGetIfArray(typeMirror);
                if (format != null) {
                    return format;
                }
                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
                if (typeElement != null) {
                    format = extractParameterGetMethodFormat(typeElement.getSuperclass());
                    if (!"".equals(format)) {
                        return format;
                    }
                    return typeElement.getInterfaces().stream()
                            .map(this::extractParameterGetMethodFormat)
                            .filter(f -> f != null)
                            .findFirst().orElse(null);
                }
                return "";
        }
    }

    boolean isPrivateField(VariableElement field) {
        return field.getModifiers().contains(Modifier.PRIVATE);
    }

    boolean hasDefaultValue(VariableElement field) {
        Args args = field.getAnnotation(Args.class);
        if (args.require()) {
            return false;
        }
        switch (field.asType().toString()) {
            case "java.lang.String":
            case "boolean":
            case "java.lang.Boolean":
            case "byte":
            case "java.lang.Byte":
            case "char":
            case "java.lang.Character":
            case "short":
            case "java.lang.Short":
            case "int":
            case "java.lang.Integer":
            case "long":
            case "java.lang.Long":
            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
                return true;
        }
        return false;
    }

    private void createParameterWithDefaultValueInitializeStatement(MethodSpec.Builder builder, VariableElement args) {
        String key = args.getSimpleName().toString();
        createGetParameterWithDefaultValueInitializeStatement(builder, args, args.asType());
        if (isPrivateField(args)) {
            builder.addStatement("fragment.set$N($N)", StringUtils.camelCase(key), key);
        } else {
            builder.addStatement("fragment.$N = $N", key, key);
        }
    }

    String extractGetIfArray(TypeMirror typeMirror) {
        return null;
    }

    String extractGetIfList(TypeMirror typeMirror) {
        String name = typeMirror.toString();
        if (name.startsWith("java.util.List")) {
            //check type
            DeclaredType declaredType = typeMirror.accept(new SimpleTypeVisitor6<DeclaredType, String>() {
                @Override
                public DeclaredType visitDeclared(DeclaredType t, String s) {
                    return t;
                }
            }, name);
            if (declaredType == null) {
                return null;
            }
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (typeArguments.isEmpty()) {
                return null;
            }
            TypeMirror typeParameter = typeArguments.get(0);
            switch (MirrorUtils.flattenArrayListType(environment, typeParameter)) {
                case "java.lang.Integer":
                    return "args.getIntegerArrayList($S)";
                case "java.lang.String":
                    return "args.getStringArrayList($S)";
                case "java.lang.CharSequence":
                    return "args.getCharSequenceArrayList($S)";
                case "android.os.Parcelable":
                    return "args.getParcelableArrayList($S)";
            }
        }
        return null;
    }

    private void createParameterInitializeStatement(MethodSpec.Builder builder, VariableElement args) {
        String key = args.getSimpleName().toString();
        String prefix = "$T $N = ";
        TypeMirror type = args.asType();
        SerializerHolder holder = SerializerHolder.get(args);
        if (!holder.isEmpty()) {
            type = holder.to;
        }
        String extracted = extractParameterGetMethodFormat(type);

        if (extracted == null || extracted.equals("")) {
            throw new UnsupportedTypeException(args.asType().toString() + " is not supported on Bundle.");
        }

        if (extracted.contains("$T")) {
            if (!holder.isEmpty()) {
                //$T $N = new $T().deserialize(($T)args.getSerializable($S))
                extracted = "new $T().deserialize(" + extracted + ")";
                builder.addStatement(prefix + extracted,
                        ClassName.get(args.asType()),
                        key,
                        ClassName.get(holder.serializer),
                        ClassName.get(holder.to),
                        key);
            } else {
                //$T $N = ($T)args.getSerializable($S)
                builder.addStatement(prefix + extracted, ClassName.get(args.asType()), key,
                        ClassName.get(args.asType()), key);
            }
        } else {
            if (!holder.isEmpty()) {
                //$T $N = new $T().deserialize(args.getXXX($S))
                extracted = "new $T().deserialize(" + extracted + ")";
                builder.addStatement(prefix + extracted,
                        ClassName.get(args.asType()),
                        key,
                        ClassName.get(holder.serializer),
                        key);
            } else {
                //$T $N = args.getXXX($S)
                builder.addStatement(prefix + extracted, ClassName.get(args.asType()), key, key);
            }
        }

        if (args.getAnnotation(Args.class).require()) {
            builder.addStatement("FragmentCreator.checkRequire($N, $S)", key, key);
        }

        if (isPrivateField(args)) {
            builder.addStatement("fragment.set$N($N)", StringUtils.camelCase(key), key);
        } else {
            builder.addStatement("fragment.$N = $N", key, key);
        }
    }
}
