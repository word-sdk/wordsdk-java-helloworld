
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordsdk.WordSDK;
import com.wordsdk.WordSDK.WasmInstanceFactory;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.Docx4J;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class HelloWordSDK_Tests {
    final WasmInstanceFactory factory;

    final Logger logger=LoggerFactory.getLogger(HelloWordSDK_Tests.class);

    public HelloWordSDK_Tests() {
        WasmInstanceFactory _factory=null;
        try {
            //for debug:
            //_factory=new com.wordsdk.WasmTimeInstanceFactory();
            //_factory=new com.wordsdk.DylibsoChicoryInstanceFactory();
            if (null==_factory) {
                String factoryClassName = System.getProperty("com.wordsdk.wasm.instance.factory");            
                System.out.println("factoryClassName="+factoryClassName);
                if (null!=factoryClassName) {
                    Class<?> factoryClass = Class.forName(factoryClassName);
                    Constructor<?> factoryConstructor=factoryClass.getDeclaredConstructor();
                    _factory=(WasmInstanceFactory)factoryConstructor.newInstance();
                }
            }
        } catch(Exception e) {
            throw new RuntimeException("Please set \"com.wordsdk.wasm.instance.factory\" system property", e);
        }
        if (null!=_factory) {
            factory=_factory;
        } else {
            throw new RuntimeException("Please set \"com.wordsdk.wasm.instance.factory\" system property");
        }
    }

    /**
     * Utility method to copy all bytes from an InputStream to an OutputStream.
     * Uses a buffer to efficiently transfer data until EOF is reached.
     */
    private static void copyStreamHelper(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }    

    @Test
    void API(@TempDir Path tempDir) {
        assertDoesNotThrow(() -> {
            // Locate the sample Word document (HelloWorld.docx) from the test resources
            URL resourceUrl = getClass().getResource("/HelloWorld.docx");
            assertNotNull(resourceUrl, "Resource URL should not be null");            

            // Convert the resource URL to a URI (safe for use with Paths)
            URI resourceUri=null; try { resourceUri=resourceUrl.toURI(); } catch(URISyntaxException e) { }
            assertNotNull(resourceUri, "Resource URI should not be null");

            // Resolve the URI into a Path object for file operations
            Path resourcePath = Paths.get(resourceUri);

            // Configure SDK options (attach logger for test output)
            WordSDK.Options options=new WordSDK.Options();
            options.logger=logger;

            // Create a worker instance with the configured options
            WordSDK.Worker api=WordSDK.createWorker(factory, options);

            // Log the test execution with the resource path
            logger.info("API Test: "+resourcePath);

            // Import the Word document into the SDK
            assertTrue(api.importFile(resourcePath), "Document should be imported successfully");            

            // Export the imported document as a PDF (in-memory as byte array)
            final byte[] pdf=api.exportPDF();
            assertNotNull(pdf, "Exported PDF bytes should not be null");

            // Load the PDF using Apache PDFBox to validate its structure
            PDDocument pdfDocument = Loader.loadPDF(pdf);
            assertNotNull(pdfDocument, "PDF document should be loadable");

            // Verify that the generated PDF contains at least one page
            assertTrue(pdfDocument.getNumberOfPages() > 0, "PDF should contain at least one page");
        });
    }

    @Test
    void API_Stream(@TempDir Path tempDir) {
        assertDoesNotThrow(() -> {
            // Load the sample Word document (HelloWorld.docx) as an InputStream from test resources
            InputStream fileInputStream = getClass().getResourceAsStream("/HelloWorld.docx");
            assertNotNull(fileInputStream, "Input stream for test document should not be null");

            // Configure SDK options (attach logger for test output)
            WordSDK.Options options=new WordSDK.Options();
            options.logger=logger;

            // Create a worker instance with the configured options
            WordSDK.Worker api=WordSDK.createWorker(factory, options);        
            assertNotNull(api, "Worker instance should be created successfully");

            // Create an import stream for feeding the Word document into the SDK
            OutputStream importStream=api.createImportStream();
            assertNotNull(importStream, "Import stream should not be null");

            // Copy the Word document data into the SDK import stream
            HelloWordSDK_Tests.copyStreamHelper(fileInputStream, importStream);

            // Close both streams to finalize the import process
            fileInputStream.close();
            importStream.close(); // Closing triggers the SDK to load the file

            // Prepare a temporary file path for the exported PDF
            File tmpFile=tempDir.resolve("out.pdf").toFile();

            // Create an output stream to write the exported PDF to disk
            OutputStream fileOutputStream=new FileOutputStream(tmpFile);
            assertNotNull(fileOutputStream, "File output stream should not be null");

            // Create an export stream to retrieve the PDF from the SDK
            InputStream pdfExportStream=api.createExportPDFStream();
            assertNotNull(pdfExportStream, "PDF export stream should not be null");

            // Copy the exported PDF data into the output file
            HelloWordSDK_Tests.copyStreamHelper(pdfExportStream, fileOutputStream);

            // Close both streams to finalize the export process
            fileOutputStream.close();
            pdfExportStream.close();

            // Load the generated PDF using Apache PDFBox to validate its structure
            PDDocument pdfDocument = Loader.loadPDF(tmpFile);
            assertNotNull(pdfDocument, "Generated PDF should be loadable");

            // Verify that the PDF contains at least one page
            assertTrue(pdfDocument.getNumberOfPages() > 0, "PDF should contain at least one page");
        });
    }

    @Test
    void DOCX4J(@TempDir Path tempDir) {
        assertDoesNotThrow(() -> {
            // Load the sample Word document (HelloWorld.docx) as an InputStream from test resources
            InputStream fileInputStream = getClass().getResourceAsStream("/HelloWorld.docx");
            assertNotNull(fileInputStream, "Input stream for test document should not be null");

            // Parse the Word document into a Docx4J WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(fileInputStream);

            // Modify the document by adding a new paragraph of text
            wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello World added via Docx4J!");

            // Configure SDK options (attach logger for test output)
            WordSDK.Options options=new WordSDK.Options();
            options.logger=logger;

            // Create a worker instance with the configured options
            WordSDK.Worker api=WordSDK.createWorker(factory, options);        
            assertNotNull(api, "Worker instance should be created successfully");

            // Create an import stream for feeding the modified Word document into the SDK
            OutputStream importStream=api.createImportStream();
            assertNotNull(importStream, "Import stream should not be null");

            // Save the modified Word document into the SDK import stream using Docx4J
            Docx4J.save(wordMLPackage, importStream, Docx4J.FLAG_NONE);

            // Export the imported document as a PDF (in-memory as byte array)
            final byte[] pdf=api.exportPDF();
            assertNotNull(pdf, "Exported PDF bytes should not be null");

            // Load the PDF using Apache PDFBox to validate its structure
            PDDocument pdfDocument = Loader.loadPDF(pdf);
            assertNotNull(pdfDocument, "Generated PDF should be loadable");

            // Verify that the generated PDF contains at least one page
            assertTrue(pdfDocument.getNumberOfPages() > 0, "PDF should contain at least one page");
        });        
    }

}
