# Preview-less printing with Web Application   
## What is pdf-print-proxy   
pdf-print-proxy is proxy of command line pdf printing program([PDFtoPrinter.exe](https://mendelson.org/pdftoprinter.html) in Windows, lpr in others).   
It is installed in local computer. It provides web api. Web Application calls it's api.    
Supported os is Windows, Mac and Linux.   

## Build   
Requirements   
* JDK >= 21. Deffine JAVA_HOME corretly.
* maven
* Wix toolset(Windows)   
```
mvn package
```
then, created installer is in target/jpackage folder.   

### Run   
Intall by installer.   
Windows version is not auto starting. Click pdf-print-proxy icon to start. It appears in SystemTray.      
Others version is installed as service.   

### Confirm   
Confirming is using curl(curl.exe in Windows)
```
curl http://localhost:6753
```   
shows printer name list.   
Edit printerName in data.json.
```
curl http://localhost:6753 -d '@data1.json' -H 'Content-Type: application/json'
```   
then, sample pdf file will print.   

### Sample Web Application   
Sample Web Application is [sample folder](sample).   

### Note
* Mac version is running as root user. It usualy not specified default printer.   
* I attempted Windows service. It runs as local system account. It cannot use network printer.

