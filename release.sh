#!/bin/bash
set -e

cd `dirname $0`
INTERIM="target/interim"

lein release
$VISUAL README.md
lein repack split

for dir in `ls -tr $INTERIM/branches/`
do
	cd $INTERIM/branches/$dir
	lein deploy
	cd -
done

cd $INTERIM/root
lein deploy
