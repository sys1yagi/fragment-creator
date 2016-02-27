package com.sys1yagi.fragmentcreator.util;

import com.sys1yagi.fragmentcreator.annotation.Serializer;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class SerializerHolder {

    public TypeMirror to;

    public TypeMirror serializer;

    private SerializerHolder(TypeMirror to, TypeMirror serializer) {
        this.to = to;
        this.serializer = serializer;
    }

    public static SerializerHolder empty() {
        return new SerializerHolder(null, null);
    }

    public boolean isEmpty() {
        return to == null && serializer == null;
    }

    public static SerializerHolder get(VariableElement param) {
        return param.getAnnotationMirrors().stream()
                .filter(annotationMirror -> annotationMirror.getAnnotationType().toString()
                        .equals(Serializer.class.getName()))
                .map(annotationMirror ->
                        new SerializerHolder(
                                MirrorUtils.findAnnotationValueAsTypeMirror(annotationMirror, "to"),
                                MirrorUtils.findAnnotationValueAsTypeMirror(annotationMirror, "serializer")
                        )
                )
                .filter(typeMirror -> typeMirror != null)
                .findFirst().orElse(SerializerHolder.empty());
    }


}
