# Clj-freeq

A free text expression based filter engine

## Publish to local mvn repository
1. Firstly make sure that you have mvn installed. By default, mvn will download repository to your ~/.m2/repository. The following command will publish clj-freeq into your local mvn repository.

2.  Build jar file with the following command

    ```bash
    lein jar
    ```

3. Install clj-freeq jar to local mvn repository

    ```bash
    mvn install:install-file -Dfile=target/clj-freeq-0.0.1-SNAPSHOT.jar -DartifactId=clj-freeq -Dversion=0.0.1-SNAPSHOT -DgroupId=clj-freeq -Dpackaging=jar -DlocalRepositoryPath=<absolute-path-to-local-repo>
    ```

4. In your project.clj, add the following option:

    ```clojure
    :repositories \[["local" ~(str (.toURI (java.io.File. "<absolute-path-to-local-repo>")))]\]
    ```
5. You now should be able to add clj-freeq to your class path by doing

   ```bash
   lein deps
   ```

## Usage
   * require 'clj-freeq.core into your clj file
   * call create-filter to create a filter function and pass the map which should align with keys in expression as parameter

## License
   To be added
