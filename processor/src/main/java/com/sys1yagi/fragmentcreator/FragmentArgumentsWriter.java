package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class FragmentArgumentsWriter {

    private final static String NEW_INSTANCE = "newInstance";

    FragmentCreatorModel model;

    public FragmentArgumentsWriter(FragmentCreatorModel model) {
        this.model = model;
    }

    public void write(Filer filer) throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.getArgumentsClassName());
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        ClassName superClassName = ClassName.get(Arguments.class);
        classBuilder.superclass(superClassName);

        List<MethodSpec> methodSpecs = new ArrayList<>();

        classBuilder.addFields(createFields(model.getArgsList()));

        methodSpecs.add(createCheckRequired(model.getArgsList()));
        methodSpecs.add(createConstructor(model));
        methodSpecs.addAll(createGetter(model.getArgsList()));

        classBuilder.addMethods(methodSpecs);

        TypeSpec outClass = classBuilder.build();

        JavaFile.builder(model.getPackageName(), outClass)
                .build()
                .writeTo(filer);
    }

    private static MethodSpec createConstructor(FragmentCreatorModel model) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.os", "Bundle"), "args")
                .addStatement("super(args)");

        List<VariableElement> argsList = model.getArgsList();
        createParameterInitializeStatement(builder, argsList);
        builder.addStatement("checkRequired()");

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
            String format = null;

            switch (param.asType().toString()) {
                case "java.lang.String":
                    format = "$N = args.getString($S)";
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    format = "$N = args.getBoolean($S)";
                    break;
                case "byte":
                case "java.lang.Byte":
                    format = "$N = args.getByte($S)";
                    break;
                case "char":
                case "java.lang.Character":
                    format = "$N = args.getChar($S)";
                    break;
                case "short":
                case "java.lang.Short":
                    format = "$N = args.getShort($S)";
                    break;
                case "int":
                case "java.lang.Integer":
                    format = "$N = args.getInt($S)";
                    break;
                case "long":
                case "java.lang.Long":
                    format = "$N = args.getLong($S)";
                    break;
                case "float":
                case "java.lang.Float":
                    format = "$N = args.getFloat($S)";
                    break;
                case "double":
                case "java.lang.Double":
                    format = "$N = args.getDouble($S)";
                    break;
                case "java.lang.CharSequence":
                    format = "$N = args.getCharSequence($S)";
                    break;
                case "android.os.Parcelable":
                    format = "$N = args.getParcelable($S)";
                    break;
                case "java.io.Serializable":
                    format = "$N = args.getSerializable($S)";
                    break;
                default:
                    //TODO extract base type
            }

            if (format == null || "".equals(format)) {
                throw new UnsupportedTypeException(param.asType().toString() + " is not supported on Bundle.");
            }

            builder.addStatement(format, key, key);
        });
    }

    private static MethodSpec createCheckRequired(List<VariableElement> args) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("checkRequired")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        args.stream()
                .filter(element -> element.getAnnotation(Args.class).require())
                .forEach(element -> {
                    String name = element.getSimpleName().toString();
                    builder.addStatement("checkRequire($N, $S)", name, name);
                });
        return builder.build();
    }

    private static List<FieldSpec> createFields(List<VariableElement> args) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        args.forEach(arg -> fieldSpecs.add(
                FieldSpec.builder(ClassName.get(arg.asType()),
                        arg.getSimpleName().toString(),
                        Modifier.PRIVATE, Modifier.FINAL)
                        .build()));
        return fieldSpecs;
    }

    private static List<MethodSpec> createGetter(List<VariableElement> args) {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        args.forEach(arg -> {
            String name = arg.getSimpleName().toString();
            methodSpecs.add(MethodSpec
                    .methodBuilder("get" + camelCase(name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(arg.asType()))
                    .addStatement("return $N", name)
                    .build()
            );
        });

        return methodSpecs;
    }

    static String camelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
