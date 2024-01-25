# !/usr/bin/bash
FLAGS="--sudo --echo --cleanup --logSuspect --acceptEstablishedRelated"
bash ./knight.sh $FLAGS --input "$1" > ./rules.sh
bash ./rules.sh
