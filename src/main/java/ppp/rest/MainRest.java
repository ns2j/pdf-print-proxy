package ppp.rest;

import static ppp.Application.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ppp.dto.PrintData;
import ppp.dto.PrinterNames;

@RestController
@RequestMapping("/")
@CrossOrigin(origins="*")
public class MainRest {
    List<String> getPrinterNames() {
        List<String> names = new ArrayList<>();
        PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, null);
        if (pss == null) return names;
        for(var ps: pss)
            names.add(ps.getName());
        return names;
    }

    @GetMapping
    public ResponseEntity<PrinterNames> index() {
        PrinterNames pns = new PrinterNames();
        pns.names = getPrinterNames();
        return ResponseEntity.ok(pns);
    }

    boolean checkPrinterName(String printerName) {
        List<String> names = getPrinterNames();
        for (var n: names)
            if (n.equals(printerName))
                return true;
        return false;
    }

    ProcessBuilder getProcessBuilder(String printerName, Path pdfPath) {
        boolean existsPrinterName = printerName != null && !printerName.isBlank();
        if (existsPrinterName) printerName = printerName.trim();
        String pdf = pdfPath.toString();
        ProcessBuilder pb = null;
        if (isWindows()) {
            String command = "PDFToPrinter";
            if (existsPrinterName) {
                if (!checkPrinterName(printerName))
                    throw new ResponseStatusException
                    (HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), 
                            printerName + " does not exist");
                pb = new ProcessBuilder(command, pdf, printerName);
            } else {
                pb = new ProcessBuilder(command, pdf);
            }
        } else {
            String command = "lpr";
            if (existsPrinterName)
                pb = new ProcessBuilder(command, "-P", printerName, pdf);
            else
                pb = new ProcessBuilder(command, pdf);
        }

        pb.redirectErrorStream(true);
        return pb;
    }

    @PostMapping
    public ResponseEntity<String> index(@RequestBody PrintData printData) throws IOException, InterruptedException {
        Path pdfPath = Files.createTempFile("ppp" + System.currentTimeMillis(), ".pdf");
        Files.write(pdfPath, printData.pdf);

        ProcessBuilder pb = getProcessBuilder(printData.printerName, pdfPath);
        Process p = pb.start();

        StringBuilder sb = new StringBuilder();
        if (!isWindows()) {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
        }
        //p.waitFor(30, TimeUnit.SECONDS);
        p.waitFor();
        p.children().forEach(c -> c.destroy());
        p.destroy();

        int exitCode = p.exitValue();
        sb.append("exit code: " + exitCode + "\n");

        Files.delete(pdfPath);

        if (exitCode != 0)
            throw new ResponseStatusException(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), sb.toString());

        return ResponseEntity.ok(sb.toString());
    }
}
