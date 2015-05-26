(ns pdfboxing.merge
  (:require [pdfboxing.common :as common])
  (:import [org.apache.pdfbox.util PDFMergerUtility]
           [org.apache.pdfbox.pdmodel PDDocument]
           [java.io File FileInputStream]))

(defn throw-exception
  [message]
  (throw (IllegalArgumentException. message)))

(defn check-if-present
  "Check if the input & output file names where supplied"
  [input output]
  (if (some true? (map empty? [input output]))
    (throw-exception "argument can't be empty")))

(defn check-for-pdfs
  "Check if all the files supplied are actual PDFs."
  [files]
  (if (some false? (map common/is-pdf? files))
    (throw-exception "the files supplied need to be PDFs")
    true))

(defn arg-check [output input]
  (check-if-present input output)
  (if (sequential? input)
    (check-for-pdfs input)
    (throw-exception "input - needs to be sequential")))

(defn merge-pdfs
  "merge multiple PDFs into output file"
  [& {:keys [output input]}]
  {:pre [(arg-check output input)]}
  (let [merger (PDFMergerUtility.)]
      (doseq [f input]
        (.addSource merger (FileInputStream. (File. f))))
      (.setDestinationFileName merger output)
      (.mergeDocuments merger)))

(defn merge-pddocuments
  "merge multiple PDDocuments into a single PDDocument"
  [& {:keys [input]}]
  (let [merger (PDFMergerUtility.)
        destination (PDDocument.)]
    (doseq [d input]
      (.appendDocument merger destination d)
      (.close d))
    destination))

(defn pddocuments->pdf
  [& {:keys [output input]}]
  (let [destination (merge-pddocuments :input input)]
    (.save destination output)
    (.close destination)))
