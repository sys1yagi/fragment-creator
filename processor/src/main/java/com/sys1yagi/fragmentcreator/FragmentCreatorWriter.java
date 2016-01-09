package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
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
import java.util.stream.Stream;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
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
        ClassName superClassName = ClassName.get(Creator.class);
        classBuilder.superclass(superClassName);

        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.addAll(createMethods(model.getElement(), model.getArgsList()));
        classBuilder.addMethods(methodSpecs);

        TypeSpec outClass = classBuilder.build();

        JavaFile.builder(model.getPackageName(), outClass)
                .build()
                .writeTo(filer);
    }

    @SuppressWarnings("unchecked")
    private List<MethodSpec> createMethods(TypeElement typeElement, List<VariableElement> argsList) {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        List<VariableElement> required = argsList.stream()
                .filter(element -> element.getAnnotation(Args.class).require())
                .collect(Collectors.toList());
        List<VariableElement> optional = argsList.stream()
                .filter(element -> !element.getAnnotation(Args.class).require())
                .collect(Collectors.toList());

        if (required.isEmpty()) {
            methodSpecs.add(createDefaultNewInstance(typeElement));
        }

        methodSpecs.addAll(extractParameterPattern(required, optional).stream()
                .map(params -> createNewInstance(typeElement, params)).collect(Collectors.toList()));

        return methodSpecs;
    }

    MethodSpec createDefaultNewInstance(TypeElement typeElement) {
        TypeName typeName = ClassName.get(typeElement.asType());

        return MethodSpec.methodBuilder(NEW_INSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(typeName)
                .addStatement("return new $T()", typeName)
                .build();
    }

    String buildMethodName(List<VariableElement> params) {
        //TODO Support duplication of combination
        if (params.size() == 1) {
            return NEW_INSTANCE + "With" +
                    params.stream().findFirst().map(param -> param.getSimpleName().toString()).get();
        } else {
            return NEW_INSTANCE;
        }
    }

    MethodSpec createNewInstance(TypeElement typeElement, List<VariableElement> params) {
        TypeName typeName = ClassName.get(typeElement.asType());

        MethodSpec.Builder builder = MethodSpec.methodBuilder(buildMethodName(params));

        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(typeName);
        builder.addStatement("$T fragment = new $T()", typeName, typeName);

        builder.addStatement("$T args = new $T()", ClassName.get(Bundle.class), ClassName.get(Bundle.class));

        params.forEach(param -> {
            builder.addParameter(ClassName.get(param.asType()), param.getSimpleName().toString());
            generatePutMethodCall(builder, param);
        });

        builder.addStatement("fragment.setArguments(args)");
        builder.addStatement("return fragment");

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

    void generatePutMethodCall(MethodSpec.Builder builder, VariableElement param) {
        String key = param.getSimpleName().toString();
        String format = null;
        System.out.println(param.asType().toString());

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

    List<List<VariableElement>> extractParameterPattern(List<VariableElement> required,
            List<VariableElement> optional) {
        List<List<VariableElement>> patterns = new ArrayList<>();

        if (!required.isEmpty()) {
            patterns.add(required);
        }

        int optionalCount = optional.size();
        Stream.iterate(1, i -> i + 1)
                .limit(optionalCount)
                .forEach(i -> patterns.addAll(createPattern(required, optional, i)));

        return patterns;
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
}
