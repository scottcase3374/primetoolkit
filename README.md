# PrimeToolKit

## Personal Project involving prime numbers

## Preface
This is a pet project of mine. I have interests in some math related areas and this is the type of personal project I like to work on. This also serves as both a skills demonstration and a platform for trying out new technologies.

## Technology Summary
Here are some highlights regarding technology tried/used.
1. Java 18
2. Picocli - command line handling
3. JBoss Infinispan - caching
4. Protobuf - related to caching
5. Eclipse Collections - alternative for standard java for lower memory usage, etc.
6. Antlr4 - parsing and processing a very simple "SQL like" language for primes.
7. Netty - provide remote access to the PrimeSQL processing (via telnet as first POC)
8. DropWizard - metrics info
9. Jakarta Validation - as replacement for javax validation.
10. JGrapht / JGraphx - some graphing POC
11. JUnit Jupiter - as replacement for JUnit4
12. JBoss Weld - some POC work for dependency injection with plain Java apps.
13. Lombok - code generation / simplification
14. Maven

Here are some of the tools used.
1. Eclipse plugins
	- git
	- pmd
	- sonar
	- m2e
	- AnyEditTools
	- SpotBugs

2. Maven plugins
	- Antlr

## Skills demonstrated
1. Complex data structure design
2. Lexical analysis / parsing
3. Network application aspects
4. Object Oriented design
5. Java Lambda and Stream usage

## Examples of Design Patterns used
- Singleton 		- example: enum BaseTypes
- Interpreter		- Antlr generated code plus PrimeSqlVisitor / PrimeSQLChannelHander classes
- Iterator 			- PrimeTreeIterator / PrimeTreeIteratorIntfc classes
- Strategy 			- "Base" handling classes; BaseReduceNPrime, PrimeTree, BaseReduceTriple, BasePrefixes
- Template Method	- AbstractPrimeBaseGenerator.genBases()
- Command			- DefaultInit.action*() methods

## Misc items
- Manual check of some libraries against online Veracode vulnerability data.
	- https://sca.analysiscenter.veracode.com/vulnerability-database/search
		- Lombok ; clean
		- Infinispan ; potential security issues depending on use.
		- Eclipse Collections ; clean
		- Picocli ; clean

## Goals
- Experimentation with various graph libraries for data visualization
- Experiment with Picocli command line interface library
- Experiment with Eclipse collections
- Experiment with Java features in versions > Java 11
- Research properties of prime numbers
- Research data structures for use as primes numbers become very large
	- Track bases of primes (sums of primes that add to current prime)
	- Track and use shared lists or other structures of subsets of bases

## Reasons
Given a reasonably powerful desktop computer with 64Gb of memory; Some of the above goals can be done with several million primes and the related data *in memory*. A question is - can a dynamic mix of data representations allow processing significantly more data in memory efficiently? In the process of determining that, some interesting properties of primes may also be found.  Minimum goal would be 15 million primes + associated data with a stretch goal of probably 50-70 million.

Current processing on my i7 with 64Gb RAM reaches about 3-5 million (cmd line args of:  init --max-count=3000000 --log=NODESTRUCT) before system speed / stability / etc start to suffer. Graph visualizations slow once you get into the low thousands. Exporting and using other external tools (GML format) performed better for visualizations.

## Build requirements
The codebase uses Java 18.

## Execution - command line argument examples
- init --help

- init --max-count=100000 --export=GML
    - Outputs prime node/edge info as GML into a file in the ~/ptk/output folder.

- init --max-count=100 --base=THREETRIPLE --output=BASES
    - Output the prime, index, #bases(triples) and each triple.

- init --max-count=100 --load-primes  DEFAULT
	- Loads primes from files / zip-files
		- NOTE\: requires providing a path to the folder containing the files.
		-  1. You can symlink from default input folder to the example files in this project which are in the 'data' folder.
		-    mkdir \~/ptk/input-data;  ln -s \~/path-to-zip-file-folder/\* \~/ptk/input-data
		-  2. you can provide the CLI argument for the desired input data path with something like the following.
		-    --input-data-dir=~/dev/eclipse/primes-workspace/PrimeToolKit/data

