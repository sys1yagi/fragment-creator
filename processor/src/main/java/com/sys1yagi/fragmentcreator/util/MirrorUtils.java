package com.sys1yagi.fragmentcreator.util;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class MirrorUtils {

    public final static String NOT_SUPPORT_TYPE = "NOT_SUPPORT_TYPE";

    public static AnnotationValue findAnnotationValue(AnnotationMirror annotationMirror, String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static TypeMirror findAnnotationValueAsTypeMirror(AnnotationMirror annotationMirror, String name) {
        AnnotationValue value = findAnnotationValue(annotationMirror, name);
        if (value != null) {
            return (TypeMirror) value.getValue();
        }
        return null;
    }

    public static String flattenArrayListType(ProcessingEnvironment environment, TypeMirror typeParameter) {
        String typeName = typeParameter.toString();
        switch (typeName) {
            case "java.lang.Integer":
            case "java.lang.String":
            case "java.lang.CharSequence":
            case "android.os.Parcelable":
                return typeName;
            default:
                TypeElement typeElement = (TypeElement) environment.getTypeUtils().asElement(typeParameter);
                if (typeElement != null) {
                    String flatten = flattenArrayListType(environment, typeElement.getSuperclass());
                    if (!NOT_SUPPORT_TYPE.equals(flatten)) {
                        return flatten;
                    }
                    return typeElement.getInterfaces().stream()
                            .map(mirror -> flattenArrayListType(environment, mirror))
                            .filter(f -> f != null)
                            .findFirst().orElse(NOT_SUPPORT_TYPE);
                }
        }
        return NOT_SUPPORT_TYPE;
    }

}
