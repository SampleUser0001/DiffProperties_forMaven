# DiffProperties_forMaven
DiffPropertiesのMaven版

## 実行

### 例

```
.
├── input
│   ├── env1
│   │   └── sample.properties
│   └── env2
│       └── sample.properties
└── output
    └── output.tsv
```

### 実行コマンド

input配下ディレクトリに置かれている、sample.propertiesの差分を取得する。  
結果はoutput/output.tsvに出力する。

``` bash
mvn clean compile exec:java -Dexec.mainClass="tool.diffproperties.DiffProperties" -Dexec.args="'`pwd`/sample/input/' 'sample.properties' '`pwd`/sample/output/output.tsv'"
```