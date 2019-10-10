# CRouter

疯狂地重复造轮子——组件路由框架，参照[ActivityRouter](https://github.com/mzule/ActivityRouter)
重新造的轮子，支持Fragment实例获取，原库好像不支持。。。(PS，更新到今天这版之后可以算是大改了)

## 特点

性能：几乎0反射，只在获取Fragment实例和注入class的时候使用了反射

class注入：与ARouter获取dex分包再获取类，调用类中的方法注入不同。
本库直接使用代码进行class注入，无需获取dex分包，效率明显高于ARouter。
具体可参见APT生成的代码！

fragment获取：支持

fragment获取时就填入bundle：支持

activity跳转：支持

activity获取结果跳转（startActivityForResult）：支持

activity携带参数跳转：支持（以原生Bundle形式）

跳转拦截器：支持

跳转回调：支持

url响应跳转：不支持

androidX：不支持（要支持很简单，修改CRouter中的一行代码，如下代码所示） 
```
import android.support.v4.app.Fragment; // 将此处v4包下的Fragment改成androidx包下的就行
```

service启动获取：不支持

## 使用方法

在自定义的Application类的onCreate方法中调用： 

```
CRouter.get().init(this);
```

引入依赖，各个模块都需要引入： 
```
annotationProcessor project(path: ':compiler')
implementation project(path: ':crouter')
```
参照[app/build.gradle](./app/build.gradle)
和[login/build.gradle](./login/build.gradle)

1. 未使用组件化的情况下：

   -  在Activity类或Fragment类上添加@RouterPath注解，并将路径值填入注解中，
      例如：@RouterPath("app/MainActivity")。注意，路径必须是
      模块名/路径名的形式，并且只能有一个/符号
   -  使用CRouter.get().open("app/MainActivity");即可打开对应activity或者获取对应Fragment实例(需要强转)
   
2. 在使用组件化情况下：

   -  ~~在主项目模块任意类上使用@Modules注解，并将所有模块名称填入注解中，
      例如：@Modules({"app","login"})~~ 已经去除，不需要
   
   - ~~在业务模块中任意类上使用@Module注解，并将当前模块名称填入注解中，
     例如：@Module("app")。
     注意，主项目模块中也需要此注解，填入的模块名称必须被包含在@Modules注解的值当中！~~
     已经去除，不需要
     
   - 在各个模块的Activity类或者Fragment类上添加@RouterPath注解，并将路径值填入注解中，例如：@RouterPath("app/MainActivity")
   
   -  使用CRouter.get().open("app/MainActivity");即可打开对应activity或者获取对应Fragment实例(需要强转)
   
3. 跳转回调以及拦截器：

   - 设置全局回调，直接调用CRouter.get().setGlobalCallback()，例如： 
       ```
       CRouter.get().setGlobalCallback(new ICRouterCallback() {
            @Override
            public boolean onBeforeOpen(Context context, String path) { 
                // 打开之前，如果返回true代表拦截，false表示不拦截
                return false;
            }

            @Override
            public void onAfterOpen(Context context, String path) {
                // 打开之后
                Toast.makeText(MyApplication.this, path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotFound(Context context, String path) {
                // 跳转路径未找到
                Toast.makeText(MyApplication.this, "Not Found" + path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Context context, String path, Exception e) {
                // 跳转路径出错
            }
        });
       ```

   - 局部回调，在open方法或者openForResult方法调用时传入ICRouterCallback即可。
   当有局部回调时，优先使用局部回调，**不执行**全局回调！
   
## 后话

- 后续慢慢优化，理论会添加拦截器、跳转回调。url响应跳转考虑加不加。
- 在后面的使用情况中发现原来的通过@Modules和通过@Module注解注明模块的方法在以单个Module作为app运行时无法实现class注入，局限性巨大，已经去除换用反射的新方法！