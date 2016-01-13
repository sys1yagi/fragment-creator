package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;
import com.sys1yagi.fragmentcreator.util.Combinations;

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

    void generatePutMethodCall(MethodSpec.Builder builder, VariableElement param) {
        //TODO check require
        String key = param.getSimpleName().toString();
        String format = null;

        switch (param.asType().toString()) {
            case "java.lang.String":
                format = "args.putString($S, $N)";
                break;
            case "boolean":
            case "java.lang.Boolean":
                format = "args.putBoolean($S, $N)";
                break;
            case "byte":
            case "java.lang.Byte":
                format = "args.putByte($S, $N)";
                break;
            case "char":
            case "java.lang.Character":
                format = "args.putChar($S, $N)";
                break;
            case "short":
            case "java.lang.Short":
                format = "args.putShort($S, $N)";
                break;
            case "int":
            case "java.lang.Integer":
                format = "args.putInt($S, $N)";
                break;
            case "long":
            case "java.lang.Long":
                format = "args.putLong($S, $N)";
                break;
            case "float":
            case "java.lang.Float":
                format = "args.putFloat($S, $N)";
                break;
            case "double":
            case "java.lang.Double":
                format = "args.putDouble($S, $N)";
                break;
            case "java.lang.CharSequence":
                format = "args.putCharSequence($S, $N)";
                break;
            case "android.os.Parcelable":
                format = "args.putParcelable($S, $N)";
                break;
            case "java.io.Serializable":
                format = "args.putSerializable($S, $N)";
                break;
            default:
                //TODO extract base type
        }

        if (format == null || "".equals(format)) {
            throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
        }

        builder.addStatement(format, key, param.getSimpleName());
    }

    List<List<VariableElement>> createPattern(List<VariableElement> seed, List<VariableElement> material,
            int slotSize) {
        List<List<VariableElement>> patterns = new ArrayList<>();

        Combinations<VariableElement> combinations = new Combinations<>(
                material.toArray(new VariableElement[material.size()]), slotSize);

        while (combinations.hasNext()) {
            List<VariableElement> params = new ArrayList<>(seed);
            List<VariableElement> combination = combinations.next();
            params.addAll(combination);
            patterns.add(params);
        }
        return patterns;
    }

    private static MethodSpec createReadMethod(FragmentCreatorModel model) {
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
    private static void createParameterInitializeStatement(MethodSpec.Builder builder,
            List<VariableElement> params) {

        params.forEach(param -> {
            //TODO check require
            String key = param.getSimpleName().toString();
            String prefix = "fragment.$N = ";
            String format = prefix;

            switch (param.asType().toString()) {
                case "java.lang.String":
                    format += "args.getString($S)";
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    format += "args.getBoolean($S)";
                    break;
                case "byte":
                case "java.lang.Byte":
                    format += "args.getByte($S)";
                    break;
                case "char":
                case "java.lang.Character":
                    format += "args.getChar($S)";
                    break;
                case "short":
                case "java.lang.Short":
                    format += "args.getShort($S)";
                    break;
                case "int":
                case "java.lang.Integer":
                    format += "args.getInt($S)";
                    break;
                case "long":
                case "java.lang.Long":
                    format += "args.getLong($S)";
                    break;
                case "float":
                case "java.lang.Float":
                    format += "args.getFloat($S)";
                    break;
                case "double":
                case "java.lang.Double":
                    format += "args.getDouble($S)";
                    break;
                case "java.lang.CharSequence":
                    format += "args.getCharSequence($S)";
                    break;
                case "android.os.Parcelable":
                    format += "args.getParcelable($S)";
                    break;
                case "java.io.Serializable":
                    format += "args.getSerializable($S)";
                    break;
                default:
                    //TODO extract base type
            }

            if (prefix.equals(format)) {
                throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
            }

            builder.addStatement(format, key, key);
        });
    }

    private static MethodSpec createCheckRequired(FragmentCreatorModel model) {
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
