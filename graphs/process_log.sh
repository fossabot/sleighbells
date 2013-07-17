OUTPUT_FILE=
ROOT_DIR=..
APP_KEY=
MASTER_SECRET=
grep "got id" $OUTPUT_FILE | sed -r 's/.+sent message (.+) got id (.+) at (.+) to \((.+),(.+)\)/\1,\2,\3,\4,\5/' > push_ids.csv
java -cp $ROOT_DIR/resources:$ROOT_DIR/target/sleighbells-1.0-SNAPSHOT-shaded.jar -Dua.ab.appKey=$APP_KEY -Dua.ab.masterSecret=$MASTER_SECRET  com.urbanairship.sleighbells.notactuallytests.graphs.StatsForCsv push_ids.csv > hourly.csv
Rscript gr.R hourly.csv
