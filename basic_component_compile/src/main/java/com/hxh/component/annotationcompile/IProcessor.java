package com.hxh.component.annotationcompile;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by hxh on 2017/7/14.
 */
public interface IProcessor {
     boolean process(RoundEnvironment roundEnv);
}
