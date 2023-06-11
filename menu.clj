(ns testt.menu
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))


;; Step 1: Read the frequency map and assign values to words
(defn create-frequency-map [frequency-file]
  (with-open [file-reader (clojure.java.io/reader frequency-file)]
    (let [lines (line-seq file-reader)]
      (zipmap (mapv keyword (range)) lines))))

;; Step 2: Compress the text file
(defn compress-file [file frequency-map compressed-file]
  (with-open [input-file (clojure.java.io/reader file)
              output-file (clojure.java.io/writer compressed-file)]
    (let [words (clojure.string/split (clojure.string/lower-case (slurp input-file)) #"[\s.,!?]+")]
      (doseq [word words]
        (let [value (frequency-map word)]
          (when value
            (spit output-file (str value " ") :append true)))))))

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


(defn load-frequency-map [file-path]
  (with-open [reader (io/reader file-path)]
    (let [compressed-content (clojure.string/split-lines (slurp reader))
          words (->> compressed-content
                     ;;(map #(clojure.string/split % #"(?<=\D)(?=\d)|(?<=\d)(?=\D)|(?<=\W)(?=\w)|(?<=\w)(?=\W)"))
                     ;;(flatten)
                     (mapcat #(clojure.string/split % #"\s+"))
                     (mapv clojure.string/lower-case))
          unique-words (distinct words)]
          ;;processed-words (map #(if (re-matches #"\d+" %)
            ;;                     (if (re-matches #"\@.*\@" %)
              ;;                     %  ; Already contains "@" symbols, return as is
                ;;                   (str "@" % "@"))  ; Add "@" symbols
                  ;;               %)
                    ;;           unique-words)]
      (zipmap unique-words (range (count unique-words))))))




(defn get-word-index-map [words frequency-map]
  (let [seen-words (volatile! #{})
        index-map (atom {})]
    (map #(if (contains? @seen-words %)
            (get @index-map %)
            (do
              (vswap! seen-words conj %)
              (let [value (or (get frequency-map (clojure.string/lower-case %)) %)]
                (swap! index-map assoc % value)
                value)))
         words)))


;;(defn get-word-index-map [words frequency-map]
  ;;(map #(or (get frequency-map (clojure.string/lower-case %)) %) words))

(defn compress-file [file-path frequency-file]
  (let [frequency-map (load-frequency-map frequency-file)
                words (-> file-path
                  slurp
                  ;;  msut work >>(str/replace #"(?<=\D)(?=\d)|(?<=\d)(?=\D)|(?<=\W)(?=\w)|(?<=\w)(?=\W)" " $0 ")
                  (str/replace #"[\W]" " $0 ")
                  (str/split #"\s+")
                  )
        compressed-words (mapv #(if (re-matches #"^\d+$" %)
                                 (str "@" % "@")
                                 %)
                               words)
        compressed-words (get-word-index-map compressed-words frequency-map)
        compressed-content (clojure.string/join " " compressed-words)
        compressed-file-path (str file-path ".ct")]
    (println "Frequency Map:")
    (prn frequency-map)
    (with-open [file-writer (clojure.java.io/writer "frequency.txt.ct")]
        (doseq [[key value] frequency-map]
            (.write file-writer (str key " " value "\n"))))

    (println "Words:")
    (prn words)
    (println "Compressed Words:")
    (prn compressed-words)
    (println "Compressed Content:")
    (println compressed-content)
    (spit compressed-file-path compressed-content)
    (println "File compressed successfully.")
    compressed-file-path))


(defn decompress-file1 [file-path frequency-file]
  (let [frequency-map (-> frequency-file load-frequency-map)
        reversed-frequency-map (zipmap (vals frequency-map) (keys frequency-map))
        compressed-content (slurp file-path)
        decompressed-words (map #(or (get reversed-frequency-map %) %) (str/split compressed-content #"[\s]+"))
        decompressed-content (str/join " " decompressed-words)
        decompressed-file-path (str file-path ".txt")]
    (println "Decompressed File Contents:")
    (println decompressed-content)
    (spit decompressed-file-path decompressed-content)
    (println "File decompressed successfully.")
    decompressed-file-path))

;;; below is where we left off 
(defn decompress-file2 [file-path frequency-file]
  (let [frequency-map (-> frequency-file load-frequency-map)
        reversed-frequency-map (zipmap (vals frequency-map) (keys frequency-map))
        compressed-content (slurp file-path)
        index-words (map #(or (get reversed-frequency-map %) %) (str/split compressed-content #"[\s]+"))
        decompressed-words (map #(if (re-matches #"^@\d+@$" %)
                                 (subs % 1 (dec (count %))) ; Remove "@" symbols
                                 %)
                               index-words)
        decompressed-content (str/join " " decompressed-words)
        decompressed-file-path (str file-path ".txt")]
    (println "Decompressed File Contents:")
    (println decompressed-content)
    (spit decompressed-file-path decompressed-content)
    (println "File decompressed successfully.")
    decompressed-file-path))

(defn decompress-file [file-path frequency-file]
  (let [frequency-map (-> frequency-file load-frequency-map)
        reversed-frequency-map (zipmap (vals frequency-map) (keys frequency-map))
        compressed-content (slurp file-path)
        index-words (str/split compressed-content #"\s+")
        decompressed-words (map #(if (re-matches #"^\d+$" %)
                                  (or (get reversed-frequency-map (Integer/parseInt %)) %)
                                  %)
                                index-words)
        decompressed-content (clojure.string/join " " decompressed-words)
        decompressed-file-path (str file-path ".txt")]
    (println "Decompressed File Contents:")
    (println decompressed-content)
    (spit decompressed-file-path decompressed-content)
    (println "File decompressed successfully.")
    decompressed-file-path))


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
                           compressed-file (compress-file file-path frequency-file)]
                       (println "Compressed file saved as" compressed-file))
                     :continue)
    (= option "4") (do
                     (println "Enter file path to decompress:")
                     (let [file-path (read-line)
                           frequency-file "frequency.txt"
                           decompressed-file (decompress-file file-path frequency-file)]
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