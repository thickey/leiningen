(ns leiningen.test.pom
  (:use [clojure.test]
        [clojure.java.io :only [file delete-file]]
        [leiningen.pom :only [make-pom pom]]
        [leiningen.test.helper :only [sample-project]]))

(deftest test-pom-file-is-created
  (let [pom-file (file (:target-path sample-project) "pom.xml")]
    (delete-file pom-file true)
    (pom sample-project)
    (is (.exists pom-file))))

(deftest test-pom-has-classifier-when-defined
  (let [pom (make-pom sample-project)]
    (is (not (re-find #"classifier" pom))))
  (let [altered-meta (assoc-in (meta sample-project)
                               [:without-profiles :classifier]
                               "stuff")
        pom (make-pom (with-meta sample-project altered-meta))]
    (is (re-find #"<classifier>stuff</classifier>" pom))))

(deftest test-pom-tries-to-pprint
  (is (re-find #"(?m)^\s+<groupId>nomnomnom</groupId>$"
               (make-pom sample-project))))


(deftest test-snapshot-checking
  (binding [leiningen.core.main/*exit-process?* false]
    (let [project (assoc sample-project :version "1.0"
                         :dependencies [['clojure "1.0.0-SNAPSHOT"]])]
      (is (not (pom project))))))
