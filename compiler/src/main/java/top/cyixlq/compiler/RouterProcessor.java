package top.cyixlq.compiler;

import com.google.auto.service.AutoService;

import org.checkerframework.checker.signature.qual.ClassGetName;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import top.cyixlq.annotion.Module;
import top.cyixlq.annotion.Modules;
import top.cyixlq.annotion.RouterPath;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Modules.class.getCanonicalName());
        types.add(Module.class.getCanonicalName());
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

        boolean hasModule = false;
        boolean hasModules = false;

        // 处理@Module注解
        final Set<? extends Element> moduleElements = roundEnv.getElementsAnnotatedWith(Module.class);
        String moduleName = "RouterModule";
        if (moduleElements != null && moduleElements.size() > 0) {
            if (moduleElements.size() > 1) {
                throw new RuntimeException("A module can only have one @Module annotation");
            }
            moduleName += "$" + moduleElements.iterator().next().getAnnotation(Module.class).value();
            hasModule = true;
        }

        // 处理@Modules注解
        String[] modulesName = null;
        final Set<? extends Element> modulesElements = roundEnv.getElementsAnnotatedWith(Modules.class);
        if (modulesElements != null && modulesElements.size() > 0) {
            modulesName = modulesElements.iterator().next().getAnnotation(Modules.class).value();
            hasModules = true;
        }

        // 开始生成Modules或者Module代码
        if (hasModules) {
            generateModulesRouterInject(modulesName);
        } else if (!hasModule) {
            generateDefaultRouterInject();
        }

        // 开始生成路径注册代码
        final Set<? extends Element> routerPathElements = roundEnv.getElementsAnnotatedWith(RouterPath.class);
        return generateRouterModule(moduleName, routerPathElements);
    }

    private void generateModulesRouterInject(String[] modulesName) {
        if (modulesName == null) return;
        try {
            JavaFileObject sourceFile = filer.createSourceFile("top.cyixlq.crouter.RouterInjector");
            Writer writer = sourceFile.openWriter();
            writer.write("package top.cyixlq.crouter;\n\n" +
                    "public final class RouterInjector {\n" +
                    "\tpublic static final void init() {\n");
            for (String name : modulesName) {
                writer.write("\t\tRouterModule$" + name + ".inject();\n");
            }
            writer.write("\t}\n");
            writer.write("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateDefaultRouterInject() {
        try {
            JavaFileObject sourceFile = filer.createSourceFile("top.cyixlq.crouter.RouterInjector");
            Writer writer = sourceFile.openWriter();
            writer.write("package top.cyixlq.crouter;\n\n" +
                    "public final class RouterInjector {\n" +
                    "\tpublic static final void init() {\n");
            writer.write("\t\tRouterModule.inject();\n");
            writer.write("\t}\n");
            writer.write("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean generateRouterModule(String moduleName, Set<? extends Element> elements) {
        try {
            JavaFileObject sourceFile = filer.createSourceFile("top.cyixlq.crouter." + moduleName);
            Writer writer = sourceFile.openWriter();
            writer.write("package top.cyixlq.crouter;\n\n" +
                    "public final class " + moduleName + "{\n" +
                    "\tpublic static final void inject() {\n");
            for (Element element : elements) {
                final String path = element.getAnnotation(RouterPath.class).value();
                final TypeElement typeElement = (TypeElement) element;
                writer.write("\t\tCRouter.get().addPath(\"" + path+ "\","
                        + typeElement.getQualifiedName().toString() + ".class);\n");
            }
            writer.write("\t}\n");
            writer.write("}");
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