- init --max-count=100 --base=PRIME_TREE --output=BASES   --use-base-file   --prefer-parallel=true
- init --max-count=100 --base=PREFIX     --output=BASES   --use-base-file   --prefer-parallel=true
    - Parallel gen then output prime and calculated prefix/tree for the prime; Prime = (Prefix or tree) + Prime[n-1]

- init --max-count=100 --base=NPRIME --output=BASES   --use-base-file   --prefer-parallel=true

Adding option:

You may need the following Java VM arguments depending on JDK version and setup/defaults for some variations/branches of the code;
- --add-exports java.base/java.lang=ALL-UNNAMED
- --add-exports java.desktop/sun.awt=ALL-UNNAMED
- --add-exports java.desktop/sun.java2d=ALL-UNNAMED


## Performance
Initial work searched for triples for each prime within the specified max range. This executed the search for each prime. I'm not sure why I didn't think about it initially but it is massively more efficient to simply iterate through all triples for the prime range and then simply assign any valid triple to its correct prime.

- init --max-count=750 --base=THREETRIPLE --output=THREETRIPLE --output-folder=~/ptk-out --stdout-redirect --use-base-file
The last record processed was: Prime [5693] idx[750] #-bases[17144].  Current code completes in 34 sec 771 ms (excluding output) while the original code took 7 min 17 sec. The output results file was 75,289,288 bytes in size.

For a command line of:
- init --max-count=25000 --output=BASES --base=NPRIME --max-reduce=4 --prefer-parallel=true --use-base-file

The original implementation ran for 4m 8s.
The current code now completes in 28 sec 73 ms.

## Observations
- My current prime/base selection method misses a few primes. The issue seems rooted in my data having a solution for the value after the currently desired item but not the current item. So when using prefixes, I can find the next prime but since the prefix which would produce the current prime hasn't been encountered yet it misses it and selects the next one which was found.  This seems like some sort of backtracking need. One thought involves looking at the order items within in a prefix and trying to determine if there is a relationship of some sort with the fact that certain sums of prefix values will be less than the previous prefix sum if using a straight sequential ordering of the items vs the resulting sum.  i.e. An algorithm may look at potential prefixes in an order such of {1}, {1,2}, {1,2,3}, {1,2,3,5}... {5} but because of that the {1,2,3} is encountered prior to prefixes with fewer members - resulting in missing a few values.  This is hard to describe in just a sentence or 2. Maybe breadth first data vs depth first data would be an adequate description for what seems like the need.
- When logging 3 million node structs with other settings set to default - meaning you get a target prime # and a set of bases which includes the previous prime plus some subset of lower primes that sum to the target prime. The largest value in the subset of small primes in each base is usually less than 23 from a quick look at the data.  Example output, Prime 49979681 bases [[1,2,3,5,7,49979663]]  <dist[6], nextPrime[49979687]> idx[2999999]
- When working with logging triples, the number of triples that all sum to the target prime starts to exceed the target prime # itself at around the 137th prime.  Partial output:

