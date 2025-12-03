# WordSDK Java HelloWorld

HelloWorld demonstration project for the Maven `com.wordsdk:wordsdk` library
```xml
<dependency>
  <groupId>com.wordsdk</groupId>
  <artifactId>wordsdk</artifactId>
  <version>0.9.1</version>
</dependency>
```

This repository showcases how **WordSDK** — a native JVM library powered by WebAssembly (WASM) — can be used in Java to seamlessly integrate proven C/C++ functionality without native DLLs or platform‑specific dependencies.

## What is WordSDK?
WordSDK demonstrates a new approach to bridging ecosystems:
- Compile existing C/C++ libraries to WASM
- Translate WASM into JVM classes using [Chicory](https://github.com/dylibso/chicory)
- Expose the result as a pure Java library
**Result**: a sandboxed, portable, and enterprise‑ready JVM artifact that leverages decades of C/C++ engineering while fitting naturally into Java workflows.

## Standalone Microsoft Word .docx/.doc to PDF conversion
WordSDK can be used as a **standalone converter** to transform Microsoft Word documents (`.docx` or legacy `.doc`) directly into PDF files — without relying on external DLLs or native dependencies. 
Perfect for enterprise environments where portability, sandboxing, and consistency are critical.
```java
    WordSDK.Worker api=WordSDK.createWorker(options);
    api.importFile(Paths.get("HelloWorld.docx"));
    api.exportPDF(Paths.get("HelloWorld.pdf"));
```
A working example is in [HelloWordSDK.java](src/main/java/com/wordsdk/HelloWordSDK.java)


## Integration with DOCX4J
WordSDK integrates seamlessly with [Docx4J](https://www.docx4java.org/), enabling developers to **leverage existing document‑processing solutions** while adding robust PDF export capabilities. By combining Docx4J’s powerful Word document manipulation features with WordSDK’s conversion engine, you can build end‑to‑end Java workflows that edit, transform, and render Word files directly to PDF — **all within the JVM sandbox**.
```java
    WordSDK.Worker api=WordSDK.createWorker(options);        
    OutputStream importStream=api.createImportStream();  // Create an import stream for feeding into WordSDK
    Docx4J.save(wordMLPackage, importStream, Docx4J.FLAG_NONE); // feed the DOCX4J document into WordSDK
    final byte[] pdf=api.exportPDF(); // generate an in-memory PDF for further processing...    
```
A working example is in the Unit Test [HelloWordSDK_Tests.DOCX4J](/src/test/java/HelloWordSDK_Tests.java#L142)


## Running the Demo
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/wordsdk-java-helloworld.git
   cd wordsdk-java-helloworld
   ```
2. Build and run:
   ```bash
   mvn clean compile exec:java
   ```
3. The demo will generate an `out.pdf` file in your project directory.

## Tests

The project includes JUnit tests. Run the test with 
```bash
mvn test
```

The tests that demonstrate:
- Importing a `.docx` file via file path and streams  
- Exporting to PDF and validating with [Apache PDFBox](https://pdfbox.apache.org/)  
- Using [Docx4J](https://www.docx4java.org/) to modify documents before export  


## Special Thanks
Huge credit to the [Chicory project](https://github.com/dylibso/chicory). Its ability to compile non‑trivial WASM modules into JVM classes is a game‑changer for developers bridging ecosystems.
