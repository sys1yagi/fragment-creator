package com.sys1yagi.fragmentcreator.writer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;
import com.sys1yagi.fragmentcreator.model.FragmentCreatorModel;
import com.sys1yagi.fragmentcreator.util.ArrayListCreator;
import com.sys1yagi.fragmentcreator.util.MirrorUtils;
import com.sys1yagi.fragmentcreator.util.SerializerHolder;
import com.sys1yagi.fragmentcreator.util.StringUtils;

import android.os.Bundle;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

public class FragmentCreatorBuilderGenerator {

    ProcessingEnvironment environment;

    public FragmentCreatorBuilderGenerator(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    public TypeSpec create(FragmentCreatorModel model) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Builder");
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        classBuilder.addFields(createBuilderFields(model.getArgsList()));
        classBuilder.addMethod(createBuilderConstructor());
        classBuilder.addMethods(createBuilderSetterMethods(getBuilderTypeName(model), model.getArgsList()));
        classBuilder.addMethod(createBuildMethod(model.getElement(), model.getArgsList()));

        return classBuilder.build();
    }

    private TypeName getBuilderTypeName(FragmentCreatorModel model) {
        return ClassName.get(model.getPackageName() + "." + model.getCreatorClassName(), "Builder");
    }

    private List<FieldSpec> createBuilderFields(List<VariableElement> argsList) {
        return argsList.stream().map(args ->
                FieldSpec.builder(ClassName.get(args.asType()), args.getSimpleName().toString())
                        .addModifiers(Modifier.PRIVATE)
                        .build()).collect(Collectors.toList());
    }

    private MethodSpec createBuilderConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    public MethodSpec createBuilderNewBuilder(FragmentCreatorModel model, List<VariableElement> argsList) {
        TypeName builderTypeName = getBuilderTypeName(model);
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
                    return MethodSpec.methodBuilder("set" + StringUtils.camelCase(nameString))
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
            if (format.contains("ArrayList")) {
                builder.addStatement(format + " $T.create($N))", key,
                        ClassName.get(ArrayListCreator.class),
                        args.getSimpleName());
            } else {
                builder.addStatement(format + " $N)", key, args.getSimpleName());
            }
        } else {
            builder.addStatement(format + " new $T().serialize($N))", key,
                    ClassName.get(holder.serializer),
                    args.getSimpleName());
        }
    }

    // bundle
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
                String format;
                format = extractPutIfList(typeMirror);
                if (format != null) {
                    return format;
                }

                format = extractPutIfArray(typeMirror);
                if (format != null) {
                    return format;
                }

                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
                if (typeElement != null) {
                    format = extractPutMethod(typeElement.getSuperclass());
                    if (format != null) {
                        return format;
                    }
                    return typeElement.getInterfaces().stream()
                            .map(this::extractPutMethod)
                            .filter(f -> f != null)
                            .findFirst().orElse(null);
                }
                return null;
        }
    }

    String extractPutIfArray(TypeMirror typeMirror) {
        //TODO
        // arrays
        //    public  void putParcelableArray(java.lang.String key, android.os.Parcelable[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putSparseParcelableArray(java.lang.String key, android.util.SparseArray<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
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
        return null;
    }

    String extractPutIfList(TypeMirror typeMirror) {
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
                    return "args.putIntegerArrayList($S,";
                case "java.lang.String":
                    return "args.putStringArrayList($S,";
                case "java.lang.CharSequence":
                    return "args.putCharSequenceArrayList($S,";
                case "android.os.Parcelable":
                    return "args.putParcelableArrayList($S,";
            }
        }
        return null;
    }



}
