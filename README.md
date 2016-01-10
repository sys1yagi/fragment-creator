# Fragment Creator

Fragment Creator is a code generation library to manage fragment class creation and arguments for Android.

I write newInstance method on Fragment. Then, call Fragment#getArguments() and call a get method that is adapted parameter type (ex getString(), getParcelable()). It's so a boring boilerplate code.

This library helps you to manage fragment class creation and arguments. 

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

Then, MainFragmentCreator and MainFragmentArguments is generated.

### Create fragment with the FragmentCreator

```java
MainFragment instance = MainFragmentCreator.newInstanceWithKeyword("keyword");
MainFragment instance = MainFragmentCreator.newInstance("keyword", "user_id");
```

### Read the arguments with the ArgumentsReader

```java
MainFragmentArguments arguments = MainFragmentArguments(getArguments());

String keyword = arguments.getKeyword();
String userId = arguments.getUserId();
```

### Supported types

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
- java.lang.CharSequence
- android.os.Parcelable
- java.io.Serializable

### Not supported yet

- android.os.Parcelable[]
- java.util.ArrayList<? extends android.os.Parcelable>
- android.util.SparseArray<? extends android.os.Parcelable>
- java.util.ArrayList<java.lang.Integer>
- java.util.ArrayList<java.lang.String>
- java.util.ArrayList<java.lang.CharSequence>
- boolean[]
- byte[]
- short[]
- char[]
- int[]
- long[]
- float[]
- double[]
- java.lang.String[]
- java.lang.CharSequence[]
- android.os.Bundle

### Installation

This library is distributed by [JitPack](https://jitpack.io/). Add dependencies your build.gradle

```
apt 'com.github.sys1yagi.fragment-creator:processor:0.0.2'
compile 'com.github.sys1yagi.fragment-creator:library:0.0.2'
```

## Development


TODO
