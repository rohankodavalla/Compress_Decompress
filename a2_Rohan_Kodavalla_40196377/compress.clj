(ns compress
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))


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
    ;;(println "Frequency Map:")
    ;;(println frequency-map)
    (with-open [file-writer (clojure.java.io/writer "frequency.txt.ct")]
        (doseq [[key value] frequency-map]
            (.write file-writer (str key " " value "\n"))))

    ;;(println "Words:")
    ;;(println words)
    ;;(println "Compressed Words:")
    ;;(prn compressed-words)
    ;;(println "Compressed Content:")
    ;;(println compressed-content)
    (spit compressed-file-path compressed-content)
    (println "File compressed successfully.")
    compressed-file-path))


(defn decompress-file [file-path frequency-file]
  (let [frequency-map (-> frequency-file load-frequency-map)
        reversed-frequency-map (zipmap (vals frequency-map) (keys frequency-map))
        compressed-content (slurp file-path)
        index-words (str/split compressed-content #"\s+")
        decompressed-words (map #(if (re-matches #"^\d+$" %)
                                  (or (get reversed-frequency-map (Integer/parseInt %)) "")
                                  %)
                                index-words)

        decompressed-words1 (map #(-> %
                                   (str/replace #"\s+(?=[,\]\)\?\.])" "") ; Remove spaces before ",", "]", ")", "?", and "."
                                   (str/replace #"(?<=[\@])\d(?=[\@])" "") ; Remove spaces after "@"xyz"@"
                                   (str/replace #"(?<=[\[\(\@])\s+" "")) ; Remove spaces after "[" and "(","@"
                              decompressed-words)
        formatted-words (map-indexed (fn [idx word]
                                       (if (or (= idx 0) (re-matches #"[.!?]" (nth decompressed-words (- idx 1))))
                                         (clojure.string/capitalize word)
                                         word))
                                     decompressed-words1)
        formatted-words (map #(str/replace % #"\s?,\s?" ", ") formatted-words)
        formatted-content (clojure.string/join " " formatted-words)]
    formatted-content


    
    ;;(println "Formatted Words:")
    ;;(println formatted-words)
    
    (println "Decompressed File Contents:")
    (println formatted-content)))

;;(decompress-file "t3.txt.ct" "frequency.txt")


;; >>> The experienced 372, named Oz, representing the @416@148 code - and his (2147) assistant - are not in the suggested @732 [250, 230]. is that actually the correct 294?
(defn decompress-file4 [file-path frequency-file]
  (let [frequency-map (-> frequency-file load-frequency-map)
        reversed-frequency-map (zipmap (vals frequency-map) (keys frequency-map))
        compressed-content (slurp file-path)
        formatted-content (-> compressed-content
                            ;;(clojure.string/replace #"(?<=\w\s*)[.,?!]" "$0 ")
                            
                            (str/replace #"\s+(?=[,\]\)\?\.])" "") ; Remove spaces before ",", "]", and ")"
                            (str/replace #"(?<=[\@])\d(?=[\@])" "") ; Remove spaces after ",", "]", and ")"
                            (str/replace #"(?<=[\[\(\@])\s+" "")) ; Remove spaces after "[","(","@"

        index-words (str/split formatted-content #"\s+")
        decompressed-words (map #(if (re-matches #"^\d+$" %)
                                  (or (get reversed-frequency-map (Integer/parseInt %)) "")
                                  %)
                                index-words)
        formatted-words (map-indexed (fn [idx word]
                                       (if (or (= idx 0) (re-matches #"[.!?]" (nth decompressed-words (- idx 1))))
                                         (str/capitalize word)
                                         word))
                                     decompressed-words)]

   ;; (println compressed-content)
   ;; (println "formatted-content")
    ;;(println formatted-content )
    (println "Decompressed File Contents:")
    (println (str/join " " formatted-words))))

;;(decompress-file "t3.txt.ct" "frequency.txt")