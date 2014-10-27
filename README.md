This is source code of project "A Hybrid Approach to Learning DL Ontology From Text"  
  
## Prerequisites:
 1. [Metamap version 2013](http://metamap.nlm.nih.gov/). When install, put `public_mm` folder in the same directory with this source code directory  
 2. [Python](https://www.python.org/). To test, run `python generatePositiveExamplesOriTest.py`
 3. [Stanford Parser](http://nlp.stanford.edu/software/lex-parser.shtml). After extracting the folder, make sure that you rename the outer folder to become `stanford-parser` (rename `stanford-parser-full-20xx-xx-xx` to `stanford-parser`). Then put that folder in the same directory with this source code directory  

## How to run:
 1. Compile all `.java` files by running `javac *.java`  
 2. Run GUI class by command `java GUI`  
 3. Insert input file containing biomedical text. You can try to insert file `input.in` in this folder  
 4. Click `Process` and see the result  

## How to learn a GCI is formed by system:
User can see a set of sentences where a GCI comes from by clicking the `?` symbol.  
In the second box, the system will show those set of sentences.  

## How to validate a GCI:
User may change the role relation between two concept names.  
 1. To validate, click the `V` symbol.  
 2. In third box, system shows two concept names and their role in the format `<first concept name> | <role relation> | <second concept name>`  
 3. User can change role relation by writing this abbreviation:  
  * `AM`: Associated Morphology  
  * `AM-1`: Associated Morphology (inverse role)  
  * `FS`: Finding Site  
  * `FS-1`: Finding Site (inverse role)  
  * `CA`: Causative Agent  
  * `CA-1`: Causative Agent (inverse role)  
  * `IS`: Is-A relation  
  * `IS-1`: Is-A relation (inverse role)
 4. and finally, click `Train`.
