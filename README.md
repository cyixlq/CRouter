# CRouter

使劲重复造轮子——组件路由框架，参照[ActivityRouter](https://github.com/mzule/ActivityRouter)
重新造的轮子，支持Fragment实例获取，原库好像不支持。。。

## 特点

性能：几乎0反射，只在获取Fragment实例的时候使用了反射

class注入：与ARouter获取dex分包再获取类，调用类中的方法注入不同。
本库直接使用代码进行class注入，无需获取dex分包，效率明显高于ARouter。
具体可参见APT生成的代码！

fragment获取：支持

fragment获取时就填入bundle：支持

activity跳转：支持

activity获取结果跳转（startActivityForResult）：支持

activity携带参数跳转：支持（以原生Bundle形式）

跳转拦截器：暂不支持

跳转回调：暂不支持

url响应跳转：不支持

## 使用方法

在自定义的Application类的onCreate方法中调用： 

```
CRouter.get().init(this);
```

引入依赖，各个模块都需要引入，参照[app/build.gradle](./app/build.gradle)

1. 未使用组件化的情况下：

   -  在Activity类或Fragment类上添加@RouterPath注解，并将路径值填入注解中，
      例如：@RouterPath("main")
   -  使用CRouter.get().open("main");即可打开对应activity或者获取对应Fragment实例(需要强转)
   
2. 在使用组件化情况下：

   -  在主项目模块任意类上使用@Modules注解，并将所有模块名称填入注解中，
      例如：@Modules({"app","login"})
   
   - 在业务模块中任意类上使用@Module注解，并将当前模块名称填入注解中，
     例如：@Module("app")。
     注意，主项目模块中也需要此注解，填入的模块名称必须被包含在@Modules注解的值当中！
     
   - 在各个模块的Activity类或者Fragment类上添加@RouterPath注解，并将路径值填入注解中，例如：@RouterPath("main")
   
   -  使用CRouter.get().open("main");即可打开对应activity或者获取对应Fragment实例(需要强转)
   
## 后话

后续慢慢优化，理论会添加拦截器、跳转回调。url响应跳转考虑加不加。