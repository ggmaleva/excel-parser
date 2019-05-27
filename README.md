# excel-parser
## Quick guide

This is simple example of using Apache Poi for parsing .xls document and ganerate new .xml config file with information from Excel.

Example of excel file:

1. First shit

Module            | Path 
----------------- | -------------
ModuleName-1.0.0  | /META-INF/wsdl/com.example.MyWSDLName.wsdl
SuperModule-1.0.0 | /META-INF/wsdl/com.example.YourWSDLName.wsdl

Second shit:

Endpoint                                           |
---------------------------------------------------|
http://localhost:8080/myService/getService?wsdl    | 
http://localhost:8080/myService/clientService?wsdl |

Example config.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<modules>
    <module name="MyModule">
        <classes>
            <class name="com.example.service.rest.MyService">
                <points>
                    <point name="getInfo"/>
					<point name="setInfo"/>
                </points>
            </class>
            <class name="com.example.service.rest.ClientService">
                <points>
                    <point name="getName"/>
					<point name="setName"/>
                </points>
            </class>
        </classes>
    </module>
<modules>
```

