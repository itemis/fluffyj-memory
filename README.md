# Fluffy J Memory
Adding some fluff to [Java's Foreign Function and Memory API](https://openjdk.org/jeps/424) ❤

## Prerequisites
This software requires Java 22 and Maven >= 3.3.x. However, it is best to use a current Maven version, i. e. >= 3.9.x.

## Build
Usually a `mvn clean install` should be enough.

## IDE Setup
Due to the usage of Java 22 code, the following special setup is required:  
* Add the following to the startup JVM options of your IDE (e. g. eclipse.ini):

```
--enable-native-access=ALL-UNNAMED
```
* Add the following JVM option to launch configurations in order to be able to run (tests) from within the IDE:
```--enable-native-access=ALL-UNNAMED```

## CAUTION
Due to the nature of unchecked memory access, it is possible to program all sort of weird things like "pointer magic", "ill-casting" data, address / read / write dangerous memory areas, etc. In most of these cases, the JVM will crash and / or the OS will prevent anything bad from happening **but this is not guaranteed**.  
  
So **please, please please** be careful and know that according to the license, there is no warranty provided with respect to any kind of damage or data loss.

## How to use
Check out the examples from down below. Also Javadoc is available with the source code.  
  
A real world example can be seen at [jSCDLib](https://github.com/itemis/jscdlib).

### Allocating an off heap Long

```
FluffyScalarSegment<Long> seg = FluffyMemory.segment().of(123L).allocate();
System.out.println(seg.getValue());  // Prints 123
  
MemorySegment nativeSeg = someNativeCode.getNumber();
seg = FluffyMemory.wrap(nativeSeg).as(long.class);
System.out.println(seg.getValue());  // Prints contents of nativeSeg interpreted as Long
```

### Pointing to an off heap Long

```
long nativeAddress = someNativeCode.getPtr();
FluffyScalarPointer<Long> valuePtr = FluffyMemory.pointer().to(nativeAddress).as(long.class).allocate();
System.out.println(valuePtr.dereference()); // prints contents of segment valuePtr points to, interpreted as Long
```
  
Please note that due to reasons, primitive array types (i. e. byte[]) are not supported at the moment. Please consider using their object-counterparts instead (i. e. Byte[]).

### Dereferencing an arbitrary address as Long
  
```
MemorySegment valueSeg = Arena.ofAuto().allocate(ValueLayout.JAVA_LONG, 123L);
long value = FluffyMemory.dereference(valueSeg.address()).as(long.class);
System.out.println(value); // 123
```

### Dereferencing MemorySegment as Long
  
```
MemorySegment valueSeg = Arena.ofAuto().allocate(ValueLayout.JAVA_LONG, 123L);
long value = FluffyMemory.dereference(valueSeg).as(long.class);
System.out.println(value); // 123
```

### Allocating an off heap Array

```
Byte[] bytes = new Byte[] {1, 2, 3};
FluffyVectorSegment<Byte> seg = FluffyMemory.segment().ofArray(bytes).allocate();
System.out.println(Arrays.toString(seg.getValue())); // Prints [1, 2, 3]
  
MemorySegment nativeSeg = someNativeCode.getNumbers();
FluffyVectorSegment<Byte> seg = FluffyMemory.wrap(nativeSeg).asArray(Byte[].class);
System.out.println(seg.getValue());  // Prints contents of nativeSeg interpreted as array of byte
```

### Pointing to an off heap Array

```
long nativeAddress = someNativeCode.getPtr();
FluffyVectorPointer<Byte> valuePtr = FluffyMemory.pointer().to(nativeAddress).asArray(arraySizeInBytes).of(Byte[].class).allocate();
System.out.println(valuePtr.dereference()); // prints contents of segment valuePtr points to, interpreted as Long
```

### Call a function from stdlib  
  
```
String testStr = "testStr";
MemorySegment ptr = FluffyMemory.segment().of(testStr).allocate().address();
NativeMethodHandle<Long> strlen = FluffyNativeMethodHandle
    .fromCStdLib()
    .returnType(long.class)
    .func("strlen")
    .args(ValueLayout.ADDRESS);
  
System.out.println(strlen.call(ptr)); // prints 7
```
  
### Call a function from stdlib that takes function pointers as arguments  
  
```
// Use C stdlib's quick sort on an array of 1024 bytes.
  
byte[] primitiveBuf = new byte[1024];
// Fill array with random bytes.
new Random().nextBytes(primitiveBuf);
// Convert from byte[] to Byte[] with Apache Commons
Byte[] buf = ArrayUtils.toObject(primitiveBuf);
FluffyVectorSegment<Byte> bufSeg = FluffyMemory.segment().ofArray(buf).allocate();
  
FluffyNativeMethodHandle<Void> qsort = FluffyNativeMethodHandle.fromCStdLib()
    .noReturnType()
    .func("qsort")
    .args(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
  
MemorySegment comparator = FluffyMemory.pointer()
    .toCFunc("qsort_comparator")
    .of(this)
    .autoBind();
  
// array base addr, length of array in bytes, length of one element in bytes, pointer to comparator func
qsort.call(bufSeg.address(), buf.length, 1, comparator);
System.out.println(Arrays.toString(bufSeg.getValue())); // Prints sorted buf
  
...
  
// 0.. equal, -1.. left larger, 1.. right larger
public int qsort_comparator(MemorySegment left, MemorySegment right) {
    Arena arena = Arena.ofAuto();
    Byte leftByte = pointer().to(left.address()).as(Byte.class).allocate(arena).dereference();
    Byte rightByte = pointer().to(right.address()).as(Byte.class).allocate(arena).dereference();
        
    int result = 0;
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
  
FluffyNativeMethodHandle<Long> func = FluffyNativeMethodHandle
    .fromLib(lib)
    .withLinker((symbol, srcFuncType) -> myLinker.link(symbol, srcFuncType))
    .withTypeConverter(new MyCustomTypeConverter())
    .returnType(long.class)
    .func("myFunc")
    .args(NATIVE_DATA_TYPE);
  
Long resultValue = func.call((MyDataType)someData);
```
