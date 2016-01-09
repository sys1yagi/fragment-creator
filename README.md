# Fragment Creator

__UNDER DEVELOPMENT__

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

### Create fragment with the FragmentCreator

```java
MainFragment instance = MainFragmentCreator.newInstance("keyword");
MainFragment instance = MainFragmentCreator.newInstance("keyword", "user_id");
```

### Read the arguments with the ArgumentsReader

```java
MainFragmentArguments arguments = MainFragmentArguments(getArguments());

String keyword = arguments.getKeyword();
String userId = arguments.getUserId();
```

### Instllation

This library is distributed by [JitPack](https://jitpack.io/). Add dependencies your build.gradle

```
apt ''
compile ''
```

## Development


