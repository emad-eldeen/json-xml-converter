## JSON/XML Converter
A Java app written from scratch to parse Json and XML from string and convert between them

## Technical requirements
- Parse XML text files
- Parse Json text files as follows:
  - check if the JSON value starts with a curly brace that can be presented as a start of child nodes and continue to check subkeys.
  - if the json object complies with the following rules, it should be parsed as an _xml object with attributes_. Otherwise, it should be considered as a normal object:
    1. The object has a key with the same name as the object, with a `#` symbol in front of it. For example, if the key of the object is "obj" then the value of this object has to be inside `#obj` key inside this object. Note that if such a key does not exist in the object, this object should not be considered a single XML object with attributes.
    2. The value object contains the `#value` key and all other attributes begin with the `@` symbol and are longer than 1 character. If this object has at least one key that equals `@` or does not start with `@` (except #value), then this object should not be considered a single XML object with attributes.
    3. If the value of any key starting with `@` is not a number, string or null (in other words, it will be an object starting with "{"), then this object cannot be an attribute of a single XML object and the `@` symbol should be removed from this key, and thus the object cannot be considered a single XML object.
- Convert between XML and JSON
- Print raw XML and JSON
- Print tree of XML and JSON elements

## Knowledge used
- Scanning and parsing user input
- Exception handling
- Reading files
- Factory design pattern
- Abstract classes
- Interfaces
- Java records
- Patterns and RegEx
- Collections
- Streams
- Recursion functions
- Enums

## How to use
copy the file to be parsed to the `resources` folder, run the app and follow the instructions.

## Example
Input.txt:
```
<node>
    <child name = "child_name1" type = "child_type1">
        <subchild id = "1" auth="auth1">Value1</subchild>
    </child>
    <child name = "child_name2" type = "child_type2">
        <subchild id = "2" auth="auth1">Value2</subchild>
        <subchild id = "3" auth="auth2">Value3</subchild>
        <subchild id = "4" auth="auth3"></subchild>
        <subchild id = "5" auth="auth3"/>
    </child>
</node>
```
Running the app:
```
   $$$$$\  $$$$$$\   $$$$$$\  $$\   $$\          $$\     $$\          $$\   $$\ $$\      $$\ $$\       
   \__$$ |$$  __$$\ $$  __$$\ $$$\  $$ |        $$  |    \$$\         $$ |  $$ |$$$\    $$$ |$$ |      
      $$ |$$ /  \__|$$ /  $$ |$$$$\ $$ |       $$  /      \$$\        \$$\ $$  |$$$$\  $$$$ |$$ |      
      $$ |\$$$$$$\  $$ |  $$ |$$ $$\$$ |      $$  /$$$$$$\ \$$\        \$$$$  / $$\$$\$$ $$ |$$ |      
$$\   $$ | \____$$\ $$ |  $$ |$$ \$$$$ |      \$$< \______|$$  |       $$  $$<  $$ \$$$  $$ |$$ |      
$$ |  $$ |$$\   $$ |$$ |  $$ |$$ |\$$$ |       \$$\       $$  /       $$  /\$$\ $$ |\$  /$$ |$$ |      
\$$$$$$  |\$$$$$$  | $$$$$$  |$$ | \$$ |        \$$\     $$  /        $$ /  $$ |$$ | \_/ $$ |$$$$$$$$\ 
 \______/  \______/  \______/ \__|  \__|         \__|    \__/         \__|  \__|\__|     \__|\________|
                                                                                                       

Type the name of the file to be parsed, or 'exit' to exit the program
input.txt
Parsing complete!
Available commands: 'print', 'print tree' and 'convert'
print tree
Element:
path = node
value = null

Element:
path = node, child
value = null
attributes:
name = "child_name1"
type = "child_type1"

Element:
path = node, child, subchild
value = "Value1"
attributes:
id = "1"
auth = "auth1"

Element:
path = node, child
value = null
attributes:
name = "child_name2"
type = "child_type2"

Element:
path = node, child, subchild
value = "Value2"
attributes:
id = "2"
auth = "auth1"

Element:
path = node, child, subchild
value = "Value3"
attributes:
id = "3"
auth = "auth2"

Element:
path = node, child, subchild
value = null
attributes:
id = "4"
auth = "auth3"

Element:
path = node, child, subchild
value = null
attributes:
id = "5"
auth = "auth3"


Type the name of the file to be parsed, or 'exit' to exit the program
exit
Goodbye!

BUILD SUCCESSFUL in 1m 12s
3 actionable tasks: 2 executed, 1 up-to-date
10:40:44: Execution finished ':app:App.main()'.
```

