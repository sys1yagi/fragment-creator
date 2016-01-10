package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.ClassName;
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
        ClassName superClassName = ClassName.get(ArgumentsReader.class);
        classBuilder.superclass(superClassName);

        List<MethodSpec> methodSpecs = new ArrayList<>();

        methodSpecs.add(createReadMethod(model));
        methodSpecs.add(createCheckRequired(model));

        classBuilder.addMethods(methodSpecs);

        TypeSpec outClass = classBuilder.build();

        JavaFile.builder(model.getPackageName(), outClass)
                .build()
                .writeTo(filer);
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
                    builder.addStatement("$T.checkRequire(fragment.$N, $S)", ClassName.get(ArgumentsReader.class), name,
                            name);
                });
        return builder.build();
    }
}
