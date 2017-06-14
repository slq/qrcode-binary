# QRCode-binary [![Build Status](https://travis-ci.org/slq/qrcode-binary.svg?branch=master)](https://travis-ci.org/slq/qrcode-binary)
Set of tools to generate and read QRCodes with any kind of content.

By convention, QR codes are usually used to transfer textual data. 
The most common exapmple of such usage is when a person is using his/hers mobile device to scan QR Code placed on bus station, shop, product label etc. to navigate to proper web page, where additional information is provided.

QR Codes, however, are not limited to transferring textual data. `QRCode-binary` is a library which facilitates sending any kind of data.
The library is design to send data between two hosts. One host serves data as a series of QR codes, while other is trying to read them sequentially. 
This might be very useful when you have very limited access to remote machine. When clipboard is disabled by Citrix / Remote host Administrator and sending data over public file sharing servers is not an option, all you have left is transferring data by _visual protocol_. 

`QRCode-binary` to the rescue!  

### Usage

Run the following snippet on both hosts:
```
git clone https://github.com/slq/qrcode-binary.git
cd qrcode-binary
./gradlew build
```

From your local machine execute:
```
cd qrcode-binary
java -jar qrcode-reader/build/libs/qrcode-reader.jar
```

It will start to _listen_ (or should I say _look for_) QR codes that appear on screen. 

Once this is ready, login to remote host and start generating QR codes:
```
cd qrcode-binary
java -jar qrcode-generator/build/libs/qrcode-generator.jar PATH_TO_YOUR_FILE
```

You will see QR codes generated on screen. They will be captured on your local machine and dumped to local file in `qrcode-binary` directory. 

### Available options
Currently there are no options available to set. 
