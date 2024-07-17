
use the other branch 

# **Text Compression and Decompression Using Clojure**

**What is the Project About**

This project aims to practice functional programming using the Clojure programming language. The task involves developing an application that allows users to compress and decompress simple text files. The application features a menu with options for manipulating and displaying input, making it convenient for users to evaluate the accuracy of the solution.

-------------------------------------------------------------------------------------------------------------------------------------------------

**Tools and Technologies Used:**

  - Programming Languages: Clojure
  - Libraries and Functions: file-seq, slurp, spit, clojure.string, zipmap
  - Functional Programming Concepts: Map, Reduce, Filter, Apply

-------------------------------------------------------------------------------------------------------------------------------------------------

**Solution Explanation:**

Menu Options:
  i. Display List of Files:
      - Uses the file-seq function to list files in the current directory.
  ii. Display File Contents:
      - Prompts the user for a file name, reads the file using slurp, and prints its content.
  iii. Compress a File:
      - Compresses a text file by converting words to numbers based on their frequency in the English language.
      - Frequencies are obtained from a provided frequency.txt file containing the 10,000 most common words.
  iv. Decompress a File:
      - Decompresses a previously compressed file and displays the original text on the screen.
      - Handles words not found in the frequency list by keeping them in their original form.
      
Task Details:

  i. File Listing:
      - Implement a function to list all files in the current directory using file-seq.
      - Ensure the menu displays the file names correctly.

  ii. Displaying File Contents:
      - Implement a function to read and display the contents of a specified file using slurp.
      - Include basic error checking to handle invalid file names.
      
  iii. File Compression:
      - Read the frequency.txt file to map words to their frequency-based numeric labels.
      - Convert the text of a given file into a compressed format where words are replaced by their numeric labels.
      - Handle words not in the frequency list by keeping them as is in the compressed file.
      - Write the compressed content to a new file using spit.
      
  iv. File Decompression:
      - Read a compressed file and convert it back to its original text form.
      - Ensure the decompressed text maintains proper punctuation and formatting.

-------------------------------------------------------------------------------------------------------------------------------------------------

**Result**

The project successfully demonstrates the implementation of a functional programming approach to compress and decompress text files using Clojure.

Key outcomes include:
1. A menu-driven application for easy navigation and evaluation of the implemented functions.
2. Correct listing of files in the current directory.
3. Accurate display of file contents for user-specified files.
4. Successful compression of text files based on word frequency.
5. Accurate decompression of files to restore the original text, including proper handling of punctuation and formatting.
6. Robust error checking for file names and menu selections.
