#!/bin/bash

MYSQL_HOST=mysql
MYSQL_PORT=3306
DB_USER=root
DB_PASSWORD=${MYSQL_ROOT_PASSWORD}

INIT_DATABASES=${INIT_DATABASES:-}

export MYSQL_HOST MYSQL_PORT DB_USER DB_PASSWORD

generate_init_database_sql() {
    db_file=$1
    dbs=$2
    echo >$db_file

    if [ "$dbs" == "" ]; then
        echo "No database, exit"
        exit 1
    fi

    echo "Generate init database file $db_file for db $dbs"

    db_array=$(echo $dbs|tr "," "\n")
    for db in $db_array; do
        echo "CREATE DATABASE IF NOT EXISTS \`$db\` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_bin;" >> $db_file
    done

    echo "--------------------------------------------------------------------"
    cat $db_file
    echo "--------------------------------------------------------------------"
}

execute_init_script() {
    for f in /docker-entrypoint-initdb.d/*.sql; do
        echo "Execute SQL script $f"
        mysql -h${MYSQL_HOST} -P${MYSQL_PORT} -u${DB_USER} -p${DB_PASSWORD} < "$f"
        if [ $? -ne 0 ]; then
            exit 1
        fi
    done
}

main0() {
    mkdir -p /docker-entrypoint-initdb.d/
    generate_init_database_sql /docker-entrypoint-initdb.d/0-init_database.sql $INIT_DATABASES

    execute_init_script

    echo "Start crond"
    crond -f
}

main0
