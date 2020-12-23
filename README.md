# Sorts, cleans and capitalizes files

Finds all specified files in the current directory and its sub-folders if desired.  
Then removes duplicated lines, capitalizes them and sort the content.  

### How to use it
Place the *jar* file in where the files to process are and with a command-line interface run `java -jar sort.jar`.  
As input args, you can specify the `extension` looked for (default is "*txt*"), if it's `recursive` (default is "*true*") and if it `capitalize` the contents (default is "*true*").  
When first use, a `config.properties` will be created holding these. It will be edited when other inputs will be passed.  
For instance, to disable recursivity and capitalization run: `java -jar sort.jar recursive false capitalize false`
