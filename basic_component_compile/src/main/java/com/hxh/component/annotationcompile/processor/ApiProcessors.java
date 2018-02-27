package com.hxh.component.annotationcompile.processor;


import com.hxh.component.annotationcompile.IProcessor;
import com.hxh.component.annotationcompile.util.ProcessorUtils;
import com.hxh.component.annotationcompile.util.TypeNameUtil;
import com.hxh.component.basicannotation.annotation.ApiServices;
import com.hxh.component.basicannotation.annotation.ApiServicesOtherPath;
import com.hxh.component.basicannotation.annotation.UseMRecycleView;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * Created by hxh on 2017/7/14.
 */
public class ApiProcessors implements IProcessor {

    private ArrayList<String> methodNames = new ArrayList<>();

    @Override
    public boolean process(RoundEnvironment roundEnv) {
        String className = "ApiFactory";
        String DATA_NAME = "NetResultBean"; //作为一个分支
        //构建一个类
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addJavadoc("@这是由APT自动生成,请注意暂不支持类似如下格式的返回值：" +
                        "\n xxx<Item.Item.Item> 泛型类型为1层以上的" +
                        "\n 推荐用法如下:" +
                        "\n 列表：NetResultBean<你的bean>" +
                        "\n 普通：随意，但是泛型层数不要为1层以上" +
                        "\n 原因： APi无法支持得到泛型实体，导致无法完成这一项工作");

        //其实这个字段没什么用，只是为了导包..
        FieldSpec.Builder convertFactoryFieldBuild = FieldSpec
                .builder(TypeNameUtil.convertFactory,"convert",Modifier.PRIVATE)
                .addJavadoc("@这个变量只是用于导包");

        FieldSpec.Builder appFieldBuild = FieldSpec
                .builder(TypeNameUtil.my_CoreLib,"app",Modifier.PRIVATE)
                .addJavadoc("@这个变量只是用于导包");

        classBuilder.addField(appFieldBuild.build());
        classBuilder.addField(convertFactoryFieldBuild.build());

        String pageageName = "";
        try
        {
            //构建方法
            //先得到应用了这个注解的所有类
            for (TypeElement typeElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ApiServices.class))) {
                //获取主要地址的Tag
                String mainPathTag = typeElement.getAnnotation(ApiServices.class).value();
                //                if(null == mainPathTag || "".equals(mainPathTag))
                //                {
                //                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.ERROR,"ApiServices must have pathTag!!");
                //                    return false;
                //                }
                pageageName =  ProcessorUtils.getINSTANCE().getmEleUtils().getPackageOf(typeElement).getQualifiedName().toString();
                //得到这个类中的所有API方法
                //  ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"正在生成代码，生成路径"+pageageName);
                //  ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"服务器地址"+mainPathTag);

                getAllMethodName(typeElement);
                // ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"方法检查完毕");
                //循环方法
                for (Element element : typeElement.getEnclosedElements()) {
                    //得到其中一个方法
                    ExecutableElement methodEle = (ExecutableElement) element;
                    //看方法上是否有注解，代表它的baseurl是不同的
                    ApiServicesOtherPath other = methodEle.getAnnotation(ApiServicesOtherPath.class);
                    String otherPathTag = other!=null?other.value():null;


                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"检查方法，并开始生成"+methodEle.getSimpleName());

                    //开始生成
                    MethodSpec.Builder methodBuilder = MethodSpec
                            .methodBuilder(getMethodName(methodEle))
                            .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                            .addJavadoc("@这是由APT自动生成");



                    TypeName returntypename = TypeName.get(methodEle.getReturnType());
                    methodBuilder.returns(returntypename);

                    //region 生成rx.iomain 中间的泛型
                    String rx_return_param =  returntypename.toString().replace("Observable","");
                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"方法返回值类型"+rx_return_param);
                    if(rx_return_param.charAt(rx_return_param.length()-1)=='>' && rx_return_param.charAt(rx_return_param.length()-2)=='>')
                    {
                        //说明是个泛型
                        rx_return_param = rx_return_param.substring(rx_return_param.indexOf("<"),rx_return_param.lastIndexOf(">"));
                        rx_return_param = rx_return_param.replace(pageageName+".","");
                        //双重泛型
                        if(rx_return_param.contains(">"))
                        {
                            String str1 = rx_return_param.substring(rx_return_param.lastIndexOf(".",rx_return_param.lastIndexOf("<"))+1,rx_return_param.lastIndexOf("<"));
                            String str2 = rx_return_param.substring(rx_return_param.lastIndexOf("<"),rx_return_param.lastIndexOf(">"));
                            str2 = str2.substring(str2.lastIndexOf(".")+1,str2.length());
                            rx_return_param = str1+"<"+str2+">";
                        }else
                        {
                            rx_return_param = rx_return_param.substring(rx_return_param.lastIndexOf("."),rx_return_param.length()).replace(".","");
                        }
                    }else
                    {
                        rx_return_param = rx_return_param.substring(rx_return_param.lastIndexOf(".")+1,rx_return_param.length()).replace(">","");
                    }
                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"返回值处理完后为"+rx_return_param);
                    //endregion

                    boolean isString = rx_return_param.equals("String")?true:false;

                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"进入动态生成参数阶段");

                    //region 开始进行分支判断，如果是以 NetResultBean 生成参数
                    //先判断有没有应用 @UseMRecycleView 这个注解
                    if(null != methodEle.getAnnotation(UseMRecycleView.class) || returntypename.toString().contains(DATA_NAME))
                    {
                        //兼容老版本
                        if(returntypename.toString().contains(DATA_NAME))
                        {
                            methodBuilder.returns(TypeNameUtil.rx_observable);
                            //让其参数为HashMap
                            methodBuilder
                                    .addParameter(ParameterizedTypeName.get(HashMap.class,String.class,Object.class),"param");

                            StringBuffer getParamCodeStr = new StringBuffer();

                            for (int i = 0; i < methodEle.getParameters().size(); i++)
                            {
                                VariableElement variableElement = methodEle.getParameters().get(i);
                                if(i+1 == methodEle.getParameters().size())
                                {

                                    getParamCodeStr.append("(("+ TypeName.get(variableElement.asType()).toString()+")param.get(\""+variableElement.getSimpleName()+"\"))");
                                }else
                                {
                                    getParamCodeStr.append("(("+ TypeName.get(variableElement.asType()).toString()+")param.get(\""+variableElement.getSimpleName()+"\")),");
                                }
                            }
                            //region 生成方法体
                            if("".equals(getParamCodeStr.toString()))
                            {
                                methodBuilder
                                        .addStatement(
                                                getmethodStament1(getParamCodeStr.toString(),isString,mainPathTag,otherPathTag),
                                                TypeNameUtil.baseapi,
                                                typeElement.getSimpleName().toString(),
                                                methodEle.getSimpleName().toString(),
                                                TypeNameUtil.rxutils
                                        );
                            }else
                            {
                                methodBuilder.addStatement(getmethodStament1(getParamCodeStr.toString(),isString,mainPathTag,otherPathTag),
                                        TypeNameUtil.baseapi,
                                        typeElement.getSimpleName().toString(),
                                        methodEle.getSimpleName().toString(),
                                        getParamCodeStr.toString(),
                                        TypeNameUtil.rxutils
                                );
                            }
                            //endregion
                        }
                    }
                    else
                    {
                        methodBuilder.returns(returntypename);
                        //正常的增删改方法，只要返回值不是 以NetResultbean包起来的
                        String paramString = "";
                        for (VariableElement ep : methodEle.getParameters())
                        {
                            methodBuilder.addParameter(TypeName.get(ep.asType()),ep.getSimpleName().toString());
                            paramString+= ep.getSimpleName().toString()+",";
                        }

                        //region 生成方法体
                        if(null == paramString || "".equals(paramString))
                        {
                            methodBuilder.addStatement(getmethodStament(paramString,isString,mainPathTag,otherPathTag),
                                    TypeNameUtil.baseapi,
                                    typeElement.getSimpleName().toString(),
                                    methodEle.getSimpleName().toString(),
                                    TypeNameUtil.rxutils,
                                    rx_return_param);
                        }else
                        {
                            methodBuilder.addStatement(getmethodStament(paramString,isString,mainPathTag,otherPathTag),
                                    TypeNameUtil.baseapi,
                                    typeElement.getSimpleName().toString(),
                                    methodEle.getSimpleName().toString(),
                                    paramString.substring(0,paramString.length()-1),
                                    TypeNameUtil.rxutils,
                                    rx_return_param);
                        }


                        //endregion
                    }
                    ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"检查方法，方法生成完毕"+methodEle.getSimpleName());
                    classBuilder.addMethod(methodBuilder.build());
                }


                JavaFile javaFile = JavaFile
                        .builder(pageageName,classBuilder
                                .build())
                        .build();
                javaFile.writeTo(ProcessorUtils.getINSTANCE().getmFiler());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 生成方法
     * @param param
     * @param isString
     * @param mainpathTag
     * @param otherPathTag
     * @return
     */
    private String getmethodStament(String param,boolean isString,String mainpathTag,String otherPathTag)
    {
        ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"正在生成参数+"+param+"返回值是否是String"+isString);
        ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"正在生成服务器地址+"+mainpathTag+"是否是其他地址"+otherPathTag);
        StringBuffer methodBuilder = new StringBuffer();
        if(null == param || "".equals(param))
        {

            methodBuilder.append("return $T\n.getInstance()" );
            if(isString)   methodBuilder.append("\n.setConvertFactory(ConverterFactory.SIMPLEString)");
            if(null != otherPathTag && !"".equals(otherPathTag))
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+otherPathTag+"\"),$L.class)");
            }else
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+mainpathTag+"\"),$L.class)");
            }
            // methodBuilder.append("\n.createServices($L.class)");
            methodBuilder.append("\n.");
            methodBuilder.append("$L()");
            methodBuilder.append("\n.");
            methodBuilder.append("compose($T.<$L>io_main())");

        }else
        {
            methodBuilder.append("return $T\n.getInstance()" );
            if(isString)   methodBuilder.append("\n.setConvertFactory(ConverterFactory.SIMPLEString)");
            if(null != otherPathTag && !"".equals(otherPathTag))
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+otherPathTag+"\"),$L.class)");
            }else
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+mainpathTag+"\"),$L.class)");
            }
            methodBuilder.append("\n.");
            methodBuilder.append("$L($L)");
            methodBuilder.append("\n.");
            methodBuilder.append("compose($T.<$L>io_main())");
        }
        ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"拼接结果"+methodBuilder.toString());
        return methodBuilder.toString();
    }

    private String getmethodStament1(String param,boolean isString,String mainpathTag,String otherPathTag)
    {
        ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"正在生成参数+"+param+"返回值是否是String"+isString);
        StringBuffer methodBuilder = new StringBuffer();
        if(null == param || "".equals(param))
        {

            methodBuilder.append("return $T\n.getInstance()" );
            if(isString)   methodBuilder.append("\n.setConvertFactory(ConverterFactory.SIMPLEString)");
            if(null != otherPathTag && !"".equals(otherPathTag))
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+otherPathTag+"\"),$L.class)");
            }else
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+mainpathTag+"\"),$L.class)");
            }

            methodBuilder.append("\n.");
            methodBuilder.append("$L()");
            methodBuilder.append("\n.");
            methodBuilder.append("compose($T.io_main())");

        }else
        {
            methodBuilder.append("return $T\n.getInstance()" );
            if(isString)   methodBuilder.append("\n.setConvertFactory(ConverterFactory.SIMPLEString)");
            if(null != otherPathTag && !"".equals(otherPathTag))
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+otherPathTag+"\"),$L.class)");
            }else
            {
                methodBuilder.append("\n.createServices(CoreLib.getInstance().getBaseUrl(\""+mainpathTag+"\"),$L.class)");
            }
            methodBuilder.append("\n.");
            methodBuilder.append("$L($L)");
            methodBuilder.append("\n.");
            methodBuilder.append("compose($T.io_main())");
        }
        ProcessorUtils.getINSTANCE().getmMessager().printMessage(Diagnostic.Kind.NOTE,"拼接结果"+methodBuilder.toString());
        return methodBuilder.toString();
    }

    private String getMethodName(ExecutableElement ele)
    {
        int count = 0;
        String methodName = ele.getSimpleName().toString();
        String paraname ="";
        for (VariableElement item : ele.getParameters()) {
            paraname += "_"+item.getSimpleName().toString();
        }
        String methodN1 = methodName+paraname;

        if(methodNames.contains(methodName)|| methodNames.contains(methodN1))
        {
            for (String name : methodNames) {
                if(name.equals(methodName))++count;
            }
            if(count>1)
            {
                return methodN1;
            }
            return methodName;
        }else
        {
            return methodName;
        }
    }


    private void getAllMethodName(TypeElement type)
    {
        for (Element element : type.getEnclosedElements())
        {
            methodNames.add(((ExecutableElement) element).getSimpleName().toString());
        }
    }


}