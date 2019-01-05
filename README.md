# Fragment Creator

[![Circle CI](https://circleci.com/gh/sys1yagi/fragment-creator.svg?style=svg)](https://circleci.com/gh/sys1yagi/fragment-creator)

Fragment Creator is a code generation library to manage fragment class creation and arguments for Android.

I wrote the newInstance method every time when declaring a new Fragment. Then, call `Fragment#getArguments()` and call a get method that is adapted parameter type (ex. getString(), getParcelable()). It's so a boring boilerplate code.

This library helps you to manage fragment class creation and arguments.

# Fragment Creator 2.0.0

Fragment Creator 2.0.0 or later supports AndroidX.
It can be used only with androidx.fragment.app.Fragment.

## How to use

### Annotate the arguments of Fragment

```java
@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    String keyword;

    @Args(require = false)
    String userId;
}
```

Then, MainFragmentCreator is generated.

### Create fragment with the FragmentCreator

```java
MainFragment instance = MainFragmentCreator
                            .newBuilder("keyword")
                            .build();

MainFragment instance = MainFragmentCreator
                            .newBuilder("keyword")  // required
                            .setUserId("mike")      // optional
                            .build();
```

### Read the arguments with the FragmentCreator

```java
public MainFragment extends Fragment {

    @Args
    String keyword;

    @Args(require = false)
    String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainFragmentCreator.read(this);

        // this.keyword, this.userId are initialized.
    }
}
```

### Default value

You can set default value.

```java
public MainFragment extends Fragment {

    // The default value can be set only if the argument is optional.
    @Args(require = false, defaultString = "unknown")
    String keyword;

    @Args(require = false, defaultInt = -1)
    int userId;

}
```

Supported types

- primitive type
- java.lang.String
- java.lang.Boolean
- java.lang.Byte
- java.lang.Character
- java.lang.Short
- java.lang.Integer
- java.lang.Long
- java.lang.Float
- java.lang.Double

### Private field

You __should__ declare a setter method if using private field.

```java
public MainFragment extends Fragment {

    @Args
    String keyword;

    @Args(require = false)
    private String userId;

    public String setUserId(String userId){
        this.userId = userId;
    }
}
```

### Type Serializer

If you want to use the class such as the following, you can use the ArgsSerializer.

```java
// It is not Serializable or Parcelable.
public class Product {

    public int id;

    public String name;
}
```

At first, define a class that implements the ArgsSerializer.

```java
public class StringSerializer implements ArgsSerializer<Product, String> {

    @Override
    public String serialize(Product product) {
        return product.getId() + "," + product.getName();
    }

    @Override
    public Product deserialize(String str) {
        String[] split = str.split(",");
        Product product = new Product();
        product.setId(Integer.parseInt(split[0]));
        product.setName(split[1]);
        return product;
    }
}
```

Set `@Serializer`.

```java
@FragmentCreator
public class MainFragment extends Fragment {
    @Args
    @Serializer(to = String.class, serializer = StringSerializer.class)
    Product product;
//...
```

### Supported types

- primitive type
- `java.lang.String`
- `java.lang.Boolean`
- `java.lang.Byte`
- `java.lang.Character`
- `java.lang.Short`
- `java.lang.Integer`
- `java.lang.Long`
- `java.lang.Float`
- `java.lang.Double`
- `java.lang.CharSequence`
- `android.os.Parcelable`
- `java.io.Serializable`
- `java.util.ArrayList<? extends android.os.Parcelable>`
- `java.util.ArrayList<java.lang.Integer>`
- `java.util.ArrayList<java.lang.String>`
- `java.util.ArrayList<java.lang.CharSequence>`

### Not supported yet

- `android.os.Parcelable[]`
- `android.util.SparseArray<? extends android.os.Parcelable>`
- `boolean[]`
- `byte[]`
- `short[]`
- `char[]`
- `int[]`
- `long[]`
- `float[]`
- `double[]`
- `java.lang.String[]`
- `java.lang.CharSequence[]`
- `android.os.Bundle`

### Installation

This library is distributed by [JitPack](https://jitpack.io/). Add dependencies your build.gradle

```
apt 'com.github.sys1yagi.fragment-creator:processor:2.0.0'
compile 'com.github.sys1yagi.fragment-creator:library:2.0.0'
```

## Development


__Show version__

```
$ ./gradlew version
```

__Bump version__

```
$ ./gradlew bumpMajor
$ ./gradlew bumpMinor
$ ./gradlew bumpPatch
```

__Generate README__

```
$ ./gradlew genReadMe
```
