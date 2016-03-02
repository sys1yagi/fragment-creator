package com.sys1yagi.fragmentcreator;

import com.google.auto.service.AutoService;

import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.exception.IllegalTypeException;
import com.sys1yagi.fragmentcreator.model.EnvParser;
import com.sys1yagi.fragmentcreator.model.FragmentCreatorModel;
import com.sys1yagi.fragmentcreator.writer.FragmentCreatorWriter;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class FragmentCreatorProcessor extends AbstractProcessor {

    private Elements elementUtils;

    private Filer filer;

    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<String>() {{
            add(FragmentCreator.class.getName());
        }};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        try {
            List<FragmentCreatorModel> models = EnvParser.parse(env, elementUtils);
            for (FragmentCreatorModel model : models) {
                {
                    FragmentCreatorWriter writer = new FragmentCreatorWriter(processingEnv, model);
                    try {
                        writer.write(filer);
                    } catch (IOException e) {
                        messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    }
                }
            }
        } catch (IllegalTypeException e) {
            error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.elementUtils = env.getElementUtils();
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    private void error(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

}
