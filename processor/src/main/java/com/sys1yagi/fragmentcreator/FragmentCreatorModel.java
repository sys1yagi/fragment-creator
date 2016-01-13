package com.sys1yagi.fragmentcreator;

import com.sys1yagi.fragmentcreator.annotation.Args;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class FragmentCreatorModel {

    private TypeElement element;

    private String packageName;

    private String originalClassName;

    private String creatorClassName;

    private List<VariableElement> argsList = new ArrayList<>();

    public FragmentCreatorModel(TypeElement element, Elements elementUtils) {
        this.element = element;
        this.packageName = getPackageName(elementUtils, element);
        this.originalClassName = getClassName(element, packageName);
        this.creatorClassName = originalClassName.concat("Creator");
        findAnnotations(element);
    }

    private void findAnnotations(Element element) {
        for (Element enclosedElement : element.getEnclosedElements()) {
            findAnnotations(enclosedElement);

            Args args = enclosedElement.getAnnotation(Args.class);
            if (args != null) {
                this.argsList.add((VariableElement) enclosedElement);
            }
        }
    }

    private String getPackageName(Elements elementUtils, TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    public TypeElement getElement() {
        return element;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public String getCreatorClassName() {
        return creatorClassName;
    }

    public List<VariableElement> getArgsList() {
        return argsList;
    }
}
