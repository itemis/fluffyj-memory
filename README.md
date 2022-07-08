# Fluffy J Memory
Adding some fluff to Java's foreign memory abstraction ❤

## Prerequisites
This software requires Java 17 and Maven >= 3.3.x. However, it is best to use a current Maven version, i. e. >= 3.8.x.

## Build
Usually a `mvn clean install` should be enough.

## CAUTION
Due to the nature of unchecked memory access, it is possible to program all sort of weird things like "pointer magic", "ill-casting" data, address / read / write dangerous memory areas, etc. In most of these cases, the JVM will crash and / or the OS will prevent anything bad from happening **but this is not guaranteed**.  
  
So **please, please please** be careful and know that according to the license, there is no warranty provided with respect to any kind of damage or data loss.

## How to use
Check out the examples from down below. Also Javadoc is available with the source code.

### Allocating an off heap Long

```
var seg = FluffyMemory.segment().of(123).allocate();
var nativeSeg = someNativeCode.getNumber();
var nativeSegValue = FluffyMemory.wrap(nativeSeg).asLong().getValue();
```

### Pointing to an off heap Long

```
var nativeAddress = someNativeCode.getPtr();
var value = FluffyMemory.pointer().to(nativeAddress).asLong().allocate().dereference();
```

### Call a function from stdlib  
  
```
var testStr = "testStr";
var ptr = FluffyMemory.segment().of(testStr).allocate().address();
var strlen = NativeMethodHandle
                .fromCStdLib()
                .returnType(long.class)
                .func("strlen")
                .args(CLinker.C_POINTER);

System.out.println(strlen.call(ptr)); // prints 7
```
  
### Call a function from stdlib that takes function pointers as arguments  
  
```
// Use C stdlib's quick sort on an array of 1024 bytes.

var primitiveBuf = new byte[1024];
// Fill array with random bytes.
new Random().nextBytes(primitiveBuf);
// Convert from byte[] to Byte[] with Apache Commons
var buf = ArrayUtils.toObject(primitiveBuf);
var bufSeg = FluffyMemory.segment().ofArray(buf).allocate();

var qsort = NativeMethodHandle.fromCStdLib()
            .noReturnType()
            .func("qsort")
            .args(CLinker.C_POINTER, CLinker.C_INT, CLinker.C_INT, CLinker.C_POINTER);

var comparator = FluffyMemory.pointer()
            .toCFunc("qsort_comparator")
            .of(this)
            .autoBind();
            
// base, length of array in bytes, length of one element in bytes, pointer to comparator function
qsort.call(bufSeg.address(), buf.length, 1, comparator);
Byte[] result = bufSeg.getValue(); // sorted buf

...

// 0.. equal, -1.. left larger, 1.. right larger
int qsort_comparator(MemoryAddress left, MemoryAddress right) {
    var scope = ResourceScope.globalScope();
    var sizeOfOneElmInByte = 1L;
    var leftByte = FluffyMemory.wrap(left.asSegment(sizeOfOneElmInByte, scope)).as(Byte.class).getValue();
    var rightByte = FluffyMemory.wrap(right.asSegment(sizeOfOneElmInByte, scope)).as(Byte.class).getValue();
        
    var result = 0;
    if (leftByte < rightByte) {
        result = -1;
    } else if (leftByte > rightByte) {
        result = 1;
    }
    
    return result;
}
```

### Call a function from an arbitrary lib  
  
```
System.loadLibrary("lib_name_known_to_system_linker");
var lib = SymbolLookup.loaderLookup();
        
var func = NativeMethodHandle
                .fromLib(lib)
                .withLinker((symbol, srcFuncType, targetMethodType) -> myLinker.link(symbol, targetMethodType, srcFuncType))
                .withTypeConverter(new MyCustomTypeConverter())
                .returnType(long.class)
                .func("myFunc")
                .args(NATIVE_DATA_TYPE);

var resultValue = func.call(someData);
```