Prime [773] idx[137] #-bases[796]

	[241,263,269], [239,263,271], [233,269,271], [239,257,277], [233,263,277],
	[227,269,277], [241,251,281], [229,263,281], [223,269,281], [239,251,283],
	[233,257,283], [227,263,283], [239,241,293], [229,251,293], [223,257,293],
	[211,269,293], [199,281,293], [197,283,293], [227,239,307], [197,269,307],
	[173,293,307], [229,233,311], [223,239,311], [211,251,311], [199,263,311],
	[193,269,311], [191,271,311], [181,281,311], [179,283,311], [227,233,313],
	[197,263,313], [191,269,313], [179,281,313], [167,293,313], [149,311,313],
	[227,229,317], [223,233,317], [199,257,317], [193,263,317], [179,277,317],
	[173,283,317], [163,293,317], [149,307,317], [191,251,331], [179,263,331],
	[173,269,331], [149,293,331], [131,311,331], [197,239,337], [179,257,337],
	[173,263,337], [167,269,337], [199,227,347], [197,229,347], [193,233,347],
	[163,263,347], [157,269,347], [149,277,347], [113,313,347], [109,317,347],
	[89,337,347], [197,227,349], [191,233,349], [173,251,349], [167,257,349],
	[131,293,349], [113,311,349], [107,317,349], [197,223,353], [193,227,353],
	[191,229,353], [181,239,353], [179,241,353], [163,257,353], [157,263,353],
	[151,269,353], [149,271,353], [139,281,353], [137,283,353], [127,293,353],
	[113,307,353], [109,311,353], [107,313,353], [103,317,353], [89,331,353],
	[83,337,353], [73,347,353], [71,349,353], [191,223,359], [181,233,359],
	[173,241,359], [163,251,359], [157,257,359], [151,263,359], [137,277,359],
	[131,283,359], [107,307,359], [103,311,359], [101,313,359], [97,317,359],
	[83,331,359], [67,347,359], [61,353,359], [179,227,367], [173,233,367],
	[167,239,367], [149,257,367], [137,269,367], [113,293,367], [89,317,367],
	[59,347,367], [53,353,367], [47,359,367], [173,227,373], [167,233,373],
	[149,251,373], [137,263,373], [131,269,373], [107,293,373], [89,311,373],
	[83,317,373], [53,347,373], [47,353,373], [41,359,373], [167,227,379],
	[137,257,379], [131,263,379], [113,281,379], [101,293,379], [83,311,379],
	[47,347,379], [41,353,379], [193,197,383], [191,199,383], [179,211,383],
	[167,223,383], [163,227,383], [157,233,383], [151,239,383], [149,241,383],
	[139,251,383], [127,263,383], [113,277,383], [109,281,383], [107,283,383],
	[97,293,383], [83,307,383], [79,311,383], [73,317,383], [59,331,383],
	[53,337,383], [43,347,383], [41,349,383], [37,353,383], [31,359,383],
	[23,367,383], [17,373,383], [11,379,383], [191,193,389], [173,211,389],
	[157,227,389], [151,233,389], [127,257,389], [113,271,389], [107,277,389],
	[103,281,389], [101,283,389], [73,311,389], [71,313,389], [67,317,389],
	[53,331,389], [47,337,389], [37,347,389], [31,353,389], [17,367,389],
	[11,373,389], [5,379,389], [1,383,389], [179,197,397], [149,227,397],
	[137,239,397], [113,263,397], [107,269,397], [83,293,397], [59,317,397],
	[29,347,397], [23,353,397], [17,359,397], [3,373,397], [181,191,401],
	[179,193,401], [173,199,401], [149,223,401], [139,233,401], [131,241,401],
	[109,263,401], [103,269,401], [101,271,401], [89,283,401], [79,293,401],
	[61,311,401], [59,313,401], [41,331,401], [23,349,401], [19,353,401],
	[13,359,401], [5,367,401], [173,191,409], [167,197,409], [137,227,409],
	[131,233,409], [113,251,409], [107,257,409], [101,263,409], [83,281,409],
	[71,293,409], [53,311,409], [47,317,409], [17,347,409], [11,353,409],
	[5,359,409], [173,181,419], [163,191,419], [157,197,419], [131,223,419],
	[127,227,419], [113,241,419], [103,251,419], [97,257,419], [83,271,419],
	[73,281,419], [71,283,419], [61,293,419], [47,307,419], [43,311,419],
	[41,313,419], [37,317,419], [23,331,419], [17,337,419], [7,347,419],
	[5,349,419], [1,353,419], [173,179,421], [113,239,421], [101,251,421],
	[89,263,421], [83,269,421], [71,281,421], [59,293,421], [41,311,421],
	[5,347,421], [3,349,421], [163,179,431], [151,191,431], [149,193,431],
	[131,211,431], [113,229,431], [109,233,431], [103,239,431], [101,241,431],
	[79,263,431], [73,269,431], [71,271,431], [61,281,431], [59,283,431],
	[31,311,431], [29,313,431], [11,331,431], [5,337,431], [167,173,433],
	[149,191,433], [113,227,433], [107,233,433], [101,239,433], [89,251,433],
	[83,257,433], [71,269,433], [59,281,433], [47,293,433], [29,311,433],
	[23,317,433], [3,337,433], [137,197,439], [107,227,439], [101,233,439],
	[83,251,439], [71,263,439], [53,281,439], [41,293,439], [23,311,439],
	[17,317,439], [3,331,439], [163,167,443], [157,173,443], [151,179,443],
	[149,181,443], [139,191,443], [137,193,443], [131,199,443], [107,223,443],
	[103,227,443], [101,229,443], [97,233,443], [89,241,443], [79,251,443],
	[73,257,443], [67,263,443], [61,269,443], [59,271,443], [53,277,443],
	[47,283,443], [37,293,443], [23,307,443], [19,311,443], [17,313,443],
	[13,317,443], [157,167,449], [151,173,449], [131,193,449], [127,197,449],
	[113,211,449], [101,223,449], [97,227,449], [83,241,449], [73,251,449],
	[67,257,449], [61,263,449], [53,271,449], [47,277,449], [43,281,449],
	[41,283,449], [31,293,449], [17,307,449], [13,311,449], [11,313,449],
	[7,317,449], [149,167,457], [137,179,457], [89,227,457], [83,233,457],
	[59,257,457], [53,263,457], [47,269,457], [23,293,457], [5,311,457],
	[3,313,457], [149,163,461], [139,173,461], [131,181,461], [113,199,461],
	[101,211,461], [89,223,461], [83,229,461], [79,233,461], [73,239,461],
	[71,241,461], [61,251,461], [43,269,461], [41,271,461], [31,281,461],
	[29,283,461], [19,293,461], [5,307,461], [1,311,461], [137,173,463],
	[131,179,463], [113,197,463], [83,227,463], [71,239,463], [59,251,463],
	[53,257,463], [47,263,463], [41,269,463], [29,281,463], [17,293,463],
	[3,307,463], [149,157,467], [139,167,467], [127,179,467], [113,193,467],
	[109,197,467], [107,199,467], [83,223,467], [79,227,467], [73,233,467],
	[67,239,467], [43,263,467], [37,269,467], [29,277,467], [23,283,467],
	[13,293,467], [137,157,479], [131,163,479], [127,167,479], [113,181,479],
	[103,191,479], [101,193,479], [97,197,479], [83,211,479], [71,223,479],
	[67,227,479], [61,233,479], [53,241,479], [43,251,479], [37,257,479],
	[31,263,479], [23,271,479], [17,277,479], [13,281,479], [11,283,479],
	[1,293,479], [137,149,487], [113,173,487], [107,179,487], [89,197,487],
	[59,227,487], [53,233,487], [47,239,487], [29,257,487], [23,263,487],
	[17,269,487], [5,281,487], [3,283,487], [131,151,491], [109,173,491],
	[103,179,491], [101,181,491], [89,193,491], [83,199,491], [71,211,491],
	[59,223,491], [53,229,491], [43,239,491], [41,241,491], [31,251,491],
	[19,263,491], [13,269,491], [11,271,491], [5,277,491], [1,281,491],
	[107,167,499], [101,173,499], [83,191,499], [47,227,499], [41,233,499],
	[23,251,499], [17,257,499], [11,263,499], [5,269,499], [3,271,499],
	[131,139,503], [113,157,503], [107,163,503], [103,167,503], [97,173,503],
	[89,181,503], [79,191,503], [73,197,503], [71,199,503], [59,211,503],
	[47,223,503], [43,227,503], [41,229,503], [37,233,503], [31,239,503],
	[29,241,503], [19,251,503], [13,257,503], [7,263,503], [1,269,503],
	[127,137,509], [113,151,509], [107,157,509], [101,163,509], [97,167,509],
	[83,181,509], [73,191,509], [71,193,509], [67,197,509], [53,211,509],
	[41,223,509], [37,227,509], [31,233,509], [23,241,509], [13,251,509],
	[7,257,509], [1,263,509], [113,139,521], [103,149,521], [101,151,521],
	[89,163,521], [79,173,521], [73,179,521], [71,181,521], [61,191,521],
	[59,193,521], [53,199,521], [41,211,521], [29,223,521], [23,229,521],
	[19,233,521], [13,239,521], [11,241,521], [1,251,521], [113,137,523],
	[101,149,523], [83,167,523], [71,179,523], [59,191,523], [53,197,523],
	[23,227,523], [17,233,523], [11,239,523], [101,131,541], [83,149,541],
	[59,173,541], [53,179,541], [41,191,541], [5,227,541], [3,229,541],
	[89,137,547], [59,167,547], [53,173,547], [47,179,547], [29,197,547],
	[3,223,547], [107,109,557], [103,113,557], [89,127,557], [79,137,557],
	[67,149,557], [59,157,557], [53,163,557], [43,173,557], [37,179,557],
	[23,193,557], [19,197,557], [17,199,557], [5,211,557], [103,107,563],
	[101,109,563], [97,113,563], [83,127,563], [79,131,563], [73,137,563],
	[71,139,563], [61,149,563], [59,151,563], [53,157,563], [47,163,563],
	[43,167,563], [37,173,563], [31,179,563], [29,181,563], [19,191,563],
	[17,193,563], [13,197,563], [11,199,563], [101,103,569], [97,107,569],
	[73,131,569], [67,137,569], [53,151,569], [47,157,569], [41,163,569],
	[37,167,569], [31,173,569], [23,181,569], [13,191,569], [11,193,569],
	[7,197,569], [5,199,569], [89,113,571], [71,131,571], [53,149,571],
	[29,173,571], [23,179,571], [11,191,571], [5,197,571], [3,199,571],
	[89,107,577], [83,113,577], [59,137,577], [47,149,577], [29,167,577],
	[23,173,577], [17,179,577], [5,191,577], [3,193,577], [89,97,587],
	[83,103,587], [79,107,587], [73,113,587], [59,127,587], [47,139,587],
	[37,149,587], [29,157,587], [23,163,587], [19,167,587], [13,173,587],
	[7,179,587], [5,181,587], [83,97,593], [79,101,593], [73,107,593],
	[71,109,593], [67,113,593], [53,127,593], [43,137,593], [41,139,593],
	[31,149,593], [29,151,593], [23,157,593], [17,163,593], [13,167,593],
	[7,173,593], [1,179,593], [73,101,599], [71,103,599], [67,107,599],
	[61,113,599], [47,127,599], [43,131,599], [37,137,599], [23,151,599],
	[17,157,599], [11,163,599], [7,167,599], [1,173,599], [83,89,601],
	[71,101,601], [59,113,601], [41,131,601], [23,149,601], [5,167,601],
	[59,107,607], [53,113,607], [29,137,607], [17,149,607], [3,163,607],
	[71,89,613], [59,101,613], [53,107,613], [47,113,613], [29,131,613],
	[23,137,613], [11,149,613], [3,157,613], [73,83,617], [67,89,617],
	[59,97,617], [53,103,617], [47,109,617], [43,113,617], [29,127,617],
	[19,137,617], [17,139,617], [7,149,617], [5,151,617], [71,83,619],
	[53,101,619], [47,107,619], [41,113,619], [23,131,619], [17,137,619],
	[5,149,619], [3,151,619], [59,83,631], [53,89,631], [41,101,631],
	[29,113,631], [11,131,631], [5,137,631], [3,139,631], [61,71,641],
	[59,73,641], [53,79,641], [43,89,641], [31,101,641], [29,103,641],
	[23,109,641], [19,113,641], [5,127,641], [1,131,641], [59,71,643],
	[47,83,643], [41,89,643], [29,101,643], [23,107,643], [17,113,643],
	[3,127,643], [59,67,647], [53,73,647], [47,79,647], [43,83,647],
	[37,89,647], [29,97,647], [23,103,647], [19,107,647], [17,109,647],
	[13,113,647], [59,61,653], [53,67,653], [47,73,653], [41,79,653],
	[37,83,653], [31,89,653], [23,97,653], [19,101,653], [17,103,653],
	[13,107,653], [11,109,653], [7,113,653], [53,61,659], [47,67,659],
	[43,71,659], [41,73,659], [31,83,659], [17,97,659], [13,101,659],
	[11,103,659], [7,107,659], [5,109,659], [1,113,659], [53,59,661],
	[41,71,661], [29,83,661], [23,89,661], [11,101,661], [5,107,661],
	[3,109,661], [47,53,673], [41,59,673], [29,71,673], [17,83,673],
	[11,89,673], [3,97,673], [43,53,677], [37,59,677], [29,67,677],
	[23,73,677], [17,79,677], [13,83,677], [7,89,677], [43,47,683],
	[37,53,683], [31,59,683], [29,61,683], [23,67,683], [19,71,683],
	[17,73,683], [11,79,683], [7,83,683], [1,89,683], [29,53,691],
	[23,59,691], [11,71,691], [3,79,691], [31,41,701], [29,43,701],
	[19,53,701], [13,59,701], [11,61,701], [5,67,701], [1,71,701],
	[23,41,709], [17,47,709], [11,53,709], [5,59,709], [3,61,709],
	[23,31,719], [17,37,719], [13,41,719], [11,43,719], [7,47,719],
	[1,53,719], [17,29,727], [5,41,727], [3,43,727], [17,23,733],
	[11,29,733], [3,37,733], [11,23,739], [5,29,739], [3,31,739],
	[13,17,743], [11,19,743], [7,23,743], [1,29,743], [5,17,751],
	[3,19,751], [5,11,757], [3,13,757], [5,7,761], [1,11,761],
	[1,3,769]

