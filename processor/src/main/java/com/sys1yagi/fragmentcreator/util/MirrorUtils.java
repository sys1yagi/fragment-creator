package com.sys1yagi.fragmentcreator.util;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class MirrorUtils {

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

}
