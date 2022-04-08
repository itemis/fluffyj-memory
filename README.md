# Fluffy J Memory
Adding some fluff to Java's foreign memory abstraction ❤

## Allocating an off heap Long

```
var seg = FluffyMemory.segment().of(123).allocate();
var nativeSeg = someNativeCode.getNumber();
var nativeSegValue = FluffyMemory.wrap(nativeSeg).asLong().getValue();
```

## Pointing to an off heap Long

```
var nativeAddress = someNativeCode.getPtr();
var value = FluffyMemory.pointer().<Long>to(nativeAddress).allocate().dereference();
```


