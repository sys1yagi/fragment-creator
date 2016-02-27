package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;
import com.sys1yagi.fragmentcreator.util.SerializerHolder;

import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class FragmentCreatorWriter {

    FragmentCreatorModel model;

    ProcessingEnvironment environment;

    public FragmentCreatorWriter(ProcessingEnvironment environment, FragmentCreatorModel model) {
        this.environment = environment;
        this.model = model;
    }

    public void write(Filer filer) throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.getCreatorClassName());
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        ClassName superClassName = ClassName.get(FragmentCreator.class);
        classBuilder.superclass(superClassName);

        classBuilder.addType(createBuilderClass(model));

        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(createReadMethod(model));
        classBuilder.addMethods(methodSpecs);

        classBuilder.addMethod(createBuilderNewBuilder(getBuilderTypeName(model), model.getArgsList()));

        TypeSpec outClass = classBuilder.build();
        JavaFile.builder(model.getPackageName(), outClass)
                .build()
                .writeTo(filer);
    }

    private TypeSpec createBuilderClass(FragmentCreatorModel model) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Builder");
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        classBuilder.addFields(createBuilderFields(model.getArgsList()));
        classBuilder.addMethod(createBuilderConstructor());
        classBuilder.addMethods(createBuilderSetterMethods(getBuilderTypeName(model), model.getArgsList()));
        classBuilder.addMethod(createBuildMethod(model.getElement(), model.getArgsList()));

        return classBuilder.build();
    }

    TypeName getBuilderTypeName(FragmentCreatorModel model) {
        return ClassName.get(model.getPackageName() + "." + model.getCreatorClassName(), "Builder");
    }

    private List<FieldSpec> createBuilderFields(List<VariableElement> argsList) {
        return argsList.stream().map(args ->
                FieldSpec.builder(ClassName.get(args.asType()), args.getSimpleName().toString())
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        ).collect(Collectors.toList());
    }

    private MethodSpec createBuilderConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    private MethodSpec createBuilderNewBuilder(TypeName builderTypeName, List<VariableElement> argsList) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("newBuilder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderTypeName);
        builder.addStatement("Builder builder = new Builder()");
        argsList.stream()
                .filter(element -> element.getAnnotation(Args.class).require())
                .forEach(args -> {
                    Name name = args.getSimpleName();
                    builder.addParameter(ClassName.get(args.asType()), name.toString());
                    builder.addStatement("builder.$N = $N", name, name);
                });
        builder.addStatement("return builder");
        return builder.build();
    }

    private List<MethodSpec> createBuilderSetterMethods(TypeName builderTypeName, List<VariableElement> argsList) {
        return argsList.stream()
                .filter(element -> !element.getAnnotation(Args.class).require())
                .map(args -> {
                    TypeName typeName = ClassName.get(args.asType());
                    Name name = args.getSimpleName();
                    String nameString = name.toString();
                    return MethodSpec.methodBuilder("set" + camelCase(nameString))
                            .addParameter(typeName, nameString)
                            .returns(builderTypeName)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("this.$N = $N", name, name)
                            .addStatement("return this")
                            .build();
                })
                .collect(Collectors.toList());
    }

    private MethodSpec createBuildMethod(TypeElement typeElement, List<VariableElement> argsList) {
        TypeName typeName = ClassName.get(typeElement.asType());

        MethodSpec.Builder builder = MethodSpec.methodBuilder("build");

        builder.addModifiers(Modifier.PUBLIC).returns(typeName);
        builder.addStatement("$T fragment = new $T()", typeName, typeName);

        builder.addStatement("$T args = new $T()", ClassName.get(Bundle.class), ClassName.get(Bundle.class));

        argsList.forEach(param -> generatePutMethodCall(builder, param));

        builder.addStatement("fragment.setArguments(args)");
        builder.addStatement("return fragment");

        return builder.build();
    }

    static String camelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // TODO
    //    public  void putParcelableArray(java.lang.String key, android.os.Parcelable[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putParcelableArrayList(java.lang.String key, java.util.ArrayList<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void putSparseParcelableArray(java.lang.String key, android.util.SparseArray<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void putIntegerArrayList(java.lang.String key, java.util.ArrayList<java.lang.Integer> value) { throw new RuntimeException("Stub!"); }
    //    public  void putStringArrayList(java.lang.String key, java.util.ArrayList<java.lang.String> value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharSequenceArrayList(java.lang.String key, java.util.ArrayList<java.lang.CharSequence> value) { throw new RuntimeException("Stub!"); }
    //    public  void putSerializable(java.lang.String key, java.io.Serializable value) { throw new RuntimeException("Stub!"); }
    //    public  void putBooleanArray(java.lang.String key, boolean[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putByteArray(java.lang.String key, byte[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putShortArray(java.lang.String key, short[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharArray(java.lang.String key, char[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putIntArray(java.lang.String key, int[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putLongArray(java.lang.String key, long[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putFloatArray(java.lang.String key, float[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putDoubleArray(java.lang.String key, double[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putStringArray(java.lang.String key, java.lang.String[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharSequenceArray(java.lang.String key, java.lang.CharSequence[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putBundle(java.lang.String key, android.os.Bundle value) { throw new RuntimeException("Stub!"); }

    String extractPutMethod(TypeMirror typeMirror) {
        switch (typeMirror.toString()) {
            case "java.lang.Object":
                return null;
            case "java.lang.String":
                return "args.putString($S,";
            case "boolean":
            case "java.lang.Boolean":
                return "args.putBoolean($S,";
            case "byte":
            case "java.lang.Byte":
                return "args.putByte($S,";
            case "char":
            case "java.lang.Character":
                return "args.putChar($S,";
            case "short":
            case "java.lang.Short":
                return "args.putShort($S,";
            case "int":
            case "java.lang.Integer":
                return "args.putInt($S,";
            case "long":
            case "java.lang.Long":
                return "args.putLong($S,";
            case "float":
            case "java.lang.Float":
                return "args.putFloat($S,";
            case "double":
            case "java.lang.Double":
                return "args.putDouble($S,";
            case "java.lang.CharSequence":
                return "args.putCharSequence($S,";
            case "android.os.Parcelable":
                return "args.putParcelable($S,";
            case "java.io.Serializable":
                return "args.putSerializable($S,";
            default:
                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
                if (typeElement != null) {
                    String format = extractPutMethod(typeElement.getSuperclass());
                    if (format != null) {
                        return format;
                    }
                    return typeElement.getInterfaces().stream()
                            .map(this::extractPutMethod)
                            .filter(f -> f != null)
                            .findFirst().get();

                }
                return null;
        }
    }


    void generatePutMethodCall(MethodSpec.Builder builder, VariableElement args) {
        String key = args.getSimpleName().toString();

        TypeMirror type = args.asType();

        SerializerHolder holder = SerializerHolder.get(args);
        type = holder.to != null ? holder.to : type;
        String format = extractPutMethod(type);

        if (format == null) {
            throw new UnsupportedTypeException(args.asType().toString() + " is not supported on Bundle.");
        }

        if (holder.isEmpty()) {
            builder.addStatement(format + " $N)", key, args.getSimpleName());
        } else {
            builder.addStatement(format + " new $T().serialize($N))", key,
                    ClassName.get(holder.serializer),
                    args.getSimpleName());
        }
    }

    private MethodSpec createReadMethod(FragmentCreatorModel model) {
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
    //    public  void putParcelableArray(java.lang.String key, android.os.Parcelable[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putParcelableArrayList(java.lang.String key, java.util.ArrayList<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void putSparseParcelableArray(java.lang.String key, android.util.SparseArray<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
    //    public  void putIntegerArrayList(java.lang.String key, java.util.ArrayList<java.lang.Integer> value) { throw new RuntimeException("Stub!"); }
    //    public  void putStringArrayList(java.lang.String key, java.util.ArrayList<java.lang.String> value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharSequenceArrayList(java.lang.String key, java.util.ArrayList<java.lang.CharSequence> value) { throw new RuntimeException("Stub!"); }
    //    public  void putSerializable(java.lang.String key, java.io.Serializable value) { throw new RuntimeException("Stub!"); }
    //    public  void putBooleanArray(java.lang.String key, boolean[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putByteArray(java.lang.String key, byte[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putShortArray(java.lang.String key, short[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharArray(java.lang.String key, char[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putIntArray(java.lang.String key, int[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putLongArray(java.lang.String key, long[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putFloatArray(java.lang.String key, float[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putDoubleArray(java.lang.String key, double[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putStringArray(java.lang.String key, java.lang.String[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putCharSequenceArray(java.lang.String key, java.lang.CharSequence[] value) { throw new RuntimeException("Stub!"); }
    //    public  void putBundle(java.lang.String key, android.os.Bundle value) { throw new RuntimeException("Stub!"); }

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
                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
                if (typeElement != null) {
                    String format = extractParameterGetMethodFormat(typeElement.getSuperclass());
                    if (!"".equals(format)) {
                        return format;
                    }
                    return typeElement.getInterfaces().stream()
                            .map(this::extractParameterGetMethodFormat)
                            .filter(f -> f != null)
                            .findFirst().get();
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
            builder.addStatement("fragment.set$N($N)", camelCase(key), key);
        } else {
            builder.addStatement("fragment.$N = $N", key, key);
        }
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
            builder.addStatement("fragment.set$N($N)", camelCase(key), key);
        } else {
            builder.addStatement("fragment.$N = $N", key, key);
        }
    }
}
