### Motivation
3 ideas how to solve problem.
1: keep list of files in memory and use String.contains for every word. Downside, uses a lot of memory. Plusside will use less CPU once read.<br>
2: count occurances whilst reading the file, then only one file will have to be in memory at the time BUT will be processing for ever search whereas (1) only will process on starting the program. "indexing directory" so to speak. <br>
3: use bloomfilter to give approximatly accurate % of how many of the words exists in file. Is prone to sometimes miss occurance but will never say a word exists that does not. Lower memoryusage, more CPU when indexing.<br>
<br>
(1) is implenented on master. (3) on branch called bloomfilter
### Installations
project uses maven, thus dependencies will be installed and artifact built with <br>
'''mvn install''' <br>
that will create a .jar file that can be found in the /target folder.
### Execution
'''java -jar search-1.0-SNAPSHOT.jar foo/bar''' <br>
where foo/bar is the directory you want to analyze <br>
type the words you are searching for and follow with the enter key to seach. <br>
quit the program sending "quit()" <br>
EDIT: in bloomfilter branch the artifact is now called search.jar
