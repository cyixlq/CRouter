package top.cyixlq.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import top.cyixlq.annotion.RouterPath;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private static final String ROUTER_MODULE = "RouterModule_";
    private static final String PACKAGE_NAME = "top.cyixlq.crouter";

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(RouterPath.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        // 开始生成路径注册代码
        return generateRouterModule(roundEnv);
    }

    private boolean generateRouterModule(RoundEnvironment roundEnv) {
        // 带有RouterPath注解的节点（其实都是类节点）
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RouterPath.class);

        // 开始收集分类module
        Map<String, List<PathBean>> map = new HashMap<>();
        for (Element element : elements) {
            final String path = element.getAnnotation(RouterPath.class).value();
            if (!isOneOfTag(path)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "路径中必须包含/符号，且只能包含一个/符号");
            }
            final String[] pathInfo = path.split("/");
            final String moduleName = pathInfo[0];
            final String className = ((TypeElement) element).getQualifiedName().toString();
            List<PathBean> pathBeans = map.get(moduleName);
            if (pathBeans == null) {
                pathBeans = new ArrayList<>();
                map.put(moduleName, pathBeans);
            }
            pathBeans.add(new PathBean(moduleName, pathInfo[1], className));
        }

        if (map.isEmpty()) return false;

        Set<String> moduleNameSet = map.keySet();
        for (String moduleName : moduleNameSet) {
            final List<PathBean> pathBeans = map.get(moduleName);
            MethodSpec.Builder inject = MethodSpec.methodBuilder("inject")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            for (PathBean pathBean : pathBeans) {
                inject.addStatement("CRouter.get().addPath($S, $S, $L.class)", pathBean.getModuleName(),
                        pathBean.getPath(), pathBean.getClassName());
            }
            TypeSpec routerModule = TypeSpec.classBuilder(ROUTER_MODULE + moduleName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ClassName.get(PACKAGE_NAME, "IRouterModule"))
                    .addMethod(inject.build())
                    .build();
            try {
                JavaFile.builder(PACKAGE_NAME, routerModule)
                        .build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.ERROR, e.getLocalizedMessage());
            }
        }
        return true;
    }

    private boolean isOneOfTag(final String path) {
        return path.indexOf('/') == path.lastIndexOf('/');
    }
}
