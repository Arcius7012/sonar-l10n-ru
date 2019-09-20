#! /bin/bash

set -e
source ./tags.sh

for ((idx=0; idx<${#tags[@]}; ++idx)); do
    echo "Check tag ${tags[idx]}"
    mvn test "-Dsonarqube.version=${tags[idx]}" || true
    cp ./build/l10n/core_ru.properties.report.txt ./compare/${tags[idx]}_core_ru.properties.report.txt
done