- For N-base reductions - Example output:

Prime [1583] idx[250]

	[base-prime-1 count:[155],base-prime-2 count:[318],base-prime-3 count:[264]]

which is interpreted as: Prime value 1583 is represented by: 1 x 155 + 2 x 318 + 3 x 264

## Design
The current design is extensible to a degree. Primes can have bases generated via different methodologies, etc - such as where Prime N is the sum of Prime N-1 + some small set of primes in the range Prime 1 to N-2. So a base can be a set of primes summed. Primes can also be represented as 2 parts where one part is Prime[n-1] and the other part is a reference to collection of primes (aka a prefix or tree of primes) and both parts are summed.

Currently, I'm mostly using the 2-part representation now and maintaining unique sets/trees of primes.

As the initial design used sequential processes; when I started to add in support for more parallel/concurrent processing I should have immediately reviewed my data structures to identify any potential issues. I ended up using Eclipse collections which were not always faster data structures but they did reduce memory consumption which allowed scaling to larger sets of primes. I also used the concurrent versions as needed to prevent race conditions.


## Implementation
- Command line parsing is handled using the picocli library and the resulting options processed; resulting in
adding a Consumer<> instance to a list of actions for each pertinent action.  Once the command line args
are processed, each of the Consumer<> objects is evaluated.  The first actions generate the default prime
information and then new base generation, logging and graphing actions are added as needed.

## Challenges
- The internal algorithm for generating the "next prime" has a flaw where a prime is skipped once in a while. This is part of the reason that caching plus loading of 50 million known primes is implemented - which allows comparison with known primes to enable identification of missed primes. Of course, this only works when you are working within the limits of the known primes that are loaded.

## ToDo
	- Increase test coverage.
	- Additional metrics.
	- Improved cache support.
	- More generic / useful methods for identifying patterns in the bases/data.
	- Enable more metric reporting options (like Graphite).
	- More remote command support.
	- Improved reporting / visualization.