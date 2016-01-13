package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;

import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class FragmentCreatorWriter {

    private final static String NEW_INSTANCE = "newInstance";

    FragmentCreatorModel model;

    public FragmentCreatorWriter(FragmentCreatorModel model) {
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
        methodSpecs.add(createCheckRequired(model));
        classBuilder.addMethods(methodSpecs);

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
        classBuilder.addMethod(createBuilderNewInstance(getBuilderTypeName(model), model.getArgsList()));
        classBuilder.addMethods(createBuilderSetterMethods(getBuilderTypeName(model), model.getArgsList()));
        classBuilder.addMethod(createBuildMethod(model.getElement(), model.getArgsList()));

        return classBuilder.build();
    }

    TypeName getBuilderTypeName(FragmentCreatorModel model) {
        return ClassName.get(model.getPackageName() + "." + model.getCreatorClassName(), "Builder");
    }

    private List<FieldSpec> createBuilderFields(List<VariableElement> argsList) {
        return argsList.stream().map(args ->
                FieldSpec.builder(ClassName.get(args.asType()), args.getSimpleName().toString()).build()
        ).collect(Collectors.toList());
    }

    private MethodSpec createBuilderConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    private MethodSpec createBuilderNewInstance(TypeName builderTypeName, List<VariableElement> argsList) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(NEW_INSTANCE)
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
                return "args.putString($S, $N)";
            case "boolean":
            case "java.lang.Boolean":
                return "args.putBoolean($S, $N)";
            case "byte":
            case "java.lang.Byte":
                return "args.putByte($S, $N)";
            case "char":
            case "java.lang.Character":
                return "args.putChar($S, $N)";
            case "short":
            case "java.lang.Short":
                return "args.putShort($S, $N)";
            case "int":
            case "java.lang.Integer":
                return "args.putInt($S, $N)";
            case "long":
            case "java.lang.Long":
                return "args.putLong($S, $N)";
            case "float":
            case "java.lang.Float":
                return "args.putFloat($S, $N)";
            case "double":
            case "java.lang.Double":
                return "args.putDouble($S, $N)";
            case "java.lang.CharSequence":
                return "args.putCharSequence($S, $N)";
            case "android.os.Parcelable":
                return "args.putParcelable($S, $N)";
            case "java.io.Serializable":
                return "args.putSerializable($S, $N)";
            default:
                DeclaredType declaredType = (DeclaredType) typeMirror;
                TypeElement typeElement = (TypeElement) declaredType.asElement();
                String format = extractPutMethod(typeElement.getSuperclass());
                if (format != null) {
                    return format;
                }
                return typeElement.getInterfaces().stream()
                        .map(this::extractPutMethod)
                        .filter(f -> f != null)
                        .findFirst().get();
        }
    }

    void generatePutMethodCall(MethodSpec.Builder builder, VariableElement param) {
        String key = param.getSimpleName().toString();

        String format = extractPutMethod(param.asType());

        if (format == null) {
            throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
        }

        builder.addStatement(format, key, param.getSimpleName());
    }

    private MethodSpec createReadMethod(FragmentCreatorModel model) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(model.getElement()), "fragment");

        builder.addStatement("$T args = fragment.getArguments()", ClassName.get("android.os", "Bundle"));

        List<VariableElement> argsList = model.getArgsList();
        createParameterInitializeStatement(builder, argsList);
        builder.addStatement("checkRequired(fragment)");

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

    String extractParameterInitializeStatement(TypeMirror typeMirror) {

        System.out.println("set : " + typeMirror.toString());

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
                DeclaredType declaredType = (DeclaredType) typeMirror;
                TypeElement typeElement = (TypeElement) declaredType.asElement();
                String format = extractParameterInitializeStatement(typeElement.getSuperclass());
                if (!"".equals(format)) {
                    return format;
                }
                return typeElement.getInterfaces().stream()
                        .map(this::extractParameterInitializeStatement)
                        .filter(f -> f != null)
                        .findFirst().get();
        }
    }

    private void createParameterInitializeStatement(MethodSpec.Builder builder,
            List<VariableElement> params) {
        params.forEach(param -> {
            String key = param.getSimpleName().toString();
            String prefix = "fragment.$N = ";
            String format = prefix + extractParameterInitializeStatement(param.asType());

            if (prefix.equals(format)) {
                throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
            }

            if (format.contains("$T")) {
                builder.addStatement(format, key, ClassName.get(param.asType()), key);
            } else {
                builder.addStatement(format, key, key);
            }
        });
    }

    private MethodSpec createCheckRequired(FragmentCreatorModel model) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("checkRequired")
                .addParameter(ClassName.get(model.getElement()), "fragment")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class);
        model.getArgsList().stream()
                .filter(element -> element.getAnnotation(Args.class).require())
                .forEach(element -> {
                    String name = element.getSimpleName().toString();
                    builder.addStatement("$T.checkRequire(fragment.$N, $S)", ClassName.get(FragmentCreator.class), name,
                            name);
                });
        return builder.build();
    }
}
