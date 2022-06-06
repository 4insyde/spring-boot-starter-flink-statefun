# Spring boot starter Flink SF

### Short Description

This spring boot starter can be used only for remote flink modules based on spring boot. 
It will simplify interaction between spring and flink, helps developers to solve routine issues 
and increase function readability

### API
`@StatefulFunction` - annotation that indicates a stateful function class and describe how to
build TypeName for this function. Annotation has two parameters `namespace` and `name`.
Both of them are used for TypeName in following format <namespace>/<name>. If you didn't define 
your custom values then TypeName will be created with default value, `namespace` - class package and 
`name` - class simple name. You can define one of them, these parameters are optional and will override 
default value.

`@Handler` - annotation that indicates a stateful function handler, could be applied to the method that corresponding 
following requirements:  
1. Public method
2. return value is `CompletableFuture<Void>`
3. Method parameters `Context context, T event` where `T` is any class that can be serialized and deserialized

`@MessageType` - indicates a class or field that will be used to serialize or deserialize Class defined in generic 
of Type<T> object. Starer will load automatically Type object into `TypeResolver`.

`DispatchableFunction` - interface that indicates to Flink SF specification that it's stateful function.
`SerDeType` - interface that could be used instead of field messate type declaration to identify Flink Type
related class, the interface should be annotated with `@MessageType`

###Endpoint
Endpoint `/v1/functions` - is API for statefun engine for communication with remote module, via this endpoint 
statefun engine is able to invoke functions which registered in spring context

## Starter in Action

### Step 1 - Create Spring boot Application
I think it shouldn't be an issue for most developers that are familiar with Spring boot.
Guide https://spring.io/guides/gs/spring-boot

### Step 2 - Add starter dependency into maven pom.xml

```xml
<dependency>
    <groupId>com.github.csipon</groupId>
    <artifactId>spring-boot-starter-flink-statefun</artifactId>
    <version>0.1.0</version>
</dependency>
```
### Step 3 - EnableMessageTypeScan
To enable scanning fields annotated with `@MessageType` you need apply to your config class `@EnableMessageTypeScan` and
define annotation parameter `basePackageScan`
If you pass into `basePackageScan` root path, like that `com`, then your will receive an exception that it's not allowed,
due to so many packages to scan, but for some brave developers it has an option to disable it, keep in mind that 
starter will scan all classes inside the path and deeper. To disable `basePackageScan` validation define a property 
`flink-sf.scan.types.validationEnabled=false`

### Step 4 - Create function event
Simple event with one field `text` and static field `TYPE` that annotated with `@MessageType`.
TYPE field is responsible for serialization and deserialization of `IncrementEvent`. Annotation `@MessaageType` 
says that this field will be found and loaded into global type resolver automatically, therefore it will be 
able to use this event in functions

```java
import com.spring.flink.statefun.api.DataType;

public class IncrementEvent {

    @DataType
    public static final Type<IncrementEvent> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("<namespace>/IncrementEvent"),
            new ObjectMapper()::writeValueAsBytes,
            bytes -> new ObjectMapper().readValue(bytes, IncrementEvent.class));

    private String text;
    // Constructors, Getters and Setters ...
}
```

### Step 5 - Create a function

We created a simple function `FooFn` that increments `COUNT` value when receiving an `IncrementEvent`.
Also, we can mark it with `@StatefulFunction` annotation and now the function is a part of Spring context

```java
import com.spring.flink.statefun.api.Handler;
import com.spring.flink.statefun.api.StatefulFunction;

@StatefulFunction
public class FooFn implements DispatchableFunction {
    public static final ValueSpec<Integer> COUNT = ValueSpec.named("count").withIntType();

    @Handler
    public CompletableFuture<Void> increment(Context context, IncrementEvent event) {
//        do some action with the event ...
//        extract value from the context or default
        Integer count = context.storage().get(COUNT).orElse(0);
//        change value
        count++;
//        put changed value into the context
        context.storage().set(COUNT, count);

        return context.done();
    }
}
```

### Step 6 - Add additional handlers

Now we can easily add another handler, let's call it decrement

```java
    @Handler
    public CompletableFuture<Void> decrement(Context context, DecrementEvent event) {
        Integer count = context.storage().get(COUNT).orElse(0);
        count--;
        context.storage().set(COUNT, count);

        return context.done();
    }
```

### Step 7 - Add handler that send message to another function
```java
@Handler
public CompletableFuture<Void> onBarEvent(Context context, BarEvent event) {
    final Message message =
            MessageBuilder.forAddress(BarFn.class, "<functionId>")
                    .withCustomType(BarEvent.TYPE, event)
                    .build();
    context.send(message);
    return context.done();
}
```

Use `MessageBuilder` from starter package to be able to build messages using function class

### Summary 

We've created the Stateful Function that can handle several events(`IncrementEvent`, `DecrementEvent`), moreover 
our function is a Spring bean and part of Spring's context, therefore we can use all Spring features for this bean.

### Alternative type declaration
For some events it's impossible to declare a field inside the event and annotate it. To do type declaration in 
different approach you can use `SerDeType<T>`
## Example

```java
import com.spring.flink.statefun.api.DataType;

@DataType
public class FooSerDeType implements SerDeType<FooEvent> {

    @Override
    public Type<FooEvent> type() {
        return SimpleType.simpleImmutableTypeFrom(
                TypeName.typeNameFromString("<namespace>/FooEvent"),
                new ObjectMapper()::writeValueAsBytes,
                bytes -> new ObjectMapper().readValue(bytes, FooEvent.class));
    }
}
```

It is more powerful approach that require an extra class but open for you all Spring Bean features, actually class
annotated with `@MessageType` will be registered into Spring Context as bean, it means that you can work with this class
as with simple spring component