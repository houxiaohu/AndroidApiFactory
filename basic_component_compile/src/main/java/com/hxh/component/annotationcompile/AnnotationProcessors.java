package com.hxh.component.annotationcompile;

import com.hxh.component.annotationcompile.processor.ApiProcessors;
import com.hxh.component.annotationcompile.util.ProcessorUtils;
import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.hxh.component.basicannotation.annotation.ApiServices;

/**
 * Created by hxh on 2017/7/14.
 */
@AutoService(Processor.class)
public class AnnotationProcessors extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ProcessorUtils.getINSTANCE(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ApiServices.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        new ApiProcessors().process(roundEnv);
        return true;
    }
}
