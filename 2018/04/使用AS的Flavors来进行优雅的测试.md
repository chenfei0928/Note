Data: 2018年4月11日

Google开发者社区曾经发了一个文章，介绍AS的Flavors功能，可以在特定文件夹存放特定代码或资源，在其对应的Flavors或BuildType中，类会被追加，资源会被替换，Manifest文件会被与主Manifest文件合并
[在 Android Studio 中利用 Product flavors 进行封闭测试](https://chinagdg.org/2016/02/%E5%9C%A8-android-studio-%E4%B8%AD%E5%88%A9%E7%94%A8-product-flavors-%E8%BF%9B%E8%A1%8C%E5%B0%81%E9%97%AD%E6%B5%8B%E8%AF%95/)

在Retrofit库中，其`retrofit2.Platform`类中实现了兼容Java8默认方法调用、Android/iOS平台将调用发送到主线程的处理的方式和工具类实例化的思路——通过运行时反射判断运行环境，并实例化对应的平台化处理类

配合ProductFlavors提供的可以在特定编译配置下追加资源、类和合并Manifest的功能，可以实现在Debug环境下添加Debug调试工具依赖、Debug调试代码、Debug设置Activity等，而不会将其无意间发布到线上APK中，这些代码甚至都不会在线上APK中存在

在主源码路径下添加类文件，添加RetrofitPlatform类似的单例初始化方法，通过反射`Class.forName()`查找Debug环境的平台配置类，如果可以找到则通过`Class.newInstance()`反射调用默认构造器实例化Debug环境配置实例（由于Debug环境平台配置类不会被打包进入Release环境的APK中，直接调用构造器会导致Release编译失败），Release下会出现反射查找类失败，直接调用默认平台配置类

这样即可轻松在Debug中使用Stetho、Traceur等调试工具而不将其自身甚至其`no-op`依赖发布到线上APK中
