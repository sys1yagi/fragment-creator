package com.sys1yagi.fragmentcreator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;

public class FragmentArgumentsWriter {

    private final static String NEW_INSTANCE = "newInstance";

    FragmentCreatorModel model;

    public FragmentArgumentsWriter(FragmentCreatorModel model) {
        this.model = model;
    }

    public void write(Filer filer) throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.getArgumentsClassName());
    }

    private static List<FieldSpec> createFields(FragmentCreatorModel model) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        //model.getArgsList().forEach();
//        fieldSpecs.add(FieldSpec.builder(String.class, "TABLE_NAME", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
//                .initializer("$S", tableName)
//                .build());
        return fieldSpecs;
    }
}
