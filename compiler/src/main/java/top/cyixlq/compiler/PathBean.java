package top.cyixlq.compiler;

public class PathBean {
    private String moduleName; // 模块名称                   模块名称/路径，例如 app/MainActivity
    private String path;  // 路径
    private String className;  // 对应的class的全名，包含包名

    public PathBean(String moduleName, String path, String className) {
        this.moduleName = moduleName;
        this.path = path;
        this.className = className;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
