## JSON/XML Converter
A Java app written from scratch to parse Json and XML from string and convert between them

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

