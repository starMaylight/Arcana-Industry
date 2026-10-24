# Arcana Industry

Minecraft 1.20.1 / Forge 47.4.10 / Java 17 向けの工業・魔術MOD。
JEIは任意です。

## ビルド
Java 17とGradle 8.8を用意し、リポジトリ直下で実行します。

```sh
gradle --no-daemon clean build
```

生成先: `build/libs/arcana_industry-1.0.jar`。
バージョンの変更は `-Pmod_version=1.1.0` で指定できます。

## リリース
このフォルダをGitHubリポジトリのルートとして登録してください。
ソースと `.github/workflows/release.yml` をコミット・pushしてから、タグを送信します。

```sh
git tag v1.0
git push origin v1.0
```

ActionsがJava 17 / Gradle 8.8でビルドし、タグから先頭のvを除いたバージョンを
JAR名とMOD情報に設定して、GitHub ReleasesへJARとSHA256SUMS.txtを添付します。
`v1.1.0-beta.1` のようなタグはプレリリースになります。
手動実行時も対象にはバージョンタグを選んでください。既存リリースは上書きしません。
追加の個人アクセストークンは不要です。リポジトリでGitHub Actionsを有効にしてください。

## ライセンス
MIT。詳細は LICENSE を参照してください。
