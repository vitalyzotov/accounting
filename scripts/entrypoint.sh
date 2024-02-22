#!/bin/bash

storepass="changeit"
cert_dir=/cacerts

for cert_file in $cert_dir/*.crt; do
    alias=$(basename $cert_file .crt)
    if ! $JAVA_HOME/bin/keytool -list -cacerts -storepass $storepass -alias $alias > /dev/null 2>&1; then
        echo "Installing certificate $alias"
        $JAVA_HOME/bin/keytool -importcert -storepass $storepass -noprompt -alias $alias -cacerts -trustcacerts -file $cert_file
    fi
done

java ${JAVA_OPTS} -XshowSettings:vm -XX:MaxRAMFraction=1 -XX:MaxRAMPercentage=80.0 -XX:+PrintFlagsFinal -cp "app:app/lib/*" ru.vzotov.accounting.Application