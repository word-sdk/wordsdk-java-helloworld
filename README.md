# wordsdk-java-helloworld

HelloWorld demonstration project for [`com.wordsdk.wordsdk`](https://wordsdk.com).

This repository shows how to use **WordSDK** in Java to:
- Import a Word (`.docx`) document
- Register fonts and configure options
- Export the document as a PDF

## Prerequisites
- Java 11 or higher
- Maven for dependency management
- A sample Word document (`HelloWorld.docx`) in `src/test/resources`

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

The project includes JUnit tests that demonstrate:

- Importing a `.docx` file via file path and streams  
- Exporting to PDF and validating with [Apache PDFBox](https://pdfbox.apache.org/)  
- Using [Docx4J](https://www.docx4java.org/) to modify documents before export  

