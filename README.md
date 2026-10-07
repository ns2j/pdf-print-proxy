# Preview-less Printing from a Web Application

## What is pdf-print-proxy

pdf-print-proxy is a proxy for command-line PDF printing programs ([PDFtoPrinter.exe](https://mendelson.org/pdftoprinter.html) on Windows, `lpr` on other platforms).
It is installed on the local computer and provides a web API, which your web application can call.
Supported operating systems: Windows, macOS, and Linux.

## Build

Requirements:

- JDK 21 or later (set `JAVA_HOME` correctly)
- Maven
- WiX Toolset (Windows only)

```
mvn package
```

The installer will be created in the `target/jpackage` folder.

## Run

Install the application using the installer.

- Windows: The application does not start automatically. Click the pdf-print-proxy icon to start it; it appears in the system tray.
- macOS and Linux: The application is installed as a service.

## Confirm

Use curl (`curl.exe` on Windows) to confirm that it works.

```
curl http://localhost:6753
```

This returns a list of printer names. Set `printerName` in `data.json` to one of them, then run:

```
curl http://localhost:6753 -d '@data.json' -H 'Content-Type: application/json'
```

The sample PDF file will be printed.

## Sample Web Application

A sample web application is available in the [sample folder](https://github.com/ns2j/pdf-print-proxy/blob/main/sample).

## Notes

- The macOS version runs as the root user, so a default printer is usually not set.
- I tried running it as a Windows service, but a service runs under the local system account, which cannot use network printers.
