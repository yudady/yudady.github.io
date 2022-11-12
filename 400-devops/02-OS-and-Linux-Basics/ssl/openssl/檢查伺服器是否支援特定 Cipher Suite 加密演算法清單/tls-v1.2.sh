SUPPORTED_CIPHERS=$(openssl ciphers -s -tls1_2)
IFS=':' read -r -a SUPPORTED_CIPHERS <<< $SUPPORTED_CIPHERS
for c in "${SUPPORTED_CIPHERS[@]}"
do
    printf -v pad %30s
    cipher=$c$pad
    printf "Checking ${cipher:0:30} ... "
    curl -s -S -o /dev/null --no-progress-meter --tls-max 1.2 --ciphers $c https://www.google.com
    if [ $? -eq 0 ]; then
        echo OK
    fi
done