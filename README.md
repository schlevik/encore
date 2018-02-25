# EnCore
### About
This is the Java implementation of [PyCobalt](https://github.com/Lambda-3/PyCobalt). At the moment only the API usage is provided.
Currently name references (i.e. "Obama" references "Barack Obama") and pronominal references (i.e. "he" references "Barack Obama" )are detected and resolved.
### Java API
At the time being the API provides only one method, which is `resolveCoreference(String text)`.

Regard following example for the usage of the Java API.
```java
import com.gitlab.aidb.encore.EnCore;

public class EnCoreTest {
    public static void main(String[] args) {
        String text = "Barack Obama is an overused example. He does not bother, though.";
        EnCore enCore = new EnCore();
        String result = enCore.resolveCoreference(text);
        System.out.println(result); // prints "Barack Obama is an overused example . Barack Obama does not bother, though."
    }
}
```

### Development
Develop your own resolvers. They must implement the Interface `ReferenceResolver<T extends Reference>.` where `<T>` is a type variable for a reference type the resolver is trying to resolve. Communication between resolvers (if needed) is done via the implementation of *features* defined in the package `com.gitlab.aidb.encore.features`. Consider `FeatureExampleTest` for an examplary implementation.