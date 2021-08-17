FushengDI

1.   What's DI

     DI 意为依赖注入，由系统或编辑器自动注入实现类，开发者只需声明所需接口便可使用，这种从以前开发者手动new实例转变为框架自动注入实现称为反转控制，而DI则是一个更加表义的名称，可以理解为一种设计模式，将我们这种自动注入功能标准化

2.   What's FushengDI

     FushengDI是基于DI模式实现的一个DI框架，支持通过构造器进行依赖的注入，轻量易用

3.   How to import FushengDI

     只需在项目引入`com.tw.fushengdi-core`便可使用

4.   Constructor-Based DI

     目前可通过`@Inject`标记构造器进行依赖的注入，例如

     ```java
     public class Test {
         private A a;
         @Inject
         public Test(A a) {
             this.a = a;
         }
         
         public void say() {
             a.say()
         }
     }
     
     interface A {
         void say();
     }
     
     class AImpl implements A {
         //...
     }
     ```

5.   Specify dependency implement

     我们可通过`@Named`在构造器上指定我们需注入的实现类

     ```java
     public class Test {
         private A a;
         @inject
         @Named("One")
         public Test(A a) {
             this.a = a;
         }
         
         public void say() {
             a.say()
         }
     }
     
     interface A {
         void say();
     }
     
     @Named("One")
     class OneImpl implements A {
         //...
     }
     @Named("Another")
     class AnotherImpl implements A {
         //...
     }
     ```

6.   Specify compoent scope in FushengContext

     我们可通过`@Singleton`指定我们每次注入的实现类都是同一个实现类，默认情况是每次注入的都是一个新的实现类

     ```java
     interface A {
         void say();
     }
     
     @Singleton
     class AImpl implements A {
         //...
     }
     ```
