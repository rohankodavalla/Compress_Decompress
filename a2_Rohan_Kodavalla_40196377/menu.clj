(ns testt.menu
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [compress :as compress-ns]))


(defn create-frequency-map [frequency-file]
  (with-open [file-reader (clojure.java.io/reader frequency-file)]
    (let [lines (line-seq file-reader)]
      (zipmap (mapv keyword (range)) lines))))



(defn display-file-list []
  (println "File List:")
  (doseq [file (file-seq (io/file "."))]
    (println "* " (.getPath file))))

(defn display-file-contents [file-path]
  (if (.exists (io/file file-path))
    (let [content (slurp file-path)]
      (println "")
      (println "File Contents:")
      (println "")
      (println content))
    (println "File does not exist.")))

(defn process-menu-option [option]
  (cond
    (= option "1") (do
                     (println "")
                     (display-file-list)
                     :continue)
    (= option "2") (do
                     (println "")
                     (print "Enter file path to display contents: ")
                     (println "")
                     (let [file-path (read-line)]
                       (display-file-contents file-path))
                     :continue)
    (= option "3") (do
                     (println "")
                     (println "Enter file path to compress:")
                     (println "")
                     (let [file-path (read-line)
                           frequency-file "frequency.txt"
                           compressed-file (compress-ns/compress-file file-path frequency-file)]
                       (println "Compressed file saved as" compressed-file))
                     :continue)
    (= option "4") (do
                     (println "Enter file path to decompress:")
                     (let [file-path (read-line)
                           frequency-file "frequency.txt"
                           decompressed-file (compress-ns/decompress-file file-path frequency-file)]
                       (println "Decompressed file saved as" decompressed-file))
                     :continue)
    (= option "5") (do
                     (println "Bye...")
                     :exit)
    :else (do
            (println "Invalid option. Please try again.")
            :continue)))

(defn menu-loop []
  (loop []
    (println "")
    (println "********* Compression Menu ***********:")
    (println "---------------------------------------")
    (println "")
    (println "1. Display File List")
    (println "2. Display File Contents")
    (println "3. Compress a File")
    (println "4. Decompress a File")
    (println "5. Exit")
    (println "")
    (print "Enter your option: ")
    (println "")
    (let [option (read-line)]
      (let [result (process-menu-option option)]
        (if (= result :continue)
          (recur)
          (println "Exiting..."))))))

(menu-loop)