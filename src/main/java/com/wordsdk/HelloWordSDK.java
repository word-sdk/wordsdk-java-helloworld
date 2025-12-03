package com.wordsdk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWordSDK {

        // Configure a logger instance for capturing SDK logs and debug information
        private static Logger logger=LoggerFactory.getLogger(WordSDK.class);

        public static void main(String[] args) throws IOException {
            // Load and use system-installed fonts (default behavior).
            // This allows the SDK to render documents using fonts already available on the machine.
            WordSDK.useSystemFonts();
            
            // Optional: Register your own license file if you have one.
            // Uncomment the following line and provide the license file + secret key.
            //WordSDK.registerLicense(WordSDK.class.getClassLoader().getResource("wordsdk.license"), System.getenv("WORDSDK_LICENSE_SECRET"));

            // Example: Register custom fonts bundled with your application.
            // This is useful if you want consistent font rendering across environments,
            // or if the required fonts are not guaranteed to be installed on the system.
            WordSDK.registerFont(WordSDK.class.getClassLoader().getResource("fonts/NotoSansTamil-Regular.ttf"));
            WordSDK.registerFont(WordSDK.class.getClassLoader().getResource("fonts/NotoSansTamil-Bold.ttf"));

            // Configure SDK options
            WordSDK.Options options=new WordSDK.Options();
            options.verbose=0;  // Verbose logging for debugging (not recommended in production)
            options.logger=logger; // Attach the logger for capturing SDK logs
            options.productionMode=false; // // Set to true to enable production mode (disables dev/debug features)

            // Create a worker instance with the configured options            
            WordSDK.Worker api=WordSDK.createWorker(options);

             // Import a Word document from the project resources
            api.importFile(Paths.get("src", "test", "resources", "HelloWorld.docx"));

            // Define the output path for the generated PDF (saved in the current working directory)
            Path outPath=Paths.get(System.getProperty("user.dir"), "out.pdf");

            // Export the imported Word document as a PDF
            api.exportPDF(outPath);

            // Print confirmation message with the output file path
            System.out.println("Created PDF "+outPath);
        }        
}