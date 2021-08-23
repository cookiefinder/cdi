#### FushengDI

1.   ##### What's DI

     DI 意为依赖注入，由系统或编辑器自动注入实现类，开发者只需声明所需接口便可使用，这种从以前开发者手动new实例转变为框架自动注入实现称为反转控制，而DI则是一个更加表义的名称，可以理解为一种设计模式，将我们这种自动注入功能标准化

2.   ##### What's FushengDI

     FushengDI是基于DI模式实现的一个DI框架，支持通过构造器进行依赖的注入，轻量易用

3.   ##### How to import FushengDI

     只需在项目引入`com.tw.fushengdi-core`便可使用

4.   ##### FushengDI startup

     程序会以入口类所在路径依次扫描，将所有类进行初始化并注入 `FushengContainer`，我们可通过 `FushengContainer` 获取相应的实例

     例：

     Given 一个入口类 `com.tw.Main`，一个 `com.tw.test.Test` 类

     When 启动程序后

     Then 可通过 `FushengContainer` 获取 `Test` 实例

5.   ##### Constructor-Based DI

     针对有依赖的类，可通过构造器组合依赖，并在构造器上指定 `@Inject`，此时该类依赖会在获取时自动注入

     例：

     Given 一个类 `com.tw.model.A`，一个类 `com.tw.model.B`，A 通过构造器依赖 B

     When 在构造器上标明`@Inject`并启动程序

     Then 通过 A 获取到的 B 实例和通过 FushengContainer 获取到的 B。实例是相同的

     ```java
     public class A {
         private B b;
         @Inject
         public A(B b) {
             this.b = b;
         }
         
         public B getB() {
             return b;
         }
     }
     ```

     ```java
     void test() {
         FushengContainer container = FushengContainer.startup();
         A a = container.getBean(A.class);
         B b = container.getBean(B.class);
         asset a.getB() == b
     }
     ```

6.   ##### Specify dependency type

     当我们程序中若有多个相同类型的类，我们可通过 `@Named` 给每个类指定一个唯一的名字，然后在注入的地方使用 `@Named` 指定相应的类

     例：

     Given 一个类 `com.tw.model.A1`，一个类 `com.tw.model.A2`

     When 通过 `@Named` 分别指定类别名为 `A1`, `A2` 并启动程序

     Then 通过 FushengContainer 和别名 `A1` 获取 A1 实例

     ```java
     interface A {
         void say();
     }
     
     @Named("A1")
     class A1 implements A {
         //...
     }
     @Named("A2")
     class A2 implements A {
         //...
     }
     ```

     ```java
     void test() {
     	FushengContainer container = FushengContainer.startup();
         A a = container.getBean(A.class, "A1");
         asset a.getType() == A1.class    
     }
     ```