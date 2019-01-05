package com.sys1yagi.fragmentcreator.model;

import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.exception.IllegalTypeException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class EnvParser {

    public static List<FragmentCreatorModel> parse(RoundEnvironment env, Elements elementUtils) {
        ArrayList<FragmentCreatorModel> models = new ArrayList<>();
        ArrayList<Element> elements = new ArrayList<>(
                env.getElementsAnnotatedWith(FragmentCreator.class));
        for (Element element : elements) {
            FragmentCreatorModel model = new FragmentCreatorModel((TypeElement) element, elementUtils);
            models.add(model);
        }

        validateFragmentCreatorModel(models);
        return models;
    }

    public static void validateFragmentCreatorModel(List<FragmentCreatorModel> models) {
        for (FragmentCreatorModel model : models) {
            TypeMirror superClass = model.getElement().getSuperclass();
            System.out.println(model.getElement().getClass());
            BASE_CLASS_CHECK:
            while (true) {
                String fqcn = superClass.toString();
                System.out.println(fqcn  + ":" + superClass.getClass().getName());
                switch (fqcn) {
                    case "java.lang.Object":
                        throw new IllegalTypeException(
                                "@FragmentCreator can be defined only if the base class is androidx.fragment.app.Fragment. : "
                                        + superClass.toString());
                    case "android.app.Fragment":
                    case "android.support.v4.app.Fragment":
                    case "androidx.fragment.app.Fragment":
                        break BASE_CLASS_CHECK;
                }
                DeclaredType superClassType = (DeclaredType) superClass;
                TypeElement superElement = (TypeElement) superClassType.asElement();
                superClass = superElement.getSuperclass();
            }
        }
    }
}
